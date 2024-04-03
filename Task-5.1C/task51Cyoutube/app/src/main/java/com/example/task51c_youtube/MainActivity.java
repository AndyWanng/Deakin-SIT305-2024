package com.example.task51c_youtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.task51c_youtube.entity.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin, buttonRegister;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign variables
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Initialise the database
        DatabaseHelper.init(this);

        buttonLogin.setOnClickListener(v -> {
            // Check if all values have been entered and attempt login
            if (validateFields()) {
                if (validateLogin(editTextUsername.getText().toString(), editTextPassword.getText().toString())) {
                    // If successful, go to home activity
                    Playlist playlist = DatabaseHelper.getInstance().getUserPlayList(editTextUsername.getText().toString());
                    try {
                        openHomeActivity(playlist);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error opening home activity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRegister.setOnClickListener(v -> {
            // Go to sign up activity
            openSignUpActivity();
        });

    }

    private Boolean validateFields() {
        boolean check = true;
        if (editTextUsername.getText().toString().isEmpty()) {
            // If not, display error message
            editTextUsername.setError("Please enter a username");
            check = false;
        }
        if (editTextPassword.getText().toString().isEmpty()) {
            // If not, display error message
            editTextPassword.setError("Please enter a password");
            check = false;
        }
        return check;
    }

    private Boolean validateLogin(String username, String password) {
        return DatabaseHelper.getInstance().verifyUser(username, password);
    }

    private void openHomeActivity(Playlist playlist) throws JsonProcessingException {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("playList", objectMapper.writeValueAsString(playlist));
        startActivity(intent);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}