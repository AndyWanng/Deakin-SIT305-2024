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

