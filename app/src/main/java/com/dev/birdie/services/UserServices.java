package com.dev.birdie.services;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.User;
import com.dev.birdie.network.ApiConfig;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserServices {
    // Health check
    @GET(ApiConfig.USERS_ENDPOINT + "/health/check")
    Call<ApiResponse<String>> healthCheck();

    // Create user
    @POST(ApiConfig.USERS_ENDPOINT)
    Call<ApiResponse<User>> createUser(@Body User user);

    // Get user by Firebase UID
    @GET(ApiConfig.USERS_ENDPOINT + "/{firebase_uid}")
    Call<ApiResponse<User>> getUserByFirebaseUid(@Path("firebase_uid") String firebaseUid);

    // Update user
    @PUT(ApiConfig.USERS_ENDPOINT + "/{firebase_uid}")
    Call<ApiResponse<User>> updateUser(@Path("firebase_uid") String firebaseUid, @Body User user);

    // Delete user (soft delete)
    @DELETE(ApiConfig.USERS_ENDPOINT + "/{firebase_uid}")
    Call<ApiResponse<Void>> deleteUser(@Path("firebase_uid") String firebaseUid);

    // Get all users (for admin/testing)
    @GET(ApiConfig.USERS_ENDPOINT)
    Call<ApiResponse<List<User>>> getAllUsers();
}
