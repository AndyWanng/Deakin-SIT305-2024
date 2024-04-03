package com.example.task51c_youtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText fullNameET, usernameET, passwordET, confirmPasswordET;
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Assign variables
        fullNameET = findViewById(R.id.fullNameET);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(v -> {
            // Check if all values have been entered and attempt the creation of a new account
            if (validateEntries()) {
                // Add user to database
                if (DatabaseHelper.getInstance().checkIfUserExists(usernameET.getText().toString())) {
                    usernameET.setError("Username already exists");
                } else {
                    boolean insertUser = DatabaseHelper.getInstance().insertUser(usernameET.getText().toString(),
                            fullNameET.getText().toString(),
                            passwordET.getText().toString());
                    if (!insertUser) {
                        Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
                    } else {
                        openMainActivity();
                    }
                }
            }
        });
    }

    private Boolean validateEntries() {
        boolean check = true;

        if (fullNameET.getText().toString().isEmpty()) {
            // If not, display error message
            fullNameET.setError("Please enter your full name");
            check = false;
        }
        if (usernameET.getText().toString().isEmpty()) {
            // If not, display error message
            usernameET.setError("Please enter a username");
            check = false;
        }
        if (passwordET.getText().toString().isEmpty()) {
            // If not, display error message
            passwordET.setError("Please enter a password");
            check = false;
        }
        if (confirmPasswordET.getText().toString().isEmpty()) {
            // If not, display error message
            confirmPasswordET.setError("Please confirm your password");
            check = false;
        }
        if (!passwordET.getText().toString().equals(confirmPasswordET.getText().toString())) {
            // If not, display error message
            confirmPasswordET.setError("Passwords do not match");
            check = false;
        }

        return check;
    }

    private void openMainActivity() {
        finish();
    }

}