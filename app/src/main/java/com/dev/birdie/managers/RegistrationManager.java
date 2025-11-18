package com.dev.birdie.managers;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.birdie.callbacks.RegistrationCallback;
import com.dev.birdie.helpers.GoogleSignInHelper;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Manages all registration operations
 */
public class RegistrationManager {

    private static final String TAG = "RegistrationManager";

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInHelper googleSignInHelper;
    private final AppCompatActivity activity;

    public RegistrationManager(AppCompatActivity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInHelper = new GoogleSignInHelper(activity);
    }

    /**
     * Registers user with email and password
     */
    public void registerWithEmail(String name, String email, String password,
                                  RegistrationCallback callback) {
        callback.onRegistrationStarted();

        Log.d(TAG, "Attempting Firebase registration for: " + email);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                String firebaseUid = firebaseUser.getUid();
                                Log.d(TAG, "Firebase registration successful. UID: " + firebaseUid);

                                // Create user in backend
                                createUserInBackend(firebaseUid, email, name, callback);
                            } else {
                                Log.e(TAG, "Firebase user is null after registration");
                                callback.onRegistrationFailure("An error occurred. Please try again.");
                            }
                        } else {
                            Log.e(TAG, "Firebase registration failed", task.getException());

                            String errorMessage = "Registration failed";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }

                            callback.onRegistrationFailure(errorMessage);
                        }
                    }
                });
    }

    /**
     * Handles Google Sign-In result for registration
     */
    public void handleGoogleSignInResult(Intent data, RegistrationCallback callback) {
        googleSignInHelper.handleSignInResult(data, new GoogleSignInHelper.OnGoogleSignInListener() {
            @Override
            public void onSuccess(String firebaseUid, String email, String displayName) {
                Log.d(TAG, "Google sign-in successful: " + email);
                createUserInBackend(firebaseUid, email, displayName, callback);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Google sign-in failed: " + error);
                callback.onRegistrationFailure(error);
            }
        });
    }

    /**
     * Gets Google Sign-In intent
     */
    public Intent getGoogleSignInIntent() {
        return googleSignInHelper.getSignInIntent();
    }

    /**
     * Creates user in backend database
     */
    private void createUserInBackend(String firebaseUid, String email, String name,
                                     RegistrationCallback callback) {
        User newUser = new User();
        newUser.setFirebaseUid(firebaseUid);
        newUser.setEmail(email);
        newUser.setFullName(name);

        Log.d(TAG, "Creating user in backend database");

        UserRepository.insertUser(newUser, activity,
                new UserRepository.OnUserInsertListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Database insertion successful!");
                        callback.onRegistrationSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Database insertion failed: " + error);

                        // Check if user already exists (409 conflict)
                        if (error.contains("409") || error.contains("already exists")) {
                            callback.onUserAlreadyExists();
                        } else {
                            callback.onRegistrationFailure("Database error: " + error);
                        }
                    }
                });
    }
}