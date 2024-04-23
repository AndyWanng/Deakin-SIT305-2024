package com.example.personalizedquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalizedquizapp.databinding.ActivityQuizBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;

    ArrayList<Question> questions;
    ArrayList<AnsweredQuestion> answeredQuestions;
    CountDownTimer timer;
    int index = 0;
    Question question;
    FirebaseFirestore database;

    int correctanswers = 0;

    boolean hasAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        questions = new ArrayList<>();
        answeredQuestions = new ArrayList<>();

        database = FirebaseFirestore.getInstance();
        String topicId = getIntent().getStringExtra("topicId");

        CollectionReference questionsCollection = database.collection("topics").document(topicId).collection("questions");

        questionsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Question question = documentToQuestion(document);
                        String questionText = question.getQuestion();
                        Log.d("QuizActivity", "Fetched question: " + questionText);

                        questions.add(question);
                    }
                    setNextQuestion();
                } else {
                    Log.e("QuizActivity", "Error fetching questions", task.getException());
                    Toast.makeText(QuizActivity.this, "Error fetching questions. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Question documentToQuestion(QueryDocumentSnapshot document) {


        String questionText = document.getString("question");
        String option1 = document.getString("option1");
        String option2 = document.getString("option2");
        String option3 = document.getString("option3");
        String option4 = document.getString("option4");
        String answer = document.getString("answer");


        return new Question(questionText, option1, option2, option3, option4, answer);
    }


    void setNextQuestion() {

        hasAnswered = false;

        if(index < questions.size()) {
            if(timer!=null){
                timer.cancel();
            }
            resetTimer();
            timer.start();
            binding.questionCounter.setText(String.format("%d/%d", (index+1), questions.size()));
            question = questions.get(index);
            binding.question.setText(question.getQuestion());
            binding.option1.setText(question.getOption1());
            binding.option2.setText(question.getOption2());
            binding.option3.setText(question.getOption3());
            binding.option4.setText(question.getOption4());
        }else{
            Toast.makeText(QuizActivity.this, "Quiz Finished", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QuizActivity.this,ResultActivity.class);
            intent.putExtra("correct", correctanswers);
            intent.putExtra("total", questions.size());
            saveHistory();
            startActivity(intent);
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void reset(){
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        for (TextView textView : Arrays.asList(binding.option3, binding.option4)) {
            textView.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void checkAnswer(TextView textView){
        String userAnswer = textView.getText().toString();
        question.setUserAnswer(userAnswer);
        String correctAnswer = question.getAnswer();
        TextView correctOption;
        if (binding.option1.getText().toString().equals(correctAnswer)){
            correctOption = binding.option1;
        }else if (binding.option2.getText().toString().equals(correctAnswer)){
            correctOption = binding.option2;
        }else if (binding.option3.getText().toString().equals(correctAnswer)){
            correctOption = binding.option3;
        }else{
            correctOption = binding.option4;
        }

        if (textView == correctOption){
            textView.setBackground(getResources().getDrawable(R.drawable.option_right));
            correctanswers++;
        }else {
            textView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            correctOption.setBackground(getResources().getDrawable(R.drawable.option_right));
        }
    }

    void resetTimer(){
        timer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timer.setText(String.valueOf(millisUntilFinished/1000) );
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasAnswered) {
                            hasAnswered = true;
                            TextView correctOption = findCorrectOption();
                            correctOption.setBackground(getResources().getDrawable(R.drawable.option_right));
                            Toast.makeText(QuizActivity.this, "Time is up!", Toast.LENGTH_SHORT).show();
                            question.setUserAnswer("Time Exceeded");
                        }
                    }
                });
            }
        };
    }

    public void onClick(View view){

        int viewId = view.getId();

        if (viewId == R.id.nextBtn){
            index++;
            setNextQuestion();
            reset();
        } else if (viewId == R.id.quitBtn) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("Are you sure you want to quit the quiz?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startActivity(new Intent(QuizActivity.this, MainActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (!hasAnswered && (viewId == R.id.option_1 || viewId == R.id.option_2 || viewId == R.id.option_3 || viewId == R.id.option_4)) {
            timer.cancel();
            hasAnswered = true;
            TextView selected = (TextView) view;
            checkAnswer(selected);
        }

    }

    public void saveHistory() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("QuizActivity", "User not logged in, cannot save history");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userHistoryRef = db.collection("users").document(userId).collection("history");

        Map<String, Object> quizResult = new HashMap<>();
        List<Map<String, Object>> questionsData = new ArrayList<>();

        for (Question q : questions) {
            Map<String, Object> qData = new HashMap<>();
            qData.put("question", q.getQuestion());
            qData.put("option1", q.getOption1());
            qData.put("option2", q.getOption2());
            qData.put("option3", q.getOption3());
            qData.put("option4", q.getOption4());
            qData.put("correctAnswer", q.getAnswer());
            qData.put("userAnswer", q.getUserAnswer());
            questionsData.add(qData);
        }

        quizResult.put("questions", questionsData);
        quizResult.put("timestamp", new Date());

        userHistoryRef.add(quizResult)
                .addOnSuccessListener(aVoid -> Log.d("QuizActivity", "History saved successfully for user: " + userId))
                .addOnFailureListener(e -> Log.e("QuizActivity", "Error saving history for user: " + userId, e));
    }

    private TextView findCorrectOption() {
        String correctAnswer = question.getAnswer();
        if (binding.option1.getText().toString().equals(correctAnswer)) {
            return binding.option1;
        } else if (binding.option2.getText().toString().equals(correctAnswer)) {
            return binding.option2;
        } else if (binding.option3.getText().toString().equals(correctAnswer)) {
            return binding.option3;
        } else {
            return binding.option4;
        }
    }

}