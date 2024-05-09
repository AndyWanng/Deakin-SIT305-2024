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
