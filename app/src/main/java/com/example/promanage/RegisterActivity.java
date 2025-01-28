package com.example.promanage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    // Regex patterns for validation
    private Pattern usernamePattern = Pattern.compile("[a-zA-Z0-9._]{3,20}");
    private Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    private Pattern phonePattern = Pattern.compile("^[0-9]{11}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        EditText etUsername = findViewById(R.id.et_register_username);
        EditText etEmail = findViewById(R.id.et_register_email);
        EditText etPassword = findViewById(R.id.et_register_password);
        EditText etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        EditText etMobile = findViewById(R.id.et_register_mobile);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnLogin = findViewById(R.id.btn_login);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();

            // Validation checks
            if (username.isEmpty()) {
                etUsername.setError("Username cannot be empty");
                etUsername.requestFocus();
                return;
            }
            if (!usernamePattern.matcher(username).matches()) {
                etUsername.setError("Username must be 3-20 characters long and can only contain letters, numbers, dots, and underscores");
                etUsername.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Email cannot be empty");
                etEmail.requestFocus();
                return;
            }
            if (!emailPattern.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password cannot be empty");
                etPassword.requestFocus();
                return;
            }
            if (!passwordPattern.matcher(password).matches()) {
                etPassword.setError("Password must contain at least 8 characters, including uppercase, lowercase, number and special character");
                etPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match!");
                etConfirmPassword.requestFocus();
                return;
            }

            if (mobile.isEmpty()) {
                etMobile.setError("Phone number cannot be empty");
                etMobile.requestFocus();
                return;
            }
            if (!phonePattern.matcher(mobile).matches()) {
                etMobile.setError("Please enter a valid 11-digit phone number");
                etMobile.requestFocus();
                return;
            }

            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, send email verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Registration successful! Please check your email for verification.",
                                                        Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        } else {
                            // If sign up fails, give a message to User
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}