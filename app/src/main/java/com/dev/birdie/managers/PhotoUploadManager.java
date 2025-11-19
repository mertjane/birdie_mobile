package com.dev.birdie.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dev.birdie.models.Photo;
import com.dev.birdie.repositories.PhotoRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoUploadManager {
    private static final String TAG = "PhotoUploadManager";
    private final Activity activity;
    private final Map<Integer, String> uploadedPhotos = new HashMap<>();
    private PhotoUploadListener listener;

    public interface PhotoUploadListener {
        void onPhotoUploaded(int position, String imageData);

        void onPhotoUploadFailed(String error);

        void onPhotosLoaded(List<Photo> photos);
    }

    public PhotoUploadManager(Activity activity) {
        this.activity = activity;
    }

    public void setListener(PhotoUploadListener listener) {
        this.listener = listener;
    }

    public Map<Integer, String> getUploadedPhotos() {
        return uploadedPhotos;
    }

    public void loadExistingPhotos(int userId) {
        PhotoRepository.getUserPhotos(userId, activity,
                new PhotoRepository.OnPhotosListListener() {
                    @Override
                    public void onSuccess(List<Photo> photos) {
                        Log.d(TAG, "User has " + photos.size() + " photos");

                        for (Photo photo : photos) {
                            uploadedPhotos.put(photo.getUploadOrder(), photo.getPhotoUrl());
                        }

                        if (listener != null) {
                            listener.onPhotosLoaded(photos);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to load photos: " + error);
                        if (listener != null) {
                            listener.onPhotoUploadFailed(error);
                        }
                    }
                });
    }

    public void processAndUploadImage(Uri imageUri, int userId, int uploadOrder, boolean isPrimary) {
        try {
            Log.d(TAG, "Processing image: " + imageUri);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            Bitmap compressedBitmap = compressBitmap(bitmap, 600, 600);
            String base64Image = bitmapToBase64(compressedBitmap);

            Log.d(TAG, "Image converted to base64, length: " + base64Image.length());
            Toast.makeText(activity, "Uploading photo...", Toast.LENGTH_SHORT).show();

            uploadPhoto(userId, base64Image, uploadOrder, isPrimary);

        } catch (IOException e) {
            Log.e(TAG, "Error loading image", e);
            if (listener != null) {
                listener.onPhotoUploadFailed("Failed to load image: " + e.getMessage());
            }
        }
    }

    private void uploadPhoto(int userId, String base64Image, int uploadOrder, boolean isPrimary) {
        Log.d(TAG, "uploadPhoto() - position: " + uploadOrder + ", isPrimary: " + isPrimary);

        PhotoRepository.addPhoto(userId, base64Image, uploadOrder, isPrimary, activity,
                new PhotoRepository.OnPhotoAddedListener() {
                    @Override
                    public void onSuccess(Photo photo, String message) {
                        uploadedPhotos.put(uploadOrder, base64Image);

                        Log.d(TAG, "Photo uploaded successfully");
                        Toast.makeText(activity, "Photo added!", Toast.LENGTH_SHORT).show();

                        if (listener != null) {
                            listener.onPhotoUploaded(uploadOrder, base64Image);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to upload photo: " + error);
                        Toast.makeText(activity, "Failed to add photo: " + error, Toast.LENGTH_LONG).show();

                        if (listener != null) {
                            listener.onPhotoUploadFailed(error);
                        }
                    }
                });
    }

    private Bitmap compressBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min(
                ((float) maxWidth / width),
                ((float) maxHeight / height)
        );

        if (scale < 1.0f) {
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        return bitmap;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}