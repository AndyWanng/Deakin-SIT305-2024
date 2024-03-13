package com.example.task_31c;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    EditText name;
    Button startButton;
    String userName="";
    Integer finalScore=0;
    boolean newQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.NameEditText);
        name.setText(userName);
        startButton = findViewById(R.id.StartButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = name.getText().toString();

                if (userName.matches("")){
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    name.setText(userName);
                    Intent quizIntent = new Intent(MainActivity.this, QuizActivity.class);
                    quizIntent.putExtra("name", userName);
                    startActivityForResult(quizIntent, RESULT_FIRST_USER);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
            case(RESULT_FIRST_USER):
            {
                finalScore = data.getIntExtra("score", -1);

                Intent scoreIntent = new Intent(MainActivity.this, ScoreActiviy.class);
                scoreIntent.putExtra("name", userName);
                scoreIntent.putExtra("score", finalScore);
                startActivityForResult(scoreIntent, 2);
            } break;

            case(2):
            {
                userName = data.getStringExtra("name");
                newQuiz = data.getBooleanExtra("newQuiz", true);

                if(!newQuiz){
                    finish();
                }
            } break;
        }
    }
}