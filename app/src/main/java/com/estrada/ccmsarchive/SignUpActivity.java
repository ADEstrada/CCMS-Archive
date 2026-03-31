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

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText firstName = findViewById(R.id.first_name_field);
        EditText lastName = findViewById(R.id.last_name_field);
        EditText email = findViewById(R.id.email_field);
        EditText password = findViewById(R.id.password_field);

        Button signUpBtn = findViewById(R.id.loginBtn);
        Button hacBtn = findViewById(R.id.dha_btn);

        // Go back to Login (HAC = Have An Account)
        hacBtn.setOnClickListener(v -> finish());

        // Validate "Required" fields
        signUpBtn.setOnClickListener(v -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (validateFields(firstName, fName, lastName, lName, email, emailInput, password, passwordInput)) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("FIRST_NAME", fName);
                intent.putExtra("LAST_NAME", lName);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateFields(EditText fnView, String fn, EditText lnView, String ln, EditText emView, String em, EditText pwView, String pw) {
        boolean isValid = true;

        if (TextUtils.isEmpty(fn)) {
            fnView.setError("First name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(ln)) {
            lnView.setError("Last name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(em)) {
            emView.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            emView.setError("Invalid Email");
            isValid = false;
        }

        if (TextUtils.isEmpty(pw)) {
            pwView.setError("Password is required");
            isValid = false;
        } else if (pw.length() < 6) {
            pwView.setError("Min 6 characters");
            isValid = false;
        }

        return isValid;
    }
}