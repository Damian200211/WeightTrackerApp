package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate input presence and move to Grid
                String user = etUsername.getText().toString();
                String pass = etPassword.getText().toString();

                if(user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, GridActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Register functionality coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
}