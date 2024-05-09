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

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    private HistoryItem historyItem;

    public static DetailFragment newInstance(String historyItemJson) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("historyItem", historyItemJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String jsonData = getArguments().getString("historyItem");
            historyItem = new Gson().fromJson(jsonData, HistoryItem.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        LinearLayout layout = view.findViewById(R.id.details_layout);
        int questionNumber = 1;

        for (HistoryQuestion question : historyItem.getQuestions()) {
            View questionView = inflater.inflate(R.layout.item_question, layout, false);
            TextView numberText = questionView.findViewById(R.id.question_number);
            numberText.setText(String.format("%d.", questionNumber));
            TextView questionText = questionView.findViewById(R.id.question_text);
            questionText.setText(question.getQuestion());

            LinearLayout optionsContainer = questionView.findViewById(R.id.options_container);
            String[] options = {question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};
            for (String option : options) {
                TextView optionView = new TextView(getContext());
                optionView.setText(option);
                optionView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                optionView.setPadding(20, 20, 20, 20);

                if (option.equals(question.getCorrectAnswer())) {
                    optionView.setBackground(getResources().getDrawable(R.drawable.option_right));
                } else if (option.equals(question.getUserAnswer())) {
                    optionView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
                }

                optionsContainer.addView(optionView);
            }

            layout.addView(questionView);
            questionNumber++;
        }


        Button analyzeButton = view.findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(v -> fetchAnalysis());

        return view;
    }
    private String createQuizSummary() {
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Please analyze the results below in details and provide insights from the results.\n");
        for (HistoryQuestion question : historyItem.getQuestions()) {
            summaryBuilder.append("Question: ").append(question.getQuestion()).append("\n")
                    .append("Option: A. ").append(question.getOption1())
                    .append(", B. ").append(question.getOption2())
                    .append(", C. ").append(question.getOption3())
                    .append(", D. ").append(question.getOption4()).append("\n")
                    .append("Correct Answer: ").append(question.getCorrectAnswer()).append("\n")
                    .append("User Answer: ").append(question.getUserAnswer()).append("\n\n");
        }
        return summaryBuilder.toString();
    }


    private void fetchAnalysis() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SummaryService service = retrofit.create(SummaryService.class);

        String summaryText = createQuizSummary();
        Call<SummaryResponse> call = service.getAnalysis(new SummaryRequest(summaryText));
        call.enqueue(new Callback<SummaryResponse>() {
            @Override
            public void onResponse(Call<SummaryResponse> call, Response<SummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SummaryResponse summaryResponse = response.body();
                    String summaryText = summaryResponse.getSummary();
                    if (summaryText != null && !summaryText.isEmpty()) {
                        showAnalysisDialog(summaryText);
                    } else {
                        showAnalysisDialog("No valid response received.");
                    }
                } else {
                    showAnalysisDialog("Request Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SummaryResponse> call, Throwable t) {
                showAnalysisDialog("Request Failed: " + t.getMessage());
            }

        });
    }

    private void showAnalysisDialog(String analysis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Quiz Summary");
        builder.setMessage(analysis);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
package com.example.personalizedquizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> historyItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> historyItems, OnItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.textViewDate.setText(item.getDate());
        holder.textViewScore.setText("Score: " + item.getScore());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewScore;

        public HistoryViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewScore = itemView.findViewById(R.id.textViewScore);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick((HistoryItem) itemView.getTag());
                }
            });
        }
    }
}
package com.example.personalizedquizapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.personalizedquizapp.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {
    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        setupRecyclerView();
        loadHistory();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(historyItems, this);
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.historyRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(HistoryItem item) {
        showDetailFragment(new Gson().toJson(item));
    }

    private void showDetailFragment(String historyItemJson) {
        DetailFragment fragment = DetailFragment.newInstance(historyItemJson);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("HistoryFragment", "User not logged in");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").document(userId).collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        historyItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HistoryItem item = document.toObject(HistoryItem.class);
                            historyItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("HistoryFragment", "Error loading history", task.getException());
                        Toast.makeText(getContext(), "Error loading history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.example.personalizedquizapp;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryItem {
    private String date;
    private List<HistoryQuestion> questions;
    private Timestamp timestamp;

    public HistoryItem() {
    }

    public HistoryItem(Timestamp timestamp, List<HistoryQuestion> questions) {
        this.timestamp = timestamp;
        this.date = formatDate(timestamp.toDate());  // 将 Timestamp 转换为格式化的日期字符串
        this.questions = questions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        this.date = formatDate(timestamp.toDate());
    }

    public List<HistoryQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<HistoryQuestion> questions) {
        this.questions = questions;
    }

    public String getScore() {
        int correctCount = 0;
        for (HistoryQuestion question : questions) {
            if (question.getUserAnswer() != null && question.getUserAnswer().equals(question.getCorrectAnswer())) {
                correctCount++;
            }
        }
        return correctCount + "/" + questions.size();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH", Locale.getDefault());
        return sdf.format(date);
    }
}

class HistoryQuestion {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private String userAnswer;

    public HistoryQuestion() {
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
package com.example.personalizedquizapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.personalizedquizapp.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseFirestore database;
    ArrayList<TopicModel> categories;
    TopicAdapter adapter;
    private AlertDialog dialog;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        adapter = new TopicAdapter(getContext(), categories);

        database.collection("topics")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        if (error != null) {
                            Log.e("HomeFragment", "Error loading topics", error);
                            return;
                        }
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            TopicModel model = new TopicModel();
                            model.setTopicId(snapshot.getId());
                            model.setTopicName(snapshot.getString("topicName"));
                            model.setTopicImage(snapshot.getString("topicImage"));
                            if (model.getTopicName() != null) {
                                categories.add(model);
                            } else {
                                Log.e("HomeFragment", "Topic Name is null for ID: " + snapshot.getId());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        binding.topicList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.topicList.setAdapter(adapter);
        binding.textView11.setOnClickListener(v -> startRandomQuiz());
        binding.textView12.setOnClickListener(v -> shareApp());

        return binding.getRoot();
    }
    private void startRandomQuiz() {
        if (!categories.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(categories.size());
            TopicModel randomTopic = categories.get(index);
            showLoadingDialog();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TopicAdapter.QuizApiService quizApiService = retrofit.create(TopicAdapter.QuizApiService.class);

            quizApiService.generateQuiz(randomTopic.getTopicName()).enqueue(new Callback<QuizCreationResponse>() {
                @Override
                public void onResponse(Call<QuizCreationResponse> call, Response<QuizCreationResponse> response) {
                    dismissLoadingDialog();
                    if (response.isSuccessful() && response.body() != null) {
                        QuizCreationResponse quizResponse = response.body();
                        if ("Quiz generated and uploaded successfully".equals(quizResponse.getMessage())) {
                            Intent intent = new Intent(getContext(), QuizActivity.class);
                            intent.putExtra("topicId", randomTopic.getTopicId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Quiz generation failed: " + quizResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to generate quiz", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<QuizCreationResponse> call, Throwable t) {
                    dismissLoadingDialog();
                    Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No topics available", Toast.LENGTH_SHORT).show();
        }
    }
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
    private void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this cool quiz app!");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
package com.example.personalizedquizapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalizedquizapp.databinding.RowLeaderboardsBinding;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>{

    Context context;
    ArrayList<User> users;

    public LeaderBoardAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards, parent, false);
        return new LeaderBoardViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getName());
        holder.binding.coins.setText(String.valueOf(user.getCoins()));
        holder.binding.index.setText(String.format("#%d", position+1));

        Glide.with(context)
                .load(user.getProfile())
                .into(holder.binding.imageView7);
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public class LeaderBoardViewHolder extends RecyclerView.ViewHolder{

        RowLeaderboardsBinding binding;
        public LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLeaderboardsBinding.bind(itemView);
        }
    }
}
package com.example.personalizedquizapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.personalizedquizapp.databinding.FragmentLeaderBoardsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderBoardsFragment extends Fragment {


    public LeaderBoardsFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentLeaderBoardsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderBoardsBinding.inflate(inflater, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        final ArrayList<User> users = new ArrayList<>();
        final LeaderBoardAdapter adapter = new LeaderBoardAdapter(getContext(), users);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database.collection("users")
                .orderBy("coins", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            try {
                                User user = snapshot.toObject(User.class);
                                users.add(user);
                            } catch (Exception e) {
                                Log.e("LeaderboardDebug", Objects.requireNonNull(e.getMessage()));
                                Log.d("LeaderboardDebug","Could not convert into oblject of user class");
                            }

                        }
                        adapter.notifyDataSetChanged();
                    }
                });


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
                        transaction.replace(R.id.content, new HistoryFragment());
                        transaction.commit();
                        break;
                    case 2:
                        transaction.replace(R.id.content,new LeaderBoardsFragment());
                        transaction.commit();
                        break;
                    case 3:
                        transaction.replace(R.id.content,new WalletFragment());
                        transaction.commit();
                        break;
                    case 4:
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
        binding.restartBtn.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finishAffinity();
        });
        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResults();
            }
        });
    }

    private void shareResults() {
        String shareText = "I scored " + getIntent().getIntExtra("correct", 0) + " out of " +
                getIntent().getIntExtra("total", 0) + " and earned " +
                (getIntent().getIntExtra("correct", 0) * POINTS) + " scores on the Quiz App!";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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
//                            dialog.dismiss();
                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}
package com.example.personalizedquizapp;

import android.os.Message;

import java.util.List;

public class SummaryRequest {
    private String text;

    public SummaryRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
package com.example.personalizedquizapp;

import java.util.List;

public class SummaryResponse {
    private String id;
    private List<Choice> choices;

    public String getSummary() {
        if (choices == null || choices.isEmpty()) {
            return "No answer available";
        }
        return choices.get(0).message.content;
    }

    static class Choice {
        Message message;
    }

    static class Message {
        String content;
    }
}
package com.example.personalizedquizapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SummaryService {
    @POST("/getSummary")
    Call<SummaryResponse> getAnalysis(@Body SummaryRequest request);
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
package com.example.personalizedquizapp;

public class User {
    private String name, email, pass, profile;
    private String tier = "Free";
    private long coins = 0;

    public User() {
    }

    public User(String name, String email, String pass, String profile) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public long getCoins() {
        return coins;
    }

    public String getTier(){return tier;}

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
package com.example.personalizedquizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalizedquizapp.databinding.FragmentWalletBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class WalletFragment extends Fragment {



    public WalletFragment() {
    }

    private static final String CONFIG_CLIENT_ID = "AUgVCM9OHAJOirmkUXRQ0uWCFq1V-xn8Tb5txG-J5pfo2m_rm2LLgfPtil57ESN1yXf_U_f3K7Tm-lNd";
    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(CONFIG_CLIENT_ID);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);
    }

    FragmentWalletBinding binding;
    FirebaseFirestore database;
    User user;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);

        database = FirebaseFirestore.getInstance();
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        binding.currentCoins.setText(String.valueOf(user.getTier()));
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViewClickListeners();
    }

    private void bindViewClickListeners() {
        binding.viewTier1.setOnClickListener(v -> startPayPalPayment(10));
        binding.viewTier2.setOnClickListener(v -> startPayPalPayment(20));
    }

    private void startPayPalPayment(double amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD",
                "Sample Item", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }
}
package com.example.personalizedquizapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class WithdrawRequest {
    private String userId;
    private String emailAddress;
    private String requestedBy;

    public WithdrawRequest() {
    }

    public WithdrawRequest(String userId, String emailAddress, String requestedBy) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.requestedBy = requestedBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @ServerTimestamp
    private Date createAt;


}
