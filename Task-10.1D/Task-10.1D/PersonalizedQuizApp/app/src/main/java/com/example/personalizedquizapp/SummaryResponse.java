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

