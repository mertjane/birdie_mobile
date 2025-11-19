package com.dev.birdie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dev.birdie.callbacks.AuthenticationCallback;
import com.dev.birdie.helpers.NavigationHelper;

import com.dev.birdie.helpers.ValidationHelper;
import com.dev.birdie.managers.AuthenticationManager;
import com.dev.birdie.managers.UIStateManager;
import com.dev.birdie.models.User;
import com.google.android.material.button.MaterialButton;

/**
 * Refactored LoginActivity with separated concerns
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI Components
    private EditText editTextEmail, editTextPwd;
    private TextView registerHref;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    // Managers and Helpers
    private AuthenticationManager authManager;
    private UIStateManager uiStateManager;
    private NavigationHelper navigationHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeManagers();
        setupGoogleSignIn();
        setupClickListeners();
        setupWindowInsets();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        editTextEmail = findViewById(R.id.etEmail);
        editTextPwd = findViewById(R.id.etPassword);
        registerHref = findViewById(R.id.navigateToRegister);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.loginProgress);
    }

    /**
     * Initialize managers and helpers
     */
    private void initializeManagers() {
        authManager = new AuthenticationManager(this);
        uiStateManager = new UIStateManager(
                this,
                progressBar,
                btnLogin,
                getString(R.string.loginBtnText)
        );
        navigationHelper = new NavigationHelper(this);
    }

    /**
     * Setup Google Sign-In activity result launcher
     */
    private void setupGoogleSignIn() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGoogleSignInActivityResult(result.getResultCode(), result.getData())
        );
    }

    /**
     * Setup all click listeners
     */
    private void setupClickListeners() {
        // Email/Password Login
        btnLogin.setOnClickListener(v -> handleEmailPasswordLogin());

        // Google Sign-In
        findViewById(R.id.btnGoogle).setOnClickListener(v -> handleGoogleSignInClick());

        // Navigate to Register
        registerHref.setOnClickListener(v -> navigationHelper.navigateToRegister());
    }

    /**
     * Setup window insets for edge-to-edge display
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Handle email/password login button click
     */
    private void handleEmailPasswordLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPwd.getText().toString().trim();

        // Validate input
        String validationError = ValidationHelper.validateLoginCredentials(email, password);
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading and perform login
        uiStateManager.showLoading();

        authManager.loginWithEmail(email, password, new AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess(User user) {
                uiStateManager.hideLoading();
                Toast.makeText(LoginActivity.this,
                        "Welcome back, " + user.getFullName() + "!",
                        Toast.LENGTH_SHORT).show();

                navigationHelper.navigateToMain();
            }

            @Override
            public void onAuthenticationFailure(String error) {
                uiStateManager.hideLoading();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProfileNotFound(String firebaseUid) {
                uiStateManager.hideLoading();
                Toast.makeText(LoginActivity.this,
                        "Profile not found. Please contact support.",
                        Toast.LENGTH_LONG).show();
                authManager.signOut();
            }
        });
    }

    /**
     * Handle Google Sign-In button click
     */
    private void handleGoogleSignInClick() {
        Log.d(TAG, "Google Sign-In button clicked");
        uiStateManager.showLoadingDialog("Connecting to Google...");
        Intent signInIntent = authManager.getGoogleSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    /**
     * Handle Google Sign-In activity result
     */
    private void handleGoogleSignInActivityResult(int resultCode, Intent data) {
        Log.d(TAG, "Activity result received. Result code: " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Result OK - processing sign-in");
            uiStateManager.updateLoadingMessage("Authenticating with Google...");

            authManager.handleGoogleSignInResult(data, new AuthenticationCallback() {
                @Override
                public void onAuthenticationSuccess(User user) {
                    uiStateManager.hideLoadingDialog();
                    Toast.makeText(LoginActivity.this,
                            "Welcome back, " + user.getFullName() + "!",
                            Toast.LENGTH_SHORT).show();

                    navigationHelper.navigateToMain();
                }

                @Override
                public void onAuthenticationFailure(String error) {
                    uiStateManager.hideLoadingDialog();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProfileNotFound(String firebaseUid) {
                    uiStateManager.hideLoadingDialog();
                    Toast.makeText(LoginActivity.this,
                            "Please register first using Google Sign-Up",
                            Toast.LENGTH_LONG).show();
                    authManager.signOut();
                }
            });

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "User cancelled Google Sign-In");
            uiStateManager.hideLoadingDialog();
            Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Unknown result code: " + resultCode);
            uiStateManager.hideLoadingDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiStateManager.cleanup();
    }
}