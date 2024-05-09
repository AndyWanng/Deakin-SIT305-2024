package com.example.personalizedquizapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SummaryService {
    @POST("/getSummary")
    Call<SummaryResponse> getAnalysis(@Body SummaryRequest request);
}

