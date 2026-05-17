package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText nameField, studentIdField, emailAddField;
    private TextView studentIdLabel; // Added reference for the label
    private Button saveBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView headerTitle = findViewById(R.id.header_title);
        ImageView btnBack = findViewById(R.id.btn_back);

        nameField = findViewById(R.id.nameField);
        nameField.setEnabled(false);
        studentIdField = findViewById(R.id.studentIdField);
        studentIdField.setEnabled(false);
        studentIdLabel = findViewById(R.id.studentIdLabel); // Initialize label
        emailAddField = findViewById(R.id.emailAddField);
        saveBtn = findViewById(R.id.saveBtn);

        if (headerTitle != null) {
            headerTitle.setText(R.string.menu_edit_profile);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadUserData();

        saveBtn.setOnClickListener(v -> {
            updateUserProfile();
        });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fName = documentSnapshot.getString("firstName");
                            String lName = documentSnapshot.getString("lastName");
                            String email = documentSnapshot.getString("email");
                            String role = documentSnapshot.getString("role");

                            String idValue = "";
                            // Condition to check role and set labels/IDs
                            if ("Instructor".equalsIgnoreCase(role)) {
                                if (studentIdLabel != null) studentIdLabel.setText("Instructor/Professor ID");
                                idValue = documentSnapshot.getString("idNumber");
                            } else {
                                if (studentIdLabel != null) studentIdLabel.setText("Student ID");
                                idValue = documentSnapshot.getString("studentID");
                            }

                            // Fill the fields
                            if (nameField != null) nameField.setText(fName + " " + lName);
                            if (studentIdField != null) studentIdField.setText(idValue != null ? idValue : "");
                            if (emailAddField != null) emailAddField.setText(email != null ? email : "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserProfile() {
        String updatedEmail = emailAddField.getText().toString().trim();

        if (updatedEmail.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            Map<String, Object> updates = new HashMap<>();
            updates.put("email", updatedEmail);

            db.collection("users").document(userId).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}