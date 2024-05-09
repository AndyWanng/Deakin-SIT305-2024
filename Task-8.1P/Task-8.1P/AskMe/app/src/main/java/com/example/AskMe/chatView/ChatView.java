package com.example.AskMe.chatView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AskMe.MainActivity;
import com.example.AskMe.R;
import com.example.AskMe.SpeechToText;
import com.example.AskMe.model.Msg;
import com.example.AskMe.utils.OkhttpUtil;
import com.example.AskMe.utils.SqlOperate;
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatView {

    private SqlOperate sqlOperate;
    private List<Msg> chatList;
    private ChatViewAdapter chatViewAdapter;
    private RecyclerView recyclerView;
    private long conversationId;

    public ChatView(long conversationId) {
        this.conversationId = conversationId;
        chatList = new ArrayList<>();
    }

    public List<Msg> getChatList() {
        return chatList;
    }

    public View Load(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.activity_chat, null);

        sqlOperate = new SqlOperate();
        initChatList(context);

        recyclerView = chatView.findViewById(R.id.chat_recycler_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        chatViewAdapter = new ChatViewAdapter(chatList);
        recyclerView.setAdapter(chatViewAdapter);

        SpeechToText speechToText = new SpeechToText(chatView.findViewById(R.id.search_input), chatView.findViewById(R.id.bt_voice_input), this);
        chatView.findViewById(R.id.bt_voice_input).setOnClickListener(v -> speechToText.checkPermissionAndStartSpeechRecognition(MainActivity.Instance));

        TextInputEditText textInput = chatView.findViewById(R.id.search_input);
        chatView.findViewById(R.id.bt_send).setOnClickListener(v -> sendMessage(textInput));
        textInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                sendMessage(textInput);
                return true;
            }
            return false;
        });

        chatView.findViewById(R.id.btn_delete_chat).setOnClickListener(v -> showDeleteConfirmationDialog());
        updateAndScroll();
        return chatView;
    }

    private void initChatList(Context context) {
        chatList.clear();
        Cursor cursor = sqlOperate.getMessages(conversationId);
        if (cursor != null) {
            try {
                int typeIndex = cursor.getColumnIndex("type");
                int contentIndex = cursor.getColumnIndex("content");
                if (typeIndex != -1 && contentIndex != -1) {
                    if (cursor.moveToFirst()) {
                        do {
                            int type = cursor.getInt(typeIndex);
                            String content = cursor.getString(contentIndex);
                            chatList.add(new Msg(content, type));
                        } while (cursor.moveToNext());
                    }
                } else {
                    Log.e("ChatView", "Error: Required column not found in cursor.");
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("ChatView", "Cursor is null.");
        }


        if (chatList.isEmpty()) {
            String userName = getUserNameFromPreferences(context);
            if (!userName.isEmpty()) {
                chatList.add(new Msg("Hi, " + userName + ", how may I help you today?", Msg.TYPE_RECEIVED));
            } else {
                chatList.add(new Msg("Hi, how may I help you today?", Msg.TYPE_RECEIVED));
            }
        }
    }

    private String getUserNameFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("UserName", "");
    }
    private void sendMessage(TextInputEditText textInput) {
        String text = textInput.getText().toString();
        if (!text.isEmpty()) {
            chatList.add(new Msg(text, Msg.TYPE_SENT));
            updateAndScroll();
            sendMsg(text);
            textInput.setText("");
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.Instance);
        builder.setTitle("Confirm deleting");
        builder.setMessage("Are you sure to delete this chat?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            sqlOperate.deleteConversation(conversationId);
            chatList.clear();
            updateAndScroll();
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateAndScroll() {
        chatViewAdapter.notifyDataSetChanged();
        if (chatViewAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(chatViewAdapter.getItemCount() - 1);
        }
    }

    public void sendMsg(String text) {
        Log.d("sendMsg", text);

        if (!text.isEmpty()) {
            OkhttpUtil okhttpUtil = new OkhttpUtil();
            okhttpUtil.setContentUsr(text);

            chatList.add(new Msg("Message loading...", Msg.TYPE_RECEIVED));
            updateAndScroll();

            JSONArray messagesHistory = new JSONArray();
            Cursor cursor = sqlOperate.getMessages(conversationId);
            while (cursor.moveToNext()) {
                JSONObject message = new JSONObject();
                try {
                    int typeIndex = cursor.getColumnIndex("type");
                    int contentIndex = cursor.getColumnIndex("content");

                    if (typeIndex != -1 && contentIndex != -1) {
                        String role = cursor.getInt(typeIndex) == Msg.TYPE_RECEIVED ? "system" : "user";
                        String content = cursor.getString(contentIndex);

                        message.put("role", role);
                        message.put("content", content);
                        messagesHistory.put(message);
                    } else {
                        Log.e("ChatView", "Column index not found.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();

            JSONObject currentUserMessage = new JSONObject();
            try {
                currentUserMessage.put("role", "user");
                currentUserMessage.put("content", text);
                messagesHistory.put(currentUserMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Callback gptCallBack = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    chatList.remove(chatList.size() - 1);
                    chatList.add(new Msg("Loading failed. Try again later.", Msg.TYPE_RECEIVED));
                    MainActivity.Instance.runOnUiThread(ChatView.this::updateAndScroll);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String str = response.body().string();
                    Log.i("Response Content", str);

                    String content = OkhttpUtil.getGptAnswer(str);

                    chatList.remove(chatList.size() - 1);
                    storeChatData(new Msg(text, Msg.TYPE_SENT));
                    storeChatData(new Msg(content, Msg.TYPE_RECEIVED));
                    chatList.add(new Msg(content, Msg.TYPE_RECEIVED));
                    MainActivity.Instance.runOnUiThread(ChatView.this::updateAndScroll);
                }
            };

            okhttpUtil.doPost(gptCallBack, messagesHistory);
        }
    }

    public void storeChatData(Msg msg) {
        sqlOperate.addMessage(conversationId, msg.getType(), msg.getContent());
    }
}
