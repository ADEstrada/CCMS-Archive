package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText firstName = findViewById(R.id.first_name_field);
        EditText lastName = findViewById(R.id.last_name_field);
        EditText studentId = findViewById(R.id.studentID_field);
        EditText email = findViewById(R.id.email_field);
        EditText password = findViewById(R.id.password_field);

        Button signUpBtn = findViewById(R.id.loginBtn);
        Button hacBtn = findViewById(R.id.dha_btn);

        hacBtn.setOnClickListener(v -> finish());

        signUpBtn.setOnClickListener(v -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String studID = studentId.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (validateFields(firstName, fName, lastName, lName, studentId, studID, email, emailInput, password, passwordInput)) {

                // --- STEP 1: VERIFY STUDENT ID IN MASTER LIST ---
                db.collection("student_masterlist").document(studID).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {

                                String officialFirstName = task.getResult().getString("firstName");
                                String officialLastName = task.getResult().getString("lastName");
                                String program = task.getResult().getString("program");
                                String year = task.getResult().getString("year");

                                // --- STEP 2: COMPARE INPUT NAME WITH MASTER LIST ---
                                if (fName.equalsIgnoreCase(officialFirstName) && lName.equalsIgnoreCase(officialLastName)) {

                                    // NAMES MATCH! Proceed to create Authentication account
                                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                            .addOnCompleteListener(authTask -> {
                                                if (authTask.isSuccessful()) {
                                                    String userId = mAuth.getCurrentUser().getUid();

                                                    java.util.Map<String, Object> user = new java.util.HashMap<>();
                                                    user.put("firstName", officialFirstName); // Save the official name
                                                    user.put("lastName", officialLastName);
                                                    user.put("studentID", studID);
                                                    user.put("email", emailInput);
                                                    user.put("program", program);
                                                    user.put("year", year);

                                                    // --- STEP 3: SAVE TO APP USERS COLLECTION ---
                                                    db.collection("users").document(userId).set(user)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                intent.putExtra("FIRST_NAME", officialFirstName);
                                                                intent.putExtra("LAST_NAME", officialLastName);
                                                                intent.putExtra("PROGRAM", program);
                                                                intent.putExtra("YEAR", year);
                                                                startActivity(intent);
                                                                finish();
                                                            });
                                                } else {
                                                    Toast.makeText(this, "Auth Error: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // NAMES DO NOT MATCH MASTER LIST
                                    firstName.setError("Name does not match CCMS records");
                                    lastName.setError("Name does not match CCMS records");
                                    Toast.makeText(this, "Name mismatch for this ID", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // ID NOT FOUND IN MASTER LIST
                                studentId.setError("Student ID not found in CCMS records");
                                Toast.makeText(this, "ID Verification failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private boolean validateFields(EditText fnView, String fn, EditText lnView, String ln, EditText stIdView, String stID, EditText emView, String em, EditText pwView, String pw) {
        boolean isValid = true;
        if (TextUtils.isEmpty(fn)) { fnView.setError("First name is required"); isValid = false; }
        if (TextUtils.isEmpty(ln)) { lnView.setError("Last name is required"); isValid = false; }

        String idPattern = "^\\d{2}-\\d{4}$";
        if (TextUtils.isEmpty(stID)) {
            stIdView.setError("Student ID is required");
            isValid = false;
        } else if (!stID.matches(idPattern)) {
            stIdView.setError("Use format: 00-0000");
            isValid = false;
        }

        if (TextUtils.isEmpty(em) || !Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            emView.setError("Invalid Email");
            isValid = false;
        }
        if (pw.length() < 6) {
            pwView.setError("Min 6 characters");
            isValid = false;
        }
        return isValid;
    }
}