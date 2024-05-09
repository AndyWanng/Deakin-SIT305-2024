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
