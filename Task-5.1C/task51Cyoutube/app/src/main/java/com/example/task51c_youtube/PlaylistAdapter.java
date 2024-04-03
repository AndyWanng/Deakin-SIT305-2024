package com.example.task51c_youtube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task51c_youtube.entity.Playlist;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    static Context context;
    static Playlist playlist;


    public PlaylistAdapter(Context _context, Playlist _playlist) {
        context = _context;
        playlist = _playlist;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycleview_layout, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        // Set the data
        holder.itemLinkTV.setText(playlist.getLinks().get(position));
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return playlist.getLinks().size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemLinkTV;

        public PlaylistViewHolder(@NonNull View itemView) {
            // Initialize the views
            super(itemView);

            itemLinkTV = itemView.findViewById(R.id.itemLinkTV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked
            int position = getAdapterPosition();

            // Get the link at the position
            String link = playlist.getLinks().get(position);

            // Open the YouTubeActivity
            openYouTubeActivity(link);
        }
    }

    public static void openYouTubeActivity(String link) {
        Intent intent = new Intent(context, YoutubeActivity.class);
        intent.putExtra("link", link);
        context.startActivity(intent);
    }

}
