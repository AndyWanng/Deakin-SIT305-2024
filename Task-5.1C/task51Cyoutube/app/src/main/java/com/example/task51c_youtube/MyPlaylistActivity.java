package com.example.task51c_youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.task51c_youtube.entity.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyPlaylistActivity extends AppCompatActivity {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    RecyclerView playlistRV;
    PlaylistAdapter playlistAdapter;
    RecyclerView.LayoutManager playlistLayoutManager;

    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playlist);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                playlist = objectMapper.readValue(extras.getString("playList"), Playlist.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Set recycler views
        playlistRV = findViewById(R.id.playlistRV);
        playlistLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playlistAdapter = new PlaylistAdapter(this, playlist);
        playlistRV.setLayoutManager(playlistLayoutManager);
        playlistRV.setAdapter(playlistAdapter);
    }
}