package com.dev.birdie.repositories;

import android.app.Activity;
import android.util.Log;

import com.dev.birdie.models.ApiResponse;
import com.dev.birdie.models.Photo;
import com.dev.birdie.dtos.PhotoRequests.*;
import com.dev.birdie.network.RetrofitClient;
import com.dev.birdie.services.PhotoService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

public class PhotoRepository {

    private static final String TAG = "PhotoRepository";
    private static PhotoService photoService;

    // Initialize PhotoService
    private static PhotoService getPhotoService() {
        if (photoService == null) {
            photoService = RetrofitClient.getInstance().createService(PhotoService.class);
        }
        return photoService;
    }

    /**
     * Get all photos for a user
     */
    public static void getUserPhotos(int userId, Activity activity, OnPhotosListListener listener) {
        Log.d(TAG, "getUserPhotos() called for userId: " + userId);

        Call<ApiResponse<List<Photo>>> call = getPhotoService().getUserPhotos(userId);

        call.enqueue(new Callback<ApiResponse<List<Photo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Photo>>> call,
                                   Response<ApiResponse<List<Photo>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Photo>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Photos fetched successfully: " + apiResponse.getData().size() + " photos");
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to fetch photos";
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
            public void onFailure(Call<ApiResponse<List<Photo>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Get photo count for a user
     */
    public static void getUserPhotoCount(int userId, Activity activity, OnPhotoCountListener listener) {
        Log.d(TAG, "getUserPhotoCount() called for userId: " + userId);

        Call<ApiResponse<PhotoCountResponse>> call = getPhotoService().getUserPhotoCount(userId);

        call.enqueue(new Callback<ApiResponse<PhotoCountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PhotoCountResponse>> call,
                                   Response<ApiResponse<PhotoCountResponse>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<PhotoCountResponse> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            int count = apiResponse.getData().getCount();
                            Log.d(TAG, "Photo count: " + count);
                            listener.onSuccess(count);
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to get count";
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
            public void onFailure(Call<ApiResponse<PhotoCountResponse>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Get primary photo for a user
     */
    public static void getPrimaryPhoto(int userId, Activity activity, OnPhotoListener listener) {
        Log.d(TAG, "getPrimaryPhoto() called for userId: " + userId);

        Call<ApiResponse<Photo>> call = getPhotoService().getPrimaryPhoto(userId);

        call.enqueue(new Callback<ApiResponse<Photo>>() {
            @Override
            public void onResponse(Call<ApiResponse<Photo>> call,
                                   Response<ApiResponse<Photo>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Photo> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Primary photo fetched successfully");
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "No primary photo found";
                            Log.e(TAG, error);
                            listener.onFailure(error);
                        }
                    } else if (response.code() == 404) {
                        Log.e(TAG, "No primary photo found");
                        listener.onFailure("No primary photo found");
                    } else {
                        String error = "Server error: " + response.code();
                        Log.e(TAG, error);
                        listener.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<Photo>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Add a photo for a user
     */
    public static void addPhoto(int userId, String base64Image, int uploadOrder,
                                boolean isPrimary, Activity activity, OnPhotoAddedListener listener) {
        Log.d(TAG, "addPhoto() called - userId: " + userId + ", order: " + uploadOrder);

        AddPhotoRequest request = new AddPhotoRequest(base64Image, uploadOrder, isPrimary);
        Call<ApiResponse<Photo>> call = getPhotoService().addPhoto(userId, request);

        call.enqueue(new Callback<ApiResponse<Photo>>() {
            @Override
            public void onResponse(Call<ApiResponse<Photo>> call,
                                   Response<ApiResponse<Photo>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Photo> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Photo added successfully");
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to add photo";
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
            public void onFailure(Call<ApiResponse<Photo>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Update a photo
     */
    public static void updatePhoto(int photoId, String photoUrl, Boolean isPrimary,
                                   Activity activity, OnPhotoUpdatedListener listener) {
        Log.d(TAG, "updatePhoto() called for photoId: " + photoId);

        UpdatePhotoRequest request = new UpdatePhotoRequest(photoUrl, isPrimary);
        Call<ApiResponse<Photo>> call = getPhotoService().updatePhoto(photoId, request);

        call.enqueue(new Callback<ApiResponse<Photo>>() {
            @Override
            public void onResponse(Call<ApiResponse<Photo>> call,
                                   Response<ApiResponse<Photo>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Photo> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Photo updated successfully");
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to update photo";
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
            public void onFailure(Call<ApiResponse<Photo>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Set a photo as primary
     */
    public static void setPrimaryPhoto(int photoId, Activity activity, OnPhotoUpdatedListener listener) {
        Log.d(TAG, "setPrimaryPhoto() called for photoId: " + photoId);

        Call<ApiResponse<Photo>> call = getPhotoService().setPrimaryPhoto(photoId);

        call.enqueue(new Callback<ApiResponse<Photo>>() {
            @Override
            public void onResponse(Call<ApiResponse<Photo>> call,
                                   Response<ApiResponse<Photo>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Photo> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Photo set as primary successfully");
                            listener.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to set primary";
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
            public void onFailure(Call<ApiResponse<Photo>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Reorder photos for a user
     */
    public static void reorderPhotos(int userId, Map<String, Integer> photoOrders,
                                     Activity activity, OnPhotosListListener listener) {
        Log.d(TAG, "reorderPhotos() called for userId: " + userId);

        ReorderPhotosRequest request = new ReorderPhotosRequest(photoOrders);
        Call<ApiResponse<List<Photo>>> call = getPhotoService().reorderPhotos(userId, request);

        call.enqueue(new Callback<ApiResponse<List<Photo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Photo>>> call,
                                   Response<ApiResponse<List<Photo>>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Photo>> apiResponse = response.body();

                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            Log.d(TAG, "Photos reordered successfully");
                            listener.onSuccess(apiResponse.getData());
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to reorder photos";
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
            public void onFailure(Call<ApiResponse<List<Photo>>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                activity.runOnUiThread(() -> {
                    listener.onFailure("Network error: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Delete a photo
     */
    public static void deletePhoto(int photoId, Activity activity, OnPhotoDeletedListener listener) {
        Log.d(TAG, "deletePhoto() called for photoId: " + photoId);

        Call<ApiResponse<Void>> call = getPhotoService().deletePhoto(photoId);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "Photo deleted successfully");
                            String message = apiResponse.getMessage() != null ?
                                    apiResponse.getMessage() : "Photo deleted";
                            listener.onSuccess(message);
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to delete photo";
                            Log.e(TAG, "API error: " + error);
                            listener.onFailure(error);
                        }
                    } else if (response.code() == 404) {
                        Log.e(TAG, "Photo not found");
                        listener.onFailure("Photo not found");
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
     * Delete all photos for a user
     */
    public static void deleteAllUserPhotos(int userId, Activity activity, OnPhotoDeletedListener listener) {
        Log.d(TAG, "deleteAllUserPhotos() called for userId: " + userId);

        Call<ApiResponse<Void>> call = getPhotoService().deleteAllUserPhotos(userId);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "All photos deleted successfully");
                            String message = apiResponse.getMessage() != null ?
                                    apiResponse.getMessage() : "All photos deleted";
                            listener.onSuccess(message);
                        } else {
                            String error = apiResponse.getError() != null ?
                                    apiResponse.getError() : "Failed to delete photos";
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
    public interface OnPhotosListListener {
        void onSuccess(List<Photo> photos);

        void onFailure(String error);
    }

    public interface OnPhotoListener {
        void onSuccess(Photo photo);

        void onFailure(String error);
    }

    public interface OnPhotoCountListener {
        void onSuccess(int count);

        void onFailure(String error);
    }

    public interface OnPhotoAddedListener {
        void onSuccess(Photo photo, String message);

        void onFailure(String error);
    }

    public interface OnPhotoUpdatedListener {
        void onSuccess(Photo photo, String message);

        void onFailure(String error);
    }

    public interface OnPhotoDeletedListener {
        void onSuccess(String message);

        void onFailure(String error);
    }
}