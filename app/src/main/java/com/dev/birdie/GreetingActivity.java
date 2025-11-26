
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

    // FIX: Re-enable button if user presses "Back" and returns here
    @Override
    protected void onResume() {
        super.onResume();
        if (btnGetStarted != null) {
            btnGetStarted.setEnabled(true);
            btnGetStarted.setText("Get Started");
        }
    }

    private void handleGetStarted() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Log.e(TAG, "No authenticated user found!");
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Disable button to prevent double-clicks
        btnGetStarted.setEnabled(false);
        btnGetStarted.setText("Loading...");

        String firebaseUid = firebaseUser.getUid();

        UserRepository.getUserByFirebaseUid(firebaseUid, this, new UserRepository.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                routeToNextScreen(user);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load user: " + error);
                btnGetStarted.setEnabled(true);
                btnGetStarted.setText("Get Started");
                Toast.makeText(GreetingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void routeToNextScreen(User user) {
        Integer onboardingStep = user.getOnboardingStep();
        if (onboardingStep == null) onboardingStep = 0;

        Intent intent;
        boolean shouldFinish = false;

        switch (onboardingStep) {
            case 0:
                intent = new Intent(this, UserDetails1Activity.class);
                break;
            case 1:
                intent = new Intent(this, UserDetails2Activity.class);
                break;
            case 2:
                intent = new Intent(this, UserDetails3Activity.class);
                break;
            case 3: // Completed
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shouldFinish = true;
                break;
            default:
                intent = new Intent(this, UserDetails1Activity.class);
                break;
        }

        startActivity(intent);

        if (shouldFinish) {
            finish();
        }
    }
}