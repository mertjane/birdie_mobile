package com.dev.birdie.repositories;

import android.app.Activity;
import android.util.Log;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.User;
import com.dev.birdie.network.RetrofitClient;
import com.dev.birdie.services.UserServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private static UserServices userServices;

    // Initialize UserServices
    private static UserServices getUserServices() {
        if (userServices == null) {
            userServices = RetrofitClient.getInstance().createService(UserServices.class);
        }
        return userServices;
    }

    /**
     * Insert/Create a new user
     */
    public static void insertUser(User user, Activity activity, OnUserInsertListener listener) {
        Log.d(TAG, "insertUser() called for user: " + user.getEmail());

        Call<ApiResponse<User>> call = getUserServices().createUser(user);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<User> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "User created successfully");
                            listener.onSuccess();
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Unknown error";
                            Log.e(TAG, "API error: " + error);
                            listener.onFailure(error);
                        }
                    } else {
                        String error = "Server error: " + response.code();
                        Log.e(TAG, error);
                        listener.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Get user by Firebase UID
     */
    public static void getUserByFirebaseUid(String firebaseUid, Activity activity, OnUserFetchListener listener) {
        Log.d(TAG, "getUserByFirebaseUid() called for: " + firebaseUid);

        Call<ApiResponse<User>> call = getUserServices().getUserByFirebaseUid(firebaseUid);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<User> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "User fetched successfully");
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "User not found";
                            Log.e(TAG, error);
                            listener.onFailure(error);
                        }
                    } else if (response.code() == 404) {
                        Log.e(TAG, "User not found");
                        listener.onFailure("User not found");
                    } else {
                        String error = "Server error: " + response.code();
                        Log.e(TAG, error);
                        listener.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Update user details
     */
    public static void updateUserDetails(String firebaseUid, User user, Activity activity, OnUserUpdateListener listener) {
        Log.d(TAG, "updateUserDetails() called for firebase_uid: " + firebaseUid);

        Call<ApiResponse<User>> call = getUserServices().updateUser(firebaseUid, user);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<User> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "User updated successfully");
                            listener.onSuccess();
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Unknown error";
                            Log.e(TAG, "API error: " + error);
                            listener.onFailure(error);
                        }
                    } else {
                        String error = "Server error: " + response.code();
                        Log.e(TAG, error);
                        listener.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Delete user (soft delete)
     */
    public static void deleteUser(String firebaseUid, Activity activity, OnUserDeleteListener listener) {
        Log.d(TAG, "deleteUser() called for firebase_uid: " + firebaseUid);

        Call<ApiResponse<Void>> call = getUserServices().deleteUser(firebaseUid);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "User deleted successfully");
                            listener.onSuccess();
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Unknown error";
                            Log.e(TAG, "API error: " + error);
                            listener.onFailure(error);
                        }
                    } else {
                        String error = "Server error: " + response.code();
                        Log.e(TAG, error);
                        listener.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
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

    public interface OnUserUpdateListener {
        void onSuccess();

        void onFailure(String error);
    }

    public interface OnUserDeleteListener {
        void onSuccess();

        void onFailure(String error);
    }
}