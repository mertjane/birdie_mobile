package com.dev.birdie.services;

import com.dev.birdie.dtos.FeedResponse;
import com.dev.birdie.dtos.SwipeRequest;
import com.dev.birdie.dtos.SwipeResponse;
import com.dev.birdie.network.ApiConfig; // Ensure this exists

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HomeService {

    // GET /api/home/feed?userId=123
    @GET(ApiConfig.FEEDS_ENDPOINT + "/feed")
    Call<FeedResponse> getFeed(@Query("userId") int userId);

    // POST /api/home/swipe
    @POST(ApiConfig.FEEDS_ENDPOINT + "/swipe")
    Call<SwipeResponse> swipeUser(@Body SwipeRequest request);
}