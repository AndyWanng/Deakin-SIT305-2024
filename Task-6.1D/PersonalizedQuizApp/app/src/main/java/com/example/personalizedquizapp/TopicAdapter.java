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
