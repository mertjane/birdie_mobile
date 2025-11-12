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

import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private GoogleSignInHelper googleSignInHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private static final String TAG = "RegisterActivity";

    EditText editTextName, editTextEmail, editTextPwd, editTextConfirmPwd;
    MaterialButton btnRegister;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

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
                                createUserInBackend(firebaseUid, email, displayName);
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e(TAG, "Google sign-in failed: " + error);
                                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d(TAG, "User cancelled Google Sign-In");
                        Toast.makeText(RegisterActivity.this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Unknown result code: " + result.getResultCode());
                    }
                }
        );

        // Find Google Sign-In button and set click listener
        // Make sure you have a button with this ID in your layout
        findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            Intent signInIntent = googleSignInHelper.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /** Navigate Login Activity **/
        TextView loginHref = findViewById(R.id.navigateToLogin);

        loginHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Register Activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        /** Registeration Block **/
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.etName);
        editTextEmail = findViewById(R.id.etEmail);
        editTextPwd = findViewById(R.id.etPassword);
        editTextConfirmPwd = findViewById(R.id.etConfirmPassword);
        progressBar = findViewById(R.id.registerProgress);
        btnRegister = findViewById(R.id.btnRegister);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name, email, pwd, cPwd;
                name = String.valueOf(editTextName.getText());
                email = String.valueOf(editTextEmail.getText());
                pwd = String.valueOf(editTextPwd.getText());
                cPwd = String.valueOf(editTextConfirmPwd.getText());

                if(TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(pwd)) {
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(cPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                }

                if (!pwd.equals(cPwd)) {
                    Toast.makeText(RegisterActivity.this, "Passwords should match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress
                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
                btnRegister.setText("");

                mAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "Firebase registration complete. Success: " + task.isSuccessful());

                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    if (firebaseUser != null) {
                                        String firebaseUid = firebaseUser.getUid();
                                        Log.d(TAG, "Firebase UID: " + firebaseUid);

                                        // Create User object
                                        User newUser = new User();
                                        newUser.setFirebaseUid(firebaseUid);
                                        newUser.setEmail(email);
                                        newUser.setFullName(name);

                                        Log.d(TAG, "User object created, calling UserRepository.insertUser");

                                        // Insert into Neon database
                                        UserRepository.insertUser(newUser, RegisterActivity.this,
                                                new UserRepository.OnUserInsertListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d(TAG, "Database insertion successful!");

                                                        progressBar.setVisibility(View.GONE);
                                                        btnRegister.setEnabled(true);
                                                        btnRegister.setText(getString(R.string.registerBtnTxt));

                                                        Toast.makeText(RegisterActivity.this,
                                                                "Registration successful!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        Log.e(TAG, "Database insertion failed: " + error);

                                                        progressBar.setVisibility(View.GONE);
                                                        btnRegister.setEnabled(true);
                                                        btnRegister.setText(getString(R.string.registerBtnTxt));

                                                        Toast.makeText(RegisterActivity.this,
                                                                "Database error: " + error, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else {
                                        Log.e(TAG, "Firebase user is null!");
                                        progressBar.setVisibility(View.GONE);
                                        btnRegister.setEnabled(true);
                                        btnRegister.setText(getString(R.string.registerBtnTxt));
                                    }
                                } else {
                                    Log.e(TAG, "Firebase registration failed: " + task.getException());

                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);
                                    btnRegister.setText(getString(R.string.registerBtnTxt));

                                    Toast.makeText(RegisterActivity.this, "Authentication failed: "
                                                    + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void createUserInBackend(String firebaseUid, String email, String displayName) {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        User newUser = new User();
        newUser.setFirebaseUid(firebaseUid);
        newUser.setEmail(email);
        newUser.setFullName(displayName);

        UserRepository.insertUser(newUser, RegisterActivity.this,
                new UserRepository.OnUserInsertListener() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        Toast.makeText(RegisterActivity.this,
                                "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, GreetingActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        // Check if user already exists
                        if (error.contains("409") || error.contains("already exists")) {
                            Toast.makeText(RegisterActivity.this,
                                    "Welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, GreetingActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Database error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

