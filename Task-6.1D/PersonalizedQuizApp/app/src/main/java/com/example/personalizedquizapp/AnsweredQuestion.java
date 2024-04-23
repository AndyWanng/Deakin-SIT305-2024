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

