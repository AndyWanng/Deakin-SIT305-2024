package com.example.AskMe.chatView;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AskMe.R;
import com.example.AskMe.utils.SqlOperate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private Context context;
    private Cursor chatCursor;
    private SqlOperate sqlOperate;

    public ChatListAdapter(Context context, Cursor chatCursor, SqlOperate sqlOperate) {
        this.context = context;
        this.chatCursor = chatCursor;
        this.sqlOperate = sqlOperate;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_card, parent, false);
        ChatViewHolder viewHolder = new ChatViewHolder(itemView);
        itemView.setTag(this);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (!chatCursor.moveToPosition(position)) {
            return;
        }

        int idIndex = chatCursor.getColumnIndex("id");
        int timestampIndex = chatCursor.getColumnIndex("timestamp");
        if (idIndex == -1 || timestampIndex == -1) {
            Log.e("ChatListAdapter", "Column 'id' or 'timestamp' not found.");
            return;
        }

        final long id = chatCursor.getLong(idIndex);
        long timestamp = chatCursor.getLong(timestampIndex);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = sdf.format(new Date(timestamp));

        holder.textViewChatId.setText(String.format("Chat "));
        holder.textViewTimestamp.setText("Last Active: " + formattedDate);

        holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(id));
    }

    @Override
    public int getItemCount() {
        return chatCursor != null ? chatCursor.getCount() : 0;
    }

    private void showDeleteConfirmationDialog(long id) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Conversation")
                .setMessage("Are you sure you want to delete this conversation?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    sqlOperate.deleteConversation(id);
                    updateCursor();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateCursor() {
        if (this.chatCursor != null) {
            this.chatCursor.close();
        }
        this.chatCursor = sqlOperate.getAllConversations();
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (chatCursor != null) {
            chatCursor.close();
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChatId;
        TextView textViewTimestamp;
        ImageButton deleteButton;

        ChatViewHolder(View itemView) {
            super(itemView);
            textViewChatId = itemView.findViewById(R.id.text_view_chat_id);
            textViewTimestamp = itemView.findViewById(R.id.text_view_timestamp);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Cursor cursor = ((ChatListAdapter) itemView.getTag()).chatCursor;
                        if (cursor.moveToPosition(position)) {
                            int idIndex = cursor.getColumnIndex("id");
                            if (idIndex != -1) {
                                long id = cursor.getLong(idIndex);
                                Activity activity = (Activity) itemView.getContext();
                                ChatView chatView = new ChatView(id);
                                View chatViewLayout = chatView.Load(activity);

                                FrameLayout frameLayout = activity.findViewById(R.id.frameLayout);
                                frameLayout.removeAllViews();
                                frameLayout.addView(chatViewLayout);
                            } else {
                                Log.e("ChatViewHolder", "Column 'id' not found.");
                            }
                        }
                    }
                }
            });
        }
    }
}
package com.example.AskMe.chatView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AskMe.MainActivity;
import com.example.AskMe.R;
import com.example.AskMe.utils.SqlOperate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private SqlOperate sqlOperate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_list, container, false);

        sqlOperate = new SqlOperate();
        recyclerView = view.findViewById(R.id.recycler_view_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(getContext(), sqlOperate.getAllConversations(), sqlOperate);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddChat = view.findViewById(R.id.fab_add_chat);
        fabAddChat.setOnClickListener(v -> addNewChat());

        return view;
    }

    private void addNewChat() {
        long conversationId = sqlOperate.addConversation();
        if (conversationId != -1) {
            ChatView chatView = new ChatView(conversationId);
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.frameLayout.removeAllViews();
                mainActivity.frameLayout.addView(chatView.Load(mainActivity));
            }
        } else {
            Toast.makeText(getActivity(), "New chat created failed", Toast.LENGTH_SHORT).show();
        }
    }

}
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
package com.example.AskMe.chatView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AskMe.R;
import com.example.AskMe.model.Msg;

import java.util.List;

public class ChatViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Msg> chatList;

    public ChatViewAdapter(List<Msg> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == Msg.TYPE_RECEIVED) {
            View itemView = inflater.inflate(R.layout.item_chat_left, parent, false);
            return new LeftViewHolder(itemView);
        } else if (viewType == Msg.TYPE_SENT) {
            View itemView = inflater.inflate(R.layout.item_chat_right, parent, false);
            return new RightViewHolder(itemView);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Msg msg = chatList.get(position);
        if (holder instanceof LeftViewHolder) {
            ((LeftViewHolder) holder).bindData(msg);
        } else if (holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).bindData(msg);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView leftMsg;

        LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMsg = itemView.findViewById(R.id.leftMsg);
        }

        void bindData(Msg msg) {
            leftMsg.setText(msg.getContent());
        }
    }

    public static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView rightMsg;

        RightViewHolder(@NonNull View itemView) {
            super(itemView);
            rightMsg = itemView.findViewById(R.id.rightMsg);
        }

        void bindData(Msg msg) {
            rightMsg.setText(msg.getContent());
        }
    }
}
package com.example.AskMe.model;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
package com.example.AskMe.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.AskMe.MainActivity;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE conversations ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE messages ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "conversation_id INTEGER, "
            + "type INTEGER, "
            + "content TEXT, "
            + "FOREIGN KEY(conversation_id) REFERENCES conversations(id))";

    public MyDatabaseHelper(String name, int version) {
        super(MainActivity.Instance.getApplicationContext(), name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSATIONS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        Log.i("MyDatabaseHelper", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS chatList");
            onCreate(db);
        }
    }
}
package com.example.AskMe.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkhttpUtil {
    private static String apiKey = "";
    private String url = "http://10.0.2.2:5000/chat";
    private static String model = "gpt-3.5-turbo";
    private static double temperature = 0.7;
    private static int maxTokens = 1000;
    private String contentSys = "Good Assistant";
    private String contentUsr = "Who are you?";

    public void ipConfig(Callback newCallback) {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor);
        OkHttpClient client = builder.build();

        MediaType ip_mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", ip_mediaType);
        Request ipRequest = new Request.Builder()
                .url("https://api.pawan.krd/resetip")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        client.newCall(ipRequest).enqueue(newCallback);
    }

    public void doPost(Callback newCallback, JSONArray messagesHistory) {
        try {
            HttpLoggingInterceptor httpLoggingInterceptor =
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");

            JSONObject json = new JSONObject();
            json.put("model", model);
            json.put("max_tokens", maxTokens);
            json.put("temperature", temperature);
            json.put("messages", messagesHistory);
            String jsonString = json.toString();

            RequestBody requestBody = RequestBody.create(jsonString, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(newCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void doPost(Callback newCallback) {
        JSONArray messagesHistory = new JSONArray();
        doPost(newCallback, messagesHistory);
    }


    public static String getGptAnswer(String response){
        try {
            JSONObject json = new JSONObject(response);

            JSONArray choices = json.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String content = message.getString("content");
                return content;
            }
        }
        catch (Exception e){
            return "No results";
        }
        return "";
    }

    public static void setApiKey(String newApiKey) {
        apiKey = newApiKey;
    }

    public void setContentUsr(String contentUsr) {
        this.contentUsr = contentUsr;
    }

    public void setContentSys(String contentSys) {
        this.contentSys = contentSys;
    }

    public static void setMaxTokens(int newMaxTokens) {
        maxTokens = newMaxTokens;
    }

    public static void setModel(String newModel) {
        model = newModel;
    }

    public static void setTemperature(double newTemperature) {
        temperature = newTemperature;
    }
}
package com.example.AskMe.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.AskMe.sql.MyDatabaseHelper;

public class SqlOperate {
    private MyDatabaseHelper dbHelper;

    public SqlOperate() {
        dbHelper = new MyDatabaseHelper("chat.db", 2);
    }

    public long addConversation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timestamp", System.currentTimeMillis());
        long id = db.insert("conversations", null, values);
        return id;
    }

    public void addMessage(long conversationId, int type, String content) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("conversation_id", conversationId);
        contentValues.put("type", type);
        contentValues.put("content", content);
        db.insert("messages", null, contentValues);
    }

    public Cursor getAllConversations() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM conversations ORDER BY timestamp DESC", null);
    }

    public Cursor getMessages(long conversationId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("messages", null, "conversation_id = ?", new String[] { String.valueOf(conversationId) }, null, null, "id ASC");
    }

    public void deleteConversation(long conversationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("messages", "conversation_id = ?", new String[] { String.valueOf(conversationId) });
        db.delete("conversations", "id = ?", new String[] { String.valueOf(conversationId) });
    }
}
package com.example.AskMe;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class AppPermission {

    private static final int PERMISSION_REQUEST_CODE = 1;

    public static void requestPermissions(Activity activity) {
        if (!arePermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    public static boolean arePermissionsGranted(Activity activity) {
        int recordAudioPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int internetPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);

        return recordAudioPermission == PackageManager.PERMISSION_GRANTED &&
                internetPermission == PackageManager.PERMISSION_GRANTED;
    }
}
package com.example.AskMe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.AskMe.chatView.ChatListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {

    public FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;
    public static MainActivity Instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instance = this;
        setContentView(R.layout.activity_main);
        AppPermission.requestPermissions(this);

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_chat:
                        loadChatListFragment();
                        return true;
                }
                return false;
            }
        });

        loadChatListFragment();

        showNameInputDialog();
    }

    private void loadChatListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new ChatListFragment());
        fragmentTransaction.commit();
    }

    public void loadSingleAppActivity(Class appClass) {
        try {


            frameLayout.removeAllViews();
            Object viewRes = appClass.getMethod("Load", Context.class).invoke(null, MainActivity.this);
            frameLayout.addView(((View) viewRes));
        }
        catch (NoSuchMethodException e){
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        } catch (InvocationTargetException e) {
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        } catch (IllegalAccessException e) {
            Log.e("NoSuchMethodException","exec app "+appClass.toString());
        }

    }

    private void saveUserName(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserName", name);
        editor.apply();
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your name here");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                saveUserName(name);
            }
        });

        builder.setCancelable(false);

        builder.show();
    }
}
