package com.example.personalizedquizapp;

public class AnsweredQuestion extends Question {
    private String userAnswer;

    public AnsweredQuestion(String question, String option1, String option2, String option3, String option4, String answer, String userAnswer) {
        super(question, option1, option2, option3, option4, answer);
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}

package com.example.personalizedquizapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalizedquizapp.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HomeFragment extends Fragment {



    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentHomeBinding binding;
    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);

        database = FirebaseFirestore.getInstance();


        ArrayList<TopicModel> categories = new ArrayList<>();

        TopicAdapter adapter = new TopicAdapter(getContext(),categories);

        database.collection("topics")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            TopicModel model = new TopicModel();
                            model.setTopicId(snapshot.getId());
                            model.setTopicName(snapshot.getString("topicName"));
                            model.setTopicImage(snapshot.getString("topicImage"));
                            if (model.getTopicName() != null) {
                                Log.d("HomeFragment", "Topic ID: " + snapshot.getId() + ", Topic Name: " + model.getTopicName() + ", Topic Image: " + model.getTopicImage());
                                categories.add(model);
                            } else {
                                Log.e("HomeFragment", "Topic Name is null for ID: " + snapshot.getId());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        binding.topicList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.topicList.setAdapter(adapter);
        return binding.getRoot();
    }
}


package com.example.personalizedquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.personalizedquizapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass;
                email = binding.emailBox.getText().toString();
                pass = binding.passwordBox.getText().toString();


                dialog.show();

                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
}


package com.example.personalizedquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.personalizedquizapp.databinding.ActivityMainBinding;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content,new HomeFragment());
        transaction.commit();

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i){
                    case 0:
                        transaction.replace(R.id.content,new HomeFragment());
                        transaction.commit();
                        break;
                    case 1:
                        transaction.replace(R.id.content,new ProfileFragment());
                        transaction.commit();
                        break;

                }
                return false;
            }
        });

    }

}


package com.example.personalizedquizapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalizedquizapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    FragmentProfileBinding binding;
    FirebaseFirestore database;
    FirebaseAuth auth;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);

        database = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        database.collection("users")
                .document(auth.getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    user  = documentSnapshot.toObject(User.class);
                    binding.emailBox.setText(user.getEmail());
                    binding.nameBox.setText(user.getName());
                    Glide.with(getContext()).load(user.getProfile()).into(binding.profileImage);
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return binding.getRoot();
    }

    private void logout() {
        auth.signOut();

        Toast.makeText(getActivity(), "Logged out Succesfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}


package com.example.personalizedquizapp;

public class Question {
    private String question,option1,option2,option3,option4,answer,userAnswer;

    public Question() {
    }

    public Question(String question, String option1, String option2, String option3, String option4, String answer) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.userAnswer = "";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserAnswer() { return userAnswer; }

    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
}



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


package com.example.personalizedquizapp;

public class QuizCreationResponse {
    private String message;
    private String topicId;
    private String error;

    public QuizCreationResponse() {}

    public QuizCreationResponse(String message, String topicId, String error) {
        this.message = message;
        this.topicId = topicId;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}



package com.example.personalizedquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.personalizedquizapp.databinding.ActivityResultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;

    User user;
    int POINTS = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);

        long points = correctAnswers * POINTS;

        binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(points));

        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }
}


package com.example.personalizedquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.personalizedquizapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {


    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("We're creating new account...");


        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass, name, profile;

                email = binding.emailBox.getText().toString();
                pass = binding.passwordBox.getText().toString();
                name = binding.nameBox.getText().toString();
                profile = "https://img.icons8.com/dotty/80/user.png";

                final User user = new User(name, email, pass, profile);

                dialog.show();
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();

                            database
                                    .collection("users")
                                    .document(uid)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                dialog.dismiss();
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}


package com.example.personalizedquizapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    Context context;
    ArrayList<TopicModel> topicModels;
    private AlertDialog dialog;

    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private QuizApiService quizApiService = retrofit.create(QuizApiService.class);

    public interface QuizApiService {
        @GET("generateQuiz")
        Call<QuizCreationResponse> generateQuiz(@Query("topic") String topic);
    }

    public TopicAdapter(Context context, ArrayList<TopicModel> topicModels){
        this.context=context;
        this.topicModels = topicModels;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic,null);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicModel model = topicModels.get(position);

        holder.textView.setText(model.getTopicName());
        Glide.with(context).load(model.getTopicImage()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            showLoadingDialog(context);
            String topic = topicModels.get(holder.getAdapterPosition()).getTopicName();
            Log.d("QuizApp", "Sending topic: " + topic);
            quizApiService.generateQuiz(topic).enqueue(new Callback<QuizCreationResponse>() {
                @Override
                public void onResponse(Call<QuizCreationResponse> call, Response<QuizCreationResponse> response) {
                    dismissLoadingDialog(context);
                    if (response.isSuccessful() && response.body() != null) {
                        QuizCreationResponse quizResponse = response.body();
                        if ("Quiz generated and uploaded successfully".equals(quizResponse.getMessage())) {
                            Intent intent = new Intent(context, QuizActivity.class);
                            intent.putExtra("topicId", model.getTopicId());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Quiz generation failed: " + quizResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to generate quiz", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<QuizCreationResponse> call, Throwable t) {
                    dismissLoadingDialog(context);
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return topicModels.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView= itemView.findViewById(R.id.topic);
        }
    }

    private void showLoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    private void dismissLoadingDialog(Context context) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}



package com.example.personalizedquizapp;

public class TopicModel {
    private String topicId, topicName, topicImage;

    public TopicModel(String topicId, String topicName, String topicImage) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicImage = topicImage;
    }

    public TopicModel() {}

    public String getTopicId() { return topicId; }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }
}
