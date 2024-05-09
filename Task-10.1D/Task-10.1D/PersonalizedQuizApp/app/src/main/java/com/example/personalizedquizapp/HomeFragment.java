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
