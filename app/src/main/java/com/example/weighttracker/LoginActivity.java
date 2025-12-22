package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for user login and registration. Handles user authentication
 * against a local SQLite database.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set OnClickListener for the Login button
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            } else {
                // Check user credentials against the database
                int userId = databaseHelper.checkUser(user, pass);
                if (userId != -1) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to GridActivity on successful login, passing the user ID
                    Intent intent = new Intent(LoginActivity.this, GridActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish(); // Close LoginActivity to prevent returning with back button
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener for the Register button
        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            } else {
                // Add new user to the database
                if (databaseHelper.addUser(user, pass)) {
                    Toast.makeText(LoginActivity.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                    // Optionally, auto-login and navigate after successful registration
                    int userId = databaseHelper.checkUser(user, pass);
                    if (userId != -1) {
                        Intent intent = new Intent(LoginActivity.this, GridActivity.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}