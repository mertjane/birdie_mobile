package com.dev.birdie;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.birdie.helpers.NavigationHelper;
import com.dev.birdie.helpers.PhotoDisplayHelper;
import com.dev.birdie.models.Photo;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.PhotoRepository;
import com.dev.birdie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth; // Import Firebase Auth

public class ProfileFragment extends Fragment {

    // UI Components
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtInitials;
    private ImageView imgProfile;
    private LinearLayout btnSignOut;

    private PhotoDisplayHelper photoHelper;
    private NavigationHelper navigationHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoHelper = new PhotoDisplayHelper(getContext());
        navigationHelper = new NavigationHelper(getActivity());

        initViews(view);
        setupClickListeners();

        // Start the data fetching process
        fetchCurrentUser();
    }

    private void initViews(View view) {
        txtName = view.findViewById(R.id.userName);
        txtEmail = view.findViewById(R.id.userEmail);
        txtInitials = view.findViewById(R.id.profileInitials);
        imgProfile = view.findViewById(R.id.profileImage);

        // Bind the Sign Out button from XML
        btnSignOut = view.findViewById(R.id.optSignOut);
    }

    private void setupClickListeners() {
        // Sign Out Logic
        btnSignOut.setOnClickListener(v -> {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Clear any other session data if you have it (e.g. SharedPrefs)
            // Example: SharedPrefManager.getInstance(getContext()).clear();

            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();

            // 3. Navigate to Login and kill current activity
            navigationHelper.navigateToLoginAndFinish();
        });

        // You can add listeners for other buttons here later:
        // view.findViewById(R.id.optEditName).setOnClickListener(...);
    }

    private void fetchCurrentUser() {
        // 1. Get the Firebase UID of the logged-in user
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. Call UserRepository to get User details
        UserRepository.getUserByFirebaseUid(currentUid, getActivity(), new UserRepository.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                // User data fetched successfully
                updateUserUI(user);

                // 3. Now that we have the User ID, fetch the Primary Photo
                if (user.getUserId() != null) {
                    fetchUserPhoto(user.getUserId());
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle error (e.g., user not found in DB)
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchUserPhoto(int userId) {
        // Call PhotoRepository to get the primary photo
        PhotoRepository.getPrimaryPhoto(userId, getActivity(), new PhotoRepository.OnPhotoListener() {
            @Override
            public void onSuccess(Photo photo) {
                // Photo fetched successfully
                if (photo != null && photo.getPhotoUrl() != null) {
                    loadPhotoIntoView(photo.getPhotoUrl());
                }
            }

            @Override
            public void onFailure(String error) {
                // It's okay if this fails (maybe user hasn't uploaded a photo yet)
                // We just keep showing the initials
                Log.e("ProfileFragment", "Photo fetch failed: " + error);
            }
        });
    }

    private void updateUserUI(User user) {
        if (user == null) return;

        // Set Name
        String name = user.getFullName() != null ? user.getFullName() : "User";
        txtName.setText(name);

        // Set Email
        String email = user.getEmail() != null ? user.getEmail() : "";
        txtEmail.setText(email);

        // Set Initials
        txtInitials.setText(getInitials(name));
        txtInitials.setVisibility(View.VISIBLE); // Show initials by default
    }

    private void loadPhotoIntoView(String url) {
        // Use your helper to load the image
        photoHelper.loadProfileImage(imgProfile, url);

        // Hide the initials because the photo is now covering them
        txtInitials.setVisibility(View.INVISIBLE);
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
                if (initials.length() >= 2) break;
            }
        }
        return initials.toString().toUpperCase();
    }
}