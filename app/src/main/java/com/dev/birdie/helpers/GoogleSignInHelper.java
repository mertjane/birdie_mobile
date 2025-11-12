package com.dev.birdie.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInHelper {
    private static final String TAG = "GoogleSignInHelper";
    private static final String WEB_CLIENT_ID = "860142977851-f98ud7kgrsnkhpbmk81nog87jcu91ldl.apps.googleusercontent.com"; // Replace with your actual Web Client ID

    private Activity activity;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    public GoogleSignInHelper(Activity activity) {
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public void handleSignInResult(Intent data, OnGoogleSignInListener listener) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "Google sign in successful: " + account.getEmail());
            firebaseAuthWithGoogle(account.getIdToken(), listener);
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed", e);
            listener.onFailure("Google sign in failed: " + e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(String idToken, OnGoogleSignInListener listener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        String firebaseUid = mAuth.getCurrentUser().getUid();
                        String email = mAuth.getCurrentUser().getEmail();
                        String displayName = mAuth.getCurrentUser().getDisplayName();

                        listener.onSuccess(firebaseUid, email, displayName);
                    } else {
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        listener.onFailure("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        googleSignInClient.signOut();
    }

    public interface OnGoogleSignInListener {
        void onSuccess(String firebaseUid, String email, String displayName);

        void onFailure(String error);
    }
}
