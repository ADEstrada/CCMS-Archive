package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText currentPassField, newPassField, confirmField;
    private TextView confirmLabel;
    private Button updateBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        TextView headerTitle = findViewById(R.id.header_title);
        ImageView btnBack = findViewById(R.id.btn_back);

        currentPassField = findViewById(R.id.currentPassField);
        newPassField = findViewById(R.id.newPassField);
        confirmLabel = findViewById(R.id.confirmPassLabel);
        confirmField = findViewById(R.id.confirmPassField);
        updateBtn = findViewById(R.id.loginBtn2);

        if (headerTitle != null) {
            headerTitle.setText(R.string.menu_change_password);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // SHOW/HIDE CONFIRM FIELDS
        newPassField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    confirmLabel.setVisibility(View.VISIBLE);
                    confirmField.setVisibility(View.VISIBLE);
                } else {
                    confirmLabel.setVisibility(View.GONE);
                    confirmField.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // UPDATE PASSWORD LOGIC
        updateBtn.setOnClickListener(v -> {
            validateAndChangePassword();
        });
    }

    private void validateAndChangePassword() {
        String currentPass = currentPassField.getText().toString().trim();
        String newPass = newPassField.getText().toString().trim();
        String confirmPass = confirmField.getText().toString().trim();

        // BASIC VALIDATIONS
        if (TextUtils.isEmpty(currentPass)) {
            currentPassField.setError("Required");
            return;
        }
        if (newPass.length() < 6) {
            newPassField.setError("Password must be at least 6 characters");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            confirmField.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // RE-AUTHENTICATION
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // UPDATE PASSWORD
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ChangePassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePassword.this, "Update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    currentPassField.setError("Incorrect current password");
                    Toast.makeText(ChangePassword.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}