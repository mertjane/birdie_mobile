package com.dev.birdie.managers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.birdie.callbacks.AuthenticationCallback;
import com.dev.birdie.helpers.GoogleSignInHelper;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Manages all authentication operations (Firebase, Google Sign-In)
 */
public class AuthenticationManager {

    private static final String TAG = "AuthenticationManager";

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInHelper googleSignInHelper;
    private final AppCompatActivity activity;

    public AuthenticationManager(AppCompatActivity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInHelper = new GoogleSignInHelper(activity);
    }

    /**
     * Performs email/password login
     */
    public void loginWithEmail(String email, String password, AuthenticationCallback callback) {
        callback.onAuthenticationStarted();

        Log.d(TAG, "Attempting Firebase login for: " + email);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                String firebaseUid = firebaseUser.getUid();
                                Log.d(TAG, "Firebase login successful. UID: " + firebaseUid);

                                // Fetch user profile from backend
                                fetchUserProfile(firebaseUid, callback);
                            } else {
                                Log.e(TAG, "Firebase user is null after successful login");
                                callback.onAuthenticationFailure("An error occurred. Please try again.");
                            }
                        } else {
                            // Firebase authentication failed
                            Log.e(TAG, "Firebase login failed", task.getException());

                            String errorMessage = "Authentication failed";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }

                            callback.onAuthenticationFailure(errorMessage);
                        }
                    }
                });
    }

    /**
     * Handles Google Sign-In result
     */
    public void handleGoogleSignInResult(Intent data, AuthenticationCallback callback) {
        googleSignInHelper.handleSignInResult(data, new GoogleSignInHelper.OnGoogleSignInListener() {
            @Override
            public void onSuccess(String firebaseUid, String email, String displayName) {
                Log.d(TAG, "Google sign-in successful: " + email);
                fetchUserProfile(firebaseUid, callback);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Google sign-in failed: " + error);
                callback.onAuthenticationFailure(error);
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
     * Fetches user profile from backend
     */
    private void fetchUserProfile(String firebaseUid, AuthenticationCallback callback) {
        UserRepository.getUserByFirebaseUid(firebaseUid, activity,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "User profile loaded: " + user.getEmail());
                        callback.onAuthenticationSuccess(user);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to load user profile: " + error);
                        callback.onProfileNotFound(firebaseUid);
                    }
                });
    }

    /**
     * Signs out from Firebase and Google
     */
    public void signOut() {
        firebaseAuth.signOut();
        googleSignInHelper.signOut();
    }

    /**
     * Gets current Firebase user
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Checks if user is logged in
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}