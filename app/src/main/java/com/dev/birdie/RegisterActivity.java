package com.dev.birdie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    EditText editTextName, editTextEmail, editTextPwd, editTextConfirmPwd;
    MaterialButton btnRegister;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


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

                                // Hide progress when done
                                progressBar.setVisibility(View.GONE);
                                btnRegister.setEnabled(true);
                                btnRegister.setText(getString(R.string.registerBtnTxt));

                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}