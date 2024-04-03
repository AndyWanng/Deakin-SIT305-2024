package com.example.task51c_youtube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task51c_youtube.entity.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HomeActivity extends AppCompatActivity {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Playlist playList;
    EditText linkET;
    Button playButton, myPlayListButton, addPlayListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                playList = objectMapper.readValue(extras.getString("playList"), Playlist.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        linkET = findViewById(R.id.linkET);
        playButton = findViewById(R.id.playButton);
        addPlayListButton = findViewById(R.id.addPlayListButton);
        myPlayListButton = findViewById(R.id.myPlayListButton);

        playButton.setOnClickListener(view -> {
            if (validateLink(linkET.getText().toString())) {
                openYouTubeActivity(linkET.getText().toString());
            } else {
                Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show();
            }
        });

        addPlayListButton.setOnClickListener(view -> {
            if (validateLink(linkET.getText().toString())) {
                if (DatabaseHelper.getInstance().addToPlaylist(playList.getUserId(), linkET.getText().toString())) {
                    Toast.makeText(this, "Added to playlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error adding to playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myPlayListButton.setOnClickListener(view -> {
            try {
                openMyPlayListActivity();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error opening my playlist activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate youtube link
    public Boolean validateLink(String url) {
        boolean check = true;
        // Check if the link is empty
        if (linkET.getText().toString().isEmpty()) {
            // If not, display error message
            linkET.setError("Please enter a password");
            check = false;
        }
        // Regex check the youtube link
        if (!linkET.getText().toString().matches("^(https?\\:\\/\\/)?(www\\.youtube\\.com|youtu\\.?be)\\/.+$")) {
            linkET.setError("Please enter a valid youtube link");
            check = false;
        }

        return check;
    }

    private void openYouTubeActivity(String link) {
        Intent intent = new Intent(this, YoutubeActivity.class);
        intent.putExtra("link", link);
        startActivity(intent);
    }

    private void openMyPlayListActivity() throws JsonProcessingException {
        playList = DatabaseHelper.getInstance().getUserPlayList(playList.getUserId());
        Intent intent = new Intent(this, MyPlaylistActivity.class);
        intent.putExtra("playList", objectMapper.writeValueAsString(playList));
        startActivity(intent);
    }

}