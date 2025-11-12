package com.dev.birdie.repositories;

import android.app.Activity;
import android.util.Log;
import com.dev.birdie.models.User;
import com.dev.birdie.network.ApiClient;
import com.dev.birdie.network.ApiConfig;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;


public class UserRepository {

    private static final String TAG = "UserRepository";

    public static void insertUser(User user, Activity activity, OnUserInsertListener listener) {
        Log.d(TAG, "insertUser() called for user: " + user.getEmail());

        new Thread(() -> {
            boolean success = false;
            String errorMessage = null;

            try {
                // Create JSON request body
                JsonObject jsonBody = new JsonObject();
                jsonBody.addProperty("firebase_uid", user.getFirebaseUid());
                jsonBody.addProperty("email", user.getEmail());
                jsonBody.addProperty("full_name", user.getFullName());

                // Add optional fields if present
                if (user.getDateOfBirth() != null) {
                    jsonBody.addProperty("date_of_birth", user.getDateOfBirth());
                }
                if (user.getAge() != null) {
                    jsonBody.addProperty("age", user.getAge());
                }
                if (user.getGender() != null) {
                    jsonBody.addProperty("gender", user.getGender());
                }

                RequestBody body = ApiClient.createJsonRequestBody(jsonBody);

                // Build request
                String url = ApiConfig.BASE_URL + ApiConfig.USERS_ENDPOINT;
                Log.d(TAG, "POST URL: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // Execute request
                Log.d(TAG, "Executing API request...");
                Response response = ApiClient.getClient().newCall(request).execute();

                String responseBody = response.body().string();
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + responseBody);

                if (response.isSuccessful()) {
                    success = true;
                    Log.d(TAG, "User created successfully");
                } else {
                    errorMessage = "Server error: " + response.code() + " - " + responseBody;
                    Log.e(TAG, errorMessage);
                }

                response.close();

            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                errorMessage = "Network error: " + e.getMessage();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
                errorMessage = "Error: " + e.getMessage();
            }

            final boolean finalSuccess = success;
            final String finalError = errorMessage;

            // Return to UI thread
            activity.runOnUiThread(() -> {
                if (finalSuccess) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(finalError != null ? finalError : "Unknown error");
                }
            });
        }).start();
    }

    public static void getUserByFirebaseUid(String firebaseUid, Activity activity, OnUserFetchListener listener) {
        Log.d(TAG, "getUserByFirebaseUid() called for: " + firebaseUid);

        new Thread(() -> {
            User user = null;
            String errorMessage = null;

            try {
                String url = ApiConfig.BASE_URL + ApiConfig.USERS_ENDPOINT + "/" + firebaseUid;
                Log.d(TAG, "GET URL: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = ApiClient.getClient().newCall(request).execute();
                String responseBody = response.body().string();

                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + responseBody);

                if (response.isSuccessful()) {
                    JsonObject jsonResponse = ApiClient.getGson().fromJson(responseBody, JsonObject.class);
                    JsonObject userData = jsonResponse.getAsJsonObject("data");
                    user = ApiClient.getGson().fromJson(userData, User.class);
                    Log.d(TAG, "User fetched successfully");
                } else if (response.code() == 404) {
                    errorMessage = "User not found";
                } else {
                    errorMessage = "Server error: " + response.code();
                }

                response.close();

            } catch (Exception e) {
                Log.e(TAG, "Error fetching user", e);
                errorMessage = "Error: " + e.getMessage();
            }

            final User finalUser = user;
            final String finalError = errorMessage;

            activity.runOnUiThread(() -> {
                if (finalUser != null) {
                    listener.onSuccess(finalUser);
                } else {
                    listener.onFailure(finalError);
                }
            });
        }).start();
    }

    // Callback interfaces
    public interface OnUserInsertListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnUserFetchListener {
        void onSuccess(User user);
        void onFailure(String error);
    }
}
