package com.dev.birdie.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ImagePickerHelper {
    private final AppCompatActivity activity;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<Intent> legacyImagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ImageSelectionListener listener;

    public interface ImageSelectionListener {
        void onImageSelected(Uri imageUri);
    }

    public ImagePickerHelper(AppCompatActivity activity) {
        this.activity = activity;
        setupImagePickers();
        setupPermissionLauncher();
    }

    public void setListener(ImageSelectionListener listener) {
        this.listener = listener;
    }

    private void setupImagePickers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            photoPickerLauncher = activity.registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {
                        if (uri != null && listener != null) {
                            listener.onImageSelected(uri);
                        }
                    }
            );
        }

        legacyImagePickerLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null && listener != null) {
                            listener.onImageSelected(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void setupPermissionLauncher() {
        permissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(activity, "Permission denied. Cannot access photos.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openImagePicker();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openImagePicker();
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            photoPickerLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent chooserIntent = Intent.createChooser(intent, "Select Photo");
            legacyImagePickerLauncher.launch(chooserIntent);
        }
    }
}