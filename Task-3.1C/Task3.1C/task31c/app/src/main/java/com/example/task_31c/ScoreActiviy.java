package com.example.task_31c;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActiviy extends AppCompatActivity {
    String userName;
    TextView congratulationsText, yourScoreText, finalScoreText;
    Button newQuizButton, finishButton;
    Integer score;
    boolean newQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activiy);

        congratulationsText = findViewById(R.id.congratulationsText);
        yourScoreText = findViewById(R.id.yourScoreText);
        finalScoreText = findViewById(R.id.finalScoreText);

        newQuizButton = findViewById(R.id.newQuizButton);
        finishButton = findViewById(R.id.finishButton);

        userName = getIntent().getStringExtra("name");
        score = getIntent().getIntExtra("score", -1);

        if (score > 5) {
            congratulationsText.setText("Congratulations " + userName + "!");
        } else {
            congratulationsText.setText(userName + ", better luck next time!");
        }
        finalScoreText.setText(score.toString() + "/10");
    }
    public void startNewQuiz(View view){
        newQuiz = true;
        Intent intent  = new Intent(ScoreActiviy.this, MainActivity.class);
        intent.putExtra("newQuiz", newQuiz);
        intent.putExtra("name", userName);
        setResult(2, intent);
        finish();
    }

    public void finishQuiz(View view){
        newQuiz = false;
        Intent intent = new Intent(ScoreActiviy.this, MainActivity.class);
        intent.putExtra("newQuiz", newQuiz);
        setResult(2, intent);
        finish();
    }
}