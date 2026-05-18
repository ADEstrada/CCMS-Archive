package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView idLabel;

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
        idLabel = findViewById(R.id.sIDLabel);
        RadioGroup roleGroup = findViewById(R.id.roleGroup);

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioInstructor) {
                idLabel.setText("Instructor ID:");
            } else {
                idLabel.setText("Student ID:");
            }
        });


        hacBtn.setOnClickListener(v -> finish());

        signUpBtn.setOnClickListener(v -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String studID = studentId.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            int selectedRole = roleGroup.getCheckedRadioButtonId();
            String role = (selectedRole == R.id.radioInstructor) ? "Instructor" : "Student";

            if (validateFields(firstName, fName, lastName, lName, studentId, studID, email, emailInput, password, passwordInput, role)) {

                if (role.equals("Instructor")) {
                    // VALIDATION PARA SA INSTRUCTOR: Gamitin ang Full Name bilang Document ID
                    String fullName = fName + " " + lName;

                    db.collection("Instructors").document(fullName).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    String officialEmail = task.getResult().getString("email");

                                    // I-check kung ang email ay tugma sa instructors_data.json
                                    if (emailInput.equalsIgnoreCase(officialEmail)) {
                                        // Proceed sa Auth at Pag-save
                                        performRegistration(emailInput, passwordInput, fName, lName, studID, role, "CCMS", "Instructor");
                                    } else {
                                        email.setError("Email does not match our instructor records.");
                                    }
                                } else {
                                    firstName.setError("Instructor name not authorized.");
                                }
                            });
                } else {
                    // VALIDATION PARA SA STUDENT: Nanatili sa student_masterlist gamit ang ID
                    db.collection("student_masterlist").document(studID).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    String offFName = task.getResult().getString("firstName");
                                    String offLName = task.getResult().getString("lastName");
                                    String prog = task.getResult().getString("program");
                                    String yr = task.getResult().getString("yearLevel"); // Match sa database

                                    if (fName.equalsIgnoreCase(offFName) && lName.equalsIgnoreCase(offLName)) {
                                        performRegistration(emailInput, passwordInput, offFName, offLName, studID, role, prog, yr);
                                    } else {
                                        Toast.makeText(this, "Name mismatch for Student", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    studentId.setError("Student ID not found in masterlist");
                                }
                            });
                }
            }
        });
    }

    private void performRegistration(String email, String password, String fName, String lName, String id, String role, String prog, String yr) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("firstName", fName);
                        user.put("lastName", lName);
                        user.put("idNumber", id); // In-input na ID ni Instructor
                        user.put("email", email);
                        user.put("program", prog);
                        user.put("year", yr);
                        user.put("role", role);

                        // 1. I-save sa 'users' collection para sa login
                        db.collection("users").document(userId).set(user)
                                .addOnSuccessListener(aVoid -> {

                                    if (role.equals("Instructor")) {
                                        db.collection("instructor_masterlist").document(id).set(user);
                                    }

                                    Intent intent = new Intent(SignUpActivity.this,
                                            role.equals("Instructor") ? InstructorMainActivity.class : MainActivity.class);

                                    intent.putExtra("FIRST_NAME", fName);
                                    intent.putExtra("LAST_NAME", lName);
                                    intent.putExtra("ROLE", role);

                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Auth Error: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validateFields(EditText fnView, String fn, EditText lnView, String ln, EditText stIdView, String stID, EditText emView, String em, EditText pwView, String pw, String role) {
        boolean isValid = true;
        if (TextUtils.isEmpty(fn)) { fnView.setError("First name is required"); isValid = false; }
        if (TextUtils.isEmpty(ln)) { lnView.setError("Last name is required"); isValid = false; }

        String idPattern = "^\\d{2}-\\d{4}$";
        if (TextUtils.isEmpty(stID)) {
            stIdView.setError(role + " ID is required");
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