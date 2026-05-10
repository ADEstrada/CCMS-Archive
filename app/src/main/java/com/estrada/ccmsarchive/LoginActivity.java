package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailField = findViewById(R.id.email_field);
        EditText passwordField = findViewById(R.id.password_field);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button dhaBtn = findViewById(R.id.dha_btn);

        // NAV TO SIGN UP
        if (dhaBtn != null) {
            dhaBtn.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }

        if (loginBtn != null) {
            loginBtn.setOnClickListener(v -> {
                String emailInput = emailField.getText().toString().trim();
                String passInput = passwordField.getText().toString().trim();

                if (validateLogin(emailField, passwordField)) {
                    mAuth.signInWithEmailAndPassword(emailInput, passInput)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();

                                    db.collection("users").document(userId).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    String fName = documentSnapshot.getString("firstName");
                                                    String lName = documentSnapshot.getString("lastName");
                                                    String role = documentSnapshot.getString("role");

                                                    Toast.makeText(LoginActivity.this, "Welcome back, " + fName + "!", Toast.LENGTH_SHORT).show();

                                                    Intent intent;
                                                    if ("Instructor".equals(role)) {
                                                        intent = new Intent(LoginActivity.this, InstructorMainActivity.class);
                                                    } else {
                                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    }

                                                    intent.putExtra("FIRST_NAME", fName);
                                                    intent.putExtra("LAST_NAME", lName);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
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