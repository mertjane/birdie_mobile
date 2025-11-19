package com.dev.birdie.repositories;

import android.app.Activity;
import android.util.Log;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.UserInterests;
import com.dev.birdie.models.UserInterestRequests.*;
import com.dev.birdie.network.RetrofitClient;
import com.dev.birdie.services.UserInterestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class UserInterestsRepository {

    private static final String TAG = "UserInterestsRepository";
    private static UserInterestService userInterestService;

    // Initialize UserInterestService
    private static UserInterestService getUserInterestService() {
        if (userInterestService == null) {
            userInterestService = RetrofitClient.getInstance().createService(UserInterestService.class);
        }
        return userInterestService;
    }

    /**
     * Get all interests for a user
     */
    public static void getUserInterests(int userId, Activity activity, OnInterestsListListener listener) {
        Log.d(TAG, "getUserInterests() called for userId: " + userId);

        Call<ApiResponse<List<UserInterests>>> call = getUserInterestService().getUserInterests(userId);

        call.enqueue(new Callback<ApiResponse<List<UserInterests>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserInterests>>> call,
                                   Response<ApiResponse<List<UserInterests>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<UserInterests>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Interests fetched successfully: " + apiResponse.getData().size() + " interests");
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to fetch interests";
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
            public void onFailure(Call<ApiResponse<List<UserInterests>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Add a single interest to a user
     */
    public static void addUserInterest(int userId, String interestName,
                                       Activity activity, OnInterestAddedListener listener) {
        Log.d(TAG, "addUserInterest() called - userId: " + userId + ", interest: " + interestName);

        AddInterestRequest request = new AddInterestRequest(interestName);
        Call<ApiResponse<UserInterests>> call = getUserInterestService().addUserInterest(userId, request);

        call.enqueue(new Callback<ApiResponse<UserInterests>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInterests>> call,
                                   Response<ApiResponse<UserInterests>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<UserInterests> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Interest added successfully");
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to add interest";
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
            public void onFailure(Call<ApiResponse<UserInterests>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Add multiple interests to a user at once
     */
    public static void addMultipleUserInterests(int userId, List<String> interests,
                                                Activity activity, OnMultipleInterestsListener listener) {
        Log.d(TAG, "addMultipleUserInterests() called - userId: " + userId +
                ", count: " + interests.size());

        AddMultipleInterestsRequest request = new AddMultipleInterestsRequest(interests);
        Call<ApiResponse<List<UserInterests>>> call =
                getUserInterestService().addMultipleUserInterests(userId, request);

        call.enqueue(new Callback<ApiResponse<List<UserInterests>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserInterests>>> call,
                                   Response<ApiResponse<List<UserInterests>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<UserInterests>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Multiple interests added: " + apiResponse.getData().size());
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to add interests";
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
            public void onFailure(Call<ApiResponse<List<UserInterests>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Update (replace) all interests for a user
     */
    public static void updateUserInterests(int userId, List<String> interests,
                                           Activity activity, OnMultipleInterestsListener listener) {
        Log.d(TAG, "updateUserInterests() called - userId: " + userId +
                ", new count: " + interests.size());

        UpdateInterestsRequest request = new UpdateInterestsRequest(interests);
        Call<ApiResponse<List<UserInterests>>> call =
                getUserInterestService().updateUserInterests(userId, request);

        call.enqueue(new Callback<ApiResponse<List<UserInterests>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserInterests>>> call,
                                   Response<ApiResponse<List<UserInterests>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<UserInterests>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Interests updated successfully");
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to update interests";
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
            public void onFailure(Call<ApiResponse<List<UserInterests>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Remove a specific interest from a user
     */
    public static void removeUserInterest(int userId, String interestName,
                                          Activity activity, OnInterestRemovedListener listener) {
        Log.d(TAG, "removeUserInterest() called - userId: " + userId +
                ", interest: " + interestName);

        Call<ApiResponse<Void>> call =
                getUserInterestService().removeUserInterest(userId, interestName);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "Interest removed successfully");
                            String message = apiResponse.getMessage() != null ?
                                    apiResponse.getMessage() : "Interest removed";
                            listener.onSuccess(message);
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to remove interest";
                            Log.e(TAG, "API error: " + error);
                            listener.onFailure(error);
                        }
                    } else if (response.code() == 404) {
                        Log.e(TAG, "Interest not found");
                        listener.onFailure("Interest not found");
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

    /**
     * Remove all interests for a user
     */
    public static void removeAllUserInterests(int userId, Activity activity,
                                              OnInterestRemovedListener listener) {
        Log.d(TAG, "removeAllUserInterests() called - userId: " + userId);

        Call<ApiResponse<Void>> call = getUserInterestService().removeAllUserInterests(userId);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "All interests removed successfully");
                            String message = apiResponse.getMessage() != null ?
                                    apiResponse.getMessage() : "All interests removed";
                            listener.onSuccess(message);
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to remove interests";
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

    /**
     * Get list of available interest options
     */
    public static void getAvailableInterests(Activity activity, OnAvailableInterestsListener listener) {
        Log.d(TAG, "getAvailableInterests() called");

        Call<ApiResponse<List<String>>> call = getUserInterestService().getAvailableInterests();

        call.enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call,
                                   Response<ApiResponse<List<String>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<String>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Available interests fetched: " + apiResponse.getData().size());
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to fetch available interests";
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
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    // Callback interfaces
    public interface OnInterestsListListener {
        void onSuccess(List<UserInterests> interests);

        void onFailure(String error);
    }

    public interface OnInterestAddedListener {
        void onSuccess(UserInterests interest, String message);

        void onFailure(String error);
    }

    public interface OnMultipleInterestsListener {
        void onSuccess(List<UserInterests> interests, String message);

        void onFailure(String error);
    }

    public interface OnInterestRemovedListener {
        void onSuccess(String message);

        void onFailure(String error);
    }

    public interface OnAvailableInterestsListener {
        void onSuccess(List<String> interests);

        void onFailure(String error);
    }
}