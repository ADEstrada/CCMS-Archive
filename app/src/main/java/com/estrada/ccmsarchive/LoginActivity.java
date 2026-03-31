package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText emailField = findViewById(R.id.email_field);
        EditText passwordField = findViewById(R.id.password_field);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button dhaBtn = findViewById(R.id.dha_btn);

        // Navigation to SignUpActivity
        if (dhaBtn != null) {
            dhaBtn.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }

        // Login Logic & Navigation to MainActivity (Dashboard)
        if (loginBtn != null) {
            loginBtn.setOnClickListener(v -> {
                if (validateLogin(emailField, passwordField)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private boolean validateLogin(EditText email, EditText password) {
        String emailInput = email.getText().toString().trim();
        String passInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            email.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Invalid email format");
            return false;
        }
        if (TextUtils.isEmpty(passInput)) {
            password.setError("Password is required");
            return false;
        }
        return true;
    }
}