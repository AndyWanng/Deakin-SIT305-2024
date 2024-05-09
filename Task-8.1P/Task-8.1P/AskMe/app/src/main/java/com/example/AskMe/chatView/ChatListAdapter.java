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
