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
