package com.example.personalizedquizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> historyItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> historyItems, OnItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.textViewDate.setText(item.getDate());
        holder.textViewScore.setText("Score: " + item.getScore());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewScore;

        public HistoryViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewScore = itemView.findViewById(R.id.textViewScore);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick((HistoryItem) itemView.getTag());
                }
            });
        }
    }
}


