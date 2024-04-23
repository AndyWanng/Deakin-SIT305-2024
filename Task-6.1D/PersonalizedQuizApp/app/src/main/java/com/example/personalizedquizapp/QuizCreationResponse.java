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
