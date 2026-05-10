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

        // Initialize Views
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

        // 2. Have an Account Button
        hacBtn.setOnClickListener(v -> finish());

        // 3. Sign Up Button Logic
        signUpBtn.setOnClickListener(v -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String studID = studentId.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            int selectedRole = roleGroup.getCheckedRadioButtonId();
            String role = (selectedRole == R.id.radioInstructor) ? "Instructor" : "Student";
            String masterlistPath = (role.equals("Instructor")) ? "instructor_masterlist" : "student_masterlist";

            if (validateFields(firstName, fName, lastName, lName, studentId, studID, email, emailInput, password, passwordInput, role)) {

                db.collection(masterlistPath).document(studID).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {

                                String officialFirstName = task.getResult().getString("firstName");
                                String officialLastName = task.getResult().getString("lastName");

                                String finalProgram;
                                String finalYear;

                                if (role.equals("Instructor")) {
                                    finalProgram = "College of Computing and Multimedia Studies";
                                    finalYear = task.getResult().getString("academicRank");
                                } else {
                                    finalProgram = task.getResult().getString("program");
                                    finalYear = task.getResult().getString("year");
                                }

                                if (fName.equalsIgnoreCase(officialFirstName) && lName.equalsIgnoreCase(officialLastName)) {
                                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                            .addOnCompleteListener(authTask -> {
                                                if (authTask.isSuccessful()) {
                                                    String userId = mAuth.getCurrentUser().getUid();

                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("firstName", officialFirstName);
                                                    user.put("lastName", officialLastName);
                                                    user.put("idNumber", studID);
                                                    user.put("email", emailInput);
                                                    user.put("program", finalProgram);
                                                    user.put("year", finalYear);
                                                    user.put("role", role);

                                                    db.collection("users").document(userId).set(user)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Intent intent;
                                                                if (role.equals("Instructor")) {
                                                                    intent = new Intent(SignUpActivity.this, InstructorMainActivity.class);
                                                                } else {
                                                                    intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                }
                                                                intent.putExtra("FIRST_NAME", officialFirstName);
                                                                intent.putExtra("ROLE", role);
                                                                startActivity(intent);
                                                                finish();
                                                            });
                                                } else {
                                                    Toast.makeText(this, "Auth Error: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, "Name mismatch for " + role, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                studentId.setError(role + " ID not found in masterlist");
                            }
                        });
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