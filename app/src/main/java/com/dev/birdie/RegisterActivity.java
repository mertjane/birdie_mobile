package com.dev.birdie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dev.birdie.callbacks.RegistrationCallback;
import com.dev.birdie.helpers.NavigationHelper;
import com.dev.birdie.helpers.ValidationHelper;
import com.dev.birdie.managers.RegistrationManager;
import com.dev.birdie.managers.UIStateManager;
import com.google.android.material.button.MaterialButton;

/**
 * Refactored RegisterActivity with separated concerns
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // UI Components
    private EditText editTextName, editTextEmail, editTextPwd, editTextConfirmPwd;
    private TextView loginHref;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    // Managers and Helpers
    private RegistrationManager registrationManager;
    private UIStateManager uiStateManager;
    private NavigationHelper navigationHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

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
        editTextName = findViewById(R.id.etName);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPwd = findViewById(R.id.etPassword);
        editTextConfirmPwd = findViewById(R.id.etConfirmPassword);
        loginHref = findViewById(R.id.navigateToLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.registerProgress);
    }

    /**
     * Initialize managers and helpers
     */
    private void initializeManagers() {
        registrationManager = new RegistrationManager(this);
        uiStateManager = new UIStateManager(
                this,
                progressBar,
                btnRegister,
                getString(R.string.registerBtnTxt)
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
        // Email/Password Registration
        btnRegister.setOnClickListener(v -> handleEmailPasswordRegistration());

        // Google Sign-In
        findViewById(R.id.btnGoogle).setOnClickListener(v -> handleGoogleSignInClick());

        // Navigate to Login
        loginHref.setOnClickListener(v -> navigationHelper.navigateToLogin());
    }

    /**
     * Setup window insets for edge-to-edge display
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Handle email/password registration button click
     */
    private void handleEmailPasswordRegistration() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPwd.getText().toString().trim();
        String confirmPassword = editTextConfirmPwd.getText().toString().trim();

        // Validate input
        String validationError = ValidationHelper.validateRegistrationCredentials(
                name, email, password, confirmPassword);

        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading and perform registration
        uiStateManager.showLoading();

        registrationManager.registerWithEmail(name, email, password, new RegistrationCallback() {
            @Override
            public void onRegistrationSuccess() {
                uiStateManager.hideLoading();
                Toast.makeText(RegisterActivity.this,
                        "Registration successful!",
                        Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToLoginAndFinish();
            }

            @Override
            public void onRegistrationFailure(String error) {
                uiStateManager.hideLoading();
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUserAlreadyExists() {
                uiStateManager.hideLoading();
                Toast.makeText(RegisterActivity.this,
                        "Account already exists. Please login.",
                        Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToLoginAndFinish();
            }
        });
    }

    /**
     * Handle Google Sign-In button click
     */
    private void handleGoogleSignInClick() {
        Log.d(TAG, "Google Sign-In button clicked");
        Intent signInIntent = registrationManager.getGoogleSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    /**
     * Handle Google Sign-In activity result
     */
    private void handleGoogleSignInActivityResult(int resultCode, Intent data) {
        Log.d(TAG, "Activity result received. Result code: " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Result OK - processing sign-in");
            uiStateManager.showLoading();

            registrationManager.handleGoogleSignInResult(data, new RegistrationCallback() {
                @Override
                public void onRegistrationSuccess() {
                    uiStateManager.hideLoading();
                    Toast.makeText(RegisterActivity.this,
                            "Registration successful!",
                            Toast.LENGTH_SHORT).show();
                    navigationHelper.navigateToMain();
                }

                @Override
                public void onRegistrationFailure(String error) {
                    uiStateManager.hideLoading();
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onUserAlreadyExists() {
                    uiStateManager.hideLoading();
                    Toast.makeText(RegisterActivity.this,
                            "Welcome back!",
                            Toast.LENGTH_SHORT).show();
                    navigationHelper.navigateToMain();
                }
            });

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "User cancelled Google Sign-In");
            Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Unknown result code: " + resultCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiStateManager.cleanup();
    }
}