package com.dev.birdie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GreetingActivity extends AppCompatActivity {

    private static final String TAG = "GreetingActivity";

    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_greeting);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.greeting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> handleGetStarted());
    }

    /**
     * Handles Get Started button - fetches user profile and routes based on onboarding_step
     */
    private void handleGetStarted() {
        // Get current Firebase user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Log.e(TAG, "No authenticated user found!");
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();

            // Navigate back to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String firebaseUid = firebaseUser.getUid();
        Log.d(TAG, "Fetching user profile for UID: " + firebaseUid);

        // Disable button and show loading state
        btnGetStarted.setEnabled(false);
        btnGetStarted.setText("Loading...");

        // Fetch user profile to check onboarding_step
        UserRepository.getUserByFirebaseUid(firebaseUid, this,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "========== USER PROFILE LOADED ==========");
                        Log.d(TAG, "Email: " + user.getEmail());
                        Log.d(TAG, "Name: " + user.getFullName());
                        Log.d(TAG, "Onboarding Step: " + user.getOnboardingStep());
                        Log.d(TAG, "========================================");

                        // Route based on onboarding_step
                        routeToNextScreen(user);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to load user profile: " + error);

                        // Re-enable button
                        btnGetStarted.setEnabled(true);
                        btnGetStarted.setText("Get Started");

                        Toast.makeText(GreetingActivity.this,
                                "Error loading profile: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Routes to the appropriate screen based on onboarding_step
     */
    private void routeToNextScreen(User user) {
        Integer onboardingStep = user.getOnboardingStep();

        // Default to 0 if null
        if (onboardingStep == null) {
            Log.w(TAG, "onboarding_step is NULL, defaulting to 0");
            onboardingStep = 0;
        }

        Log.d(TAG, "Routing user with onboarding_step: " + onboardingStep);

        Intent intent;

        switch (onboardingStep) {
            case 0: // Not started - go to UserDetails1
                Log.d(TAG, "→ Routing to UserDetails1Activity");
                intent = new Intent(this, UserDetails1Activity.class);
                break;

            case 1: // Step 1 completed - go to UserDetails2
                Log.d(TAG, "→ Routing to UserDetails2Activity");
                intent = new Intent(this, UserDetails2Activity.class);
                break;

            case 2: // Step 2 completed - go to UserDetails3
                Log.d(TAG, "→ Routing to UserDetails3Activity");
                intent = new Intent(this, UserDetails3Activity.class);
                break;

            case 3: // Onboarding completed - stay here or go to main app
                Log.d(TAG, "Onboarding completed! User profile is complete.");
                Toast.makeText(this, "Welcome! Your profile is complete.", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to main app when ready
                return; // Don't start new activity yet

            default: // Unknown - default to UserDetails1
                Log.w(TAG, "→ Unknown onboarding_step: " + onboardingStep + ", defaulting to UserDetails1");
                intent = new Intent(this, UserDetails1Activity.class);
                break;
        }

        startActivity(intent);
        // Don't finish() here - allow back navigation to greeting screen
    }
}