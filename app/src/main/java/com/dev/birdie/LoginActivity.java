package com.dev.birdie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.dev.birdie.helpers.GoogleSignInHelper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dev.birdie.helpers.GoogleSignInHelper;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


    private GoogleSignInHelper googleSignInHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    EditText editTextEmail, editTextPwd;
    TextView registerHref;
    MaterialButton btnLogin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Google Sign-In Helper
        googleSignInHelper = new GoogleSignInHelper(this);

        // Register activity result launcher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Activity result received. Result code: " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Log.d(TAG, "Result OK - processing sign-in");

                        googleSignInHelper.handleSignInResult(data, new GoogleSignInHelper.OnGoogleSignInListener() {
                            @Override
                            public void onSuccess(String firebaseUid, String email, String displayName) {
                                Log.d(TAG, "Google sign-in successful: " + email);

                                // Fetch user profile from backend
                                fetchUserAndNavigate(firebaseUid, displayName);
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e(TAG, "Google sign-in failed: " + error);
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d(TAG, "User cancelled Google Sign-In");
                        Toast.makeText(LoginActivity.this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Unknown result code: " + result.getResultCode());
                    }
                }
        );

        // Find Google Sign-In button and set click listener
        findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            progressBar.setVisibility(View.VISIBLE);
            Intent signInIntent = googleSignInHelper.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /** Navigate to Register Activity **/
        registerHref = findViewById(R.id.navigateToRegister);
        registerHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Register Activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        /** Login with email&password block **/
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.etEmail);
        editTextPwd = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.loginProgress);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email, pwd;

            email = String.valueOf(editTextEmail.getText());
            pwd = String.valueOf(editTextPwd.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(pwd)) {
                Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            }

            // Show progress
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("");

            Log.d(TAG, "Attempting Firebase login for: " + email);


            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            btnLogin.setText(getString(R.string.loginBtnText));

                            if (task.isSuccessful()) {
                                // Firebase authentication successful
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                if (firebaseUser != null) {
                                    String firebaseUid = firebaseUser.getUid();
                                    Log.d(TAG, "Firebase login successful. UID: " + firebaseUid);

                                    // Fetch user profile from backend
                                    UserRepository.getUserByFirebaseUid(firebaseUid, LoginActivity.this,
                                            new UserRepository.OnUserFetchListener() {
                                                @Override
                                                public void onSuccess(User user) {
                                                    Log.d(TAG, "User profile loaded: " + user.getEmail());

                                                    // Hide progress
                                                    progressBar.setVisibility(View.GONE);
                                                    btnLogin.setEnabled(true);
                                                    btnLogin.setText(getString(R.string.loginBtnText));

                                                    Toast.makeText(LoginActivity.this,
                                                            "Welcome back, " + user.getFullName() + "!",
                                                            Toast.LENGTH_SHORT).show();

                                                    // TODO: Save user data locally (SharedPreferences or Room)
                                                    // For now, just navigate to the main screen

                                                    Intent intent = new Intent(LoginActivity.this, GreetingActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                                @Override
                                                public void onFailure(String error) {
                                                    Log.e(TAG, "Failed to load user profile: " + error);

                                                    // Hide progress
                                                    progressBar.setVisibility(View.GONE);
                                                    btnLogin.setEnabled(true);
                                                    btnLogin.setText(getString(R.string.loginBtnText));

                                                    // User exists in Firebase but not in database
                                                    // This shouldn't happen in normal flow
                                                    Toast.makeText(LoginActivity.this,
                                                            "Profile not found. Please contact support.",
                                                            Toast.LENGTH_LONG).show();

                                                    // Sign out from Firebase since profile doesn't exist
                                                    mAuth.signOut();
                                                }
                                            });
                                } else {
                                    Log.e(TAG, "Firebase user is null after successful login");

                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    btnLogin.setText(getString(R.string.loginBtnText));

                                    Toast.makeText(LoginActivity.this,
                                            "An error occurred. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                // Firebase authentication failed
                                Log.e(TAG, "Firebase login failed", task.getException());

                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                btnLogin.setText(getString(R.string.loginBtnText));

                                String errorMessage = "Authentication failed";
                                if (task.getException() != null) {
                                    errorMessage = task.getException().getMessage();
                                }

                                Toast.makeText(LoginActivity.this, errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private void fetchUserAndNavigate(String firebaseUid, String displayName) {
        progressBar.setVisibility(View.VISIBLE);

        UserRepository.getUserByFirebaseUid(firebaseUid, LoginActivity.this,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "User profile loaded: " + user.getEmail());

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(LoginActivity.this,
                                "Welcome back, " + user.getFullName() + "!",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, GreetingActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to load user profile: " + error);

                        progressBar.setVisibility(View.GONE);

                        // User signed in with Google but not registered in our system
                        Toast.makeText(LoginActivity.this,
                                "Please register first using Google Sign-Up",
                                Toast.LENGTH_LONG).show();

                        // Sign out from Firebase
                        mAuth.signOut();
                        googleSignInHelper.signOut();
                    }
                });
    }
}