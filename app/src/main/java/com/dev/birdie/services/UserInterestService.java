package com.dev.birdie.services;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.UserInterests;
import com.dev.birdie.models.UserInterestRequests.*;
import com.dev.birdie.network.ApiConfig;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserInterestService {

    /**
     * Get all interests for a specific user
     * GET /api/user-interests/{userId}
     */
    @GET(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}")
    Call<ApiResponse<List<UserInterests>>> getUserInterests(@Path("userId") int userId);

    /**
     * Add a single interest to a user
     * POST /api/user-interests/{userId}
     * Body: { "interest_name": "Swimming" }
     */
    @POST(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}")
    Call<ApiResponse<UserInterests>> addUserInterest(
            @Path("userId") int userId,
            @Body AddInterestRequest request
    );

    /**
     * Add multiple interests to a user at once
     * POST /api/user-interests/{userId}/multiple
     * Body: { "interests": ["Swimming", "Reading", "Gaming"] }
     */
    @POST(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}/multiple")
    Call<ApiResponse<List<UserInterests>>> addMultipleUserInterests(
            @Path("userId") int userId,
            @Body AddMultipleInterestsRequest request
    );

    /**
     * Update (replace) all interests for a user
     * PUT /api/user-interests/{userId}
     * Body: { "interests": ["New Interest 1", "New Interest 2"] }
     */
    @PUT(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}")
    Call<ApiResponse<List<UserInterests>>> updateUserInterests(
            @Path("userId") int userId,
            @Body UpdateInterestsRequest request
    );

    /**
     * Remove a specific interest from a user
     * DELETE /api/user-interests/{userId}/{interestName}
     */
    @DELETE(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}/{interestName}")
    Call<ApiResponse<Void>> removeUserInterest(
            @Path("userId") int userId,
            @Path("interestName") String interestName
    );

    /**
     * Remove all interests for a user
     * DELETE /api/user-interests/{userId}
     */
    @DELETE(ApiConfig.USER_INTERESTS_ENDPOINT + "/{userId}")
    Call<ApiResponse<Void>> removeAllUserInterests(@Path("userId") int userId);

    /**
     * Get list of available interest options
     * GET /api/user-interests/options/available
     */
    @GET("api/user-interests/options/available")
    Call<ApiResponse<List<String>>> getAvailableInterests();
}