package com.dev.birdie;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.birdie.helpers.ImagePickerHelper;
import com.dev.birdie.helpers.NavigationHelper;
import com.dev.birdie.helpers.OnboardingHelper;
import com.dev.birdie.helpers.PhotoDisplayHelper;
import com.dev.birdie.managers.PhotoUploadManager;
import com.dev.birdie.models.Photo;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserDetails3Activity extends AppCompatActivity {

    private static final String TAG = "UserDetails3Activity";

    // UI Components
    private LinearLayout mainPhotoContainer;
    private LinearLayout photoBox2, photoBox3, photoBox4, photoBox5;
    private ImageView ivMainPhoto, ivPhoto2, ivPhoto3, ivPhoto4, ivPhoto5;
    private MaterialButton btnBack, btnNext;
    private TextView skipForNowLink;

    // Managers & Helpers
    private NavigationHelper navigationHelper;
    private PhotoUploadManager photoUploadManager;
    private ImagePickerHelper imagePickerHelper;
    private PhotoDisplayHelper photoDisplayHelper;

    // Current user data
    private int currentUserId;
    private String currentFirebaseUid;

    // Photo selection tracking
    private int currentSelectedPosition = -1;
    private boolean currentIsPrimary = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details3);

        initializeViews();
        initializeManagers();
        setupListeners();
        getCurrentUser();
    }

    private void initializeViews() {
        mainPhotoContainer = findViewById(R.id.mainPhotoContainer);
        photoBox2 = findViewById(R.id.photoBox2);
        photoBox3 = findViewById(R.id.photoBox3);
        photoBox4 = findViewById(R.id.photoBox4);
        photoBox5 = findViewById(R.id.photoBox5);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        skipForNowLink = findViewById(R.id.skipForNowLink);
    }

    private void initializeManagers() {
        navigationHelper = new NavigationHelper(this);
        photoUploadManager = new PhotoUploadManager(this);
        imagePickerHelper = new ImagePickerHelper(this);
        photoDisplayHelper = new PhotoDisplayHelper(this);
    }

    private void setupListeners() {
        setupClickListeners();
        setupPhotoUploadListener();
        setupImagePickerListener();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> navigationHelper.navigateTo3To2());
        btnNext.setOnClickListener(v -> handleNextButton());
        skipForNowLink.setOnClickListener(v -> handleSkipForNow());

        mainPhotoContainer.setOnClickListener(v -> selectPhoto(1, true));
        photoBox2.setOnClickListener(v -> selectPhoto(2, false));
        photoBox3.setOnClickListener(v -> selectPhoto(3, false));
        photoBox4.setOnClickListener(v -> selectPhoto(4, false));
        photoBox5.setOnClickListener(v -> selectPhoto(5, false));
    }

    private void setupPhotoUploadListener() {
        photoUploadManager.setListener(new PhotoUploadManager.PhotoUploadListener() {
            @Override
            public void onPhotoUploaded(int position, String imageData) {
                displayPhotoInUI(position, imageData);
            }

            @Override
            public void onPhotoUploadFailed(String error) {
                Toast.makeText(UserDetails3Activity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPhotosLoaded(List<Photo> photos) {
                for (Photo photo : photos) {
                    displayPhotoInUI(photo.getUploadOrder(), photo.getPhotoUrl());
                }
            }
        });
    }

    private void setupImagePickerListener() {
        imagePickerHelper.setListener(imageUri -> {
            photoUploadManager.processAndUploadImage(
                    imageUri,
                    currentUserId,
                    currentSelectedPosition,
                    currentIsPrimary
            );
        });
    }

    private void getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            navigationHelper.navigateToLoginAndFinish();
            return;
        }

        currentFirebaseUid = firebaseUser.getUid();

        UserRepository.getUserByFirebaseUid(currentFirebaseUid, this,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        currentUserId = user.getUserId();
                        Log.d(TAG, "Current user_id: " + currentUserId);
                        loadExistingPhotos();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to fetch user: " + error);
                        Toast.makeText(UserDetails3Activity.this,
                                "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadExistingPhotos() {
        new android.os.Handler().postDelayed(() -> {
            if (currentUserId > 0) {
                photoUploadManager.loadExistingPhotos(currentUserId);
            }
        }, 500);
    }

    private void selectPhoto(int position, boolean isPrimary) {
        Log.d(TAG, "selectPhoto() called for position: " + position);
        currentSelectedPosition = position;
        currentIsPrimary = isPrimary;
        imagePickerHelper.checkPermissionAndOpenPicker();
    }

    private void displayPhotoInUI(int position, String imageData) {
        runOnUiThread(() -> {
            LinearLayout targetContainer = getContainerForPosition(position);
            if (targetContainer == null) return;

            ImageView imageView = photoDisplayHelper.displayPhotoInContainer(targetContainer, imageData);

            // Store reference to ImageView
            switch (position) {
                case 1:
                    ivMainPhoto = imageView;
                    break;
                case 2:
                    ivPhoto2 = imageView;
                    break;
                case 3:
                    ivPhoto3 = imageView;
                    break;
                case 4:
                    ivPhoto4 = imageView;
                    break;
                case 5:
                    ivPhoto5 = imageView;
                    break;
            }
        });
    }

    private LinearLayout getContainerForPosition(int position) {
        switch (position) {
            case 1:
                return mainPhotoContainer;
            case 2:
                return photoBox2;
            case 3:
                return photoBox3;
            case 4:
                return photoBox4;
            case 5:
                return photoBox5;
            default:
                return null;
        }
    }

    private void handleNextButton() {
        Log.d(TAG, "Next button clicked. Photos uploaded: " +
                photoUploadManager.getUploadedPhotos().size());

        if (photoUploadManager.getUploadedPhotos().size() < 2) {
            Toast.makeText(this,
                    "Please upload at least 2 photos to continue",
                    Toast.LENGTH_LONG).show();
            return;
        }

        updateOnboardingStep();
    }

    private void handleSkipForNow() {
        new AlertDialog.Builder(this)
                .setTitle("Skip Photo Upload?")
                .setMessage("Adding photos greatly increases your chances of matches. Are you sure?")
                .setPositiveButton("Skip", (dialog, which) -> updateOnboardingStep())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateOnboardingStep() {
        btnNext.setEnabled(false);
        btnNext.setText("Finishing...");

        User user = new User();
        user.setOnboardingStep(OnboardingHelper.ONBOARDING_COMPLETED);

        UserRepository.updateUserDetails(currentFirebaseUid, user, this,
                new UserRepository.OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Onboarding completed! onboarding_step = " +
                                OnboardingHelper.ONBOARDING_COMPLETED);

                        Toast.makeText(UserDetails3Activity.this,
                                "Profile complete!", Toast.LENGTH_SHORT).show();

                        // Fetch updated user and route accordingly
                        fetchUserAndRoute();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to update onboarding step: " + error);

                        btnNext.setEnabled(true);
                        btnNext.setText("Next");

                        Toast.makeText(UserDetails3Activity.this,
                                "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchUserAndRoute() {
        UserRepository.getUserByFirebaseUid(currentFirebaseUid, this,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "User fetched successfully. Routing based on onboarding step.");
                        OnboardingHelper.routeUserBasedOnOnboardingStep(
                                UserDetails3Activity.this, user);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to fetch user for routing: " + error);
                        btnNext.setEnabled(true);
                        btnNext.setText("Next");
                        Toast.makeText(UserDetails3Activity.this,
                                "Error routing: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}