package com.example.task_31c;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    Integer score = 0, currentQuestion = 1;
    ProgressBar progressBar;
    TextView welcomeText, progressText, questionTitle, questionDescription, liveScore;
    Button ans1,ans2,ans3,submit, userSelection;
    String[][] questions = new String[10][];
    String correctAnswer;
    Button[] answerButtons = new Button[3];
    Boolean answerSelected = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        progressBar = findViewById(R.id.ProgressBar);
        welcomeText = findViewById(R.id.WelcomeText);
        progressText = findViewById(R.id.Progress_level);
        questionTitle = findViewById(R.id.QuestionTitle);
        questionDescription = findViewById(R.id.QuestionDesc);
        liveScore = findViewById(R.id.LiveScore);
        ans1 = findViewById(R.id.Answer1);
        ans1.setTag(1);
        answerButtons[0] = ans1;

        ans2 = findViewById(R.id.Answer2);
        ans2.setTag(2);
        answerButtons[1] = ans2;

        ans3 = findViewById(R.id.Answer3);
        ans3.setTag(3);
        answerButtons[2] = ans3;

        submit = findViewById(R.id.submitButton);
        submit.setTag(0);



        questions[0] = getResources().getStringArray(R.array.question1);
        questions[1] = getResources().getStringArray(R.array.question2);
        questions[2] = getResources().getStringArray(R.array.question3);
        questions[3] = getResources().getStringArray(R.array.question4);
        questions[4] = getResources().getStringArray(R.array.question5);
        questions[5] = getResources().getStringArray(R.array.question6);
        questions[6] = getResources().getStringArray(R.array.question7);
        questions[7] = getResources().getStringArray(R.array.question8);
        questions[8] = getResources().getStringArray(R.array.question9);
        questions[9] = getResources().getStringArray(R.array.question10);

        setQuestion();
    }

    public void setQuestion(){
        String welcomeTextString = "Welcome " + getIntent().getStringExtra("name") + "!";
        welcomeText.setText(welcomeTextString);

        questionTitle.setText(questions[currentQuestion-1][0]);
        questionDescription.setText(questions[currentQuestion-1][1]);
        ans1.setText(questions[currentQuestion-1][2]);
        ans2.setText(questions[currentQuestion-1][3]);
        ans3.setText(questions[currentQuestion-1][4]);

        correctAnswer = questions[currentQuestion-1][5];

        progressBar.setProgress(currentQuestion);
        progressText.setText(String.valueOf(currentQuestion) + "/10");

        int defaultBtnColor = ContextCompat.getColor(this, R.color.ans_btn);
        ans1.setBackgroundColor(defaultBtnColor);
        ans2.setBackgroundColor(defaultBtnColor);
        ans3.setBackgroundColor(defaultBtnColor);
    }


    @Override
    public void onClick(View v) {
        int defaultColor = ContextCompat.getColor(v.getContext(), R.color.ans_btn);
        ans1.setBackgroundColor(defaultColor);
        ans2.setBackgroundColor(defaultColor);
        ans3.setBackgroundColor(defaultColor);

        int id = v.getId();
        if (id == R.id.Answer1) {
            ans1.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.selected_answer_btn));
            userSelection = ans1;
        } else if (id == R.id.Answer2) {
            ans2.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.selected_answer_btn));
            userSelection = ans2;
        } else if (id == R.id.Answer3) {
            ans3.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.selected_answer_btn));
            userSelection = ans3;
        }
    }



    public void submitClick(View v){
        if (Integer.parseInt(v.getTag().toString()) == 0) {
            Integer selectedAnswer = -1;
            boolean answerSelected = false;

            try {
                selectedAnswer = Integer.parseInt(userSelection.getTag().toString());
                answerSelected = true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please select an answer!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (answerSelected) {
                int correctAnswer = Integer.parseInt(questions[currentQuestion - 1][5]);

                int colorCorrect = ContextCompat.getColor(getApplicationContext(), R.color.correct_answer_btn);
                int colorIncorrect = ContextCompat.getColor(getApplicationContext(), R.color.incorrect_answer_btn);

                if (selectedAnswer.equals(correctAnswer)) {
                    userSelection.setBackgroundColor(colorCorrect);
                    score += 1;
                    updateLiveScore();
                } else {
                    userSelection.setBackgroundColor(colorIncorrect);
                    answerButtons[correctAnswer - 1].setBackgroundColor(colorCorrect);
                }

                ans1.setClickable(false);
                ans2.setClickable(false);
                ans3.setClickable(false);

                submit.setText("NEXT");
                submit.setTag(1);
            }
        }
        else{
            userSelection = null;
            answerSelected=false;

            System.out.println("Next question appears now...");
            currentQuestion = currentQuestion + 1;

            if(currentQuestion<11){
                submit.setText("SUBMIT");
                submit.setTag(0);
                setQuestion();

                ans1.setClickable(true);
                ans2.setClickable(true);
                ans3.setClickable(true);
            }
            else{
                submit.setTag(3);

                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                intent.putExtra("score", score);
                setResult(RESULT_FIRST_USER, intent);

                finish();
            }
        }
    }
    public void updateLiveScore() {
        String liveScoreText = score.toString() + "/10";
        liveScore.setText("Live Score: " + liveScoreText);
    }

}