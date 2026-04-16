package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetails extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<PostPreview> postList;
    private TextView headerTitle;
    private ImageView btnBack;

    // UI ELEMENTS
    private TextView tvFullName, tvProgramYear, tvInitials;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        headerTitle = findViewById(R.id.header_title);
        btnBack = findViewById(R.id.btn_back);
        rvPosts = findViewById(R.id.rvPosts);

        // --- MAPPING THE VIEWS ---
        tvFullName = findViewById(R.id.studentNameText);
        tvProgramYear = findViewById(R.id.programYearText);
        tvInitials = findViewById(R.id.tvInitial);

        // Load the data
        fetchUserData();

        if (rvPosts != null) {
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postList = new ArrayList<>();
            postList.add(new PostPreview("CCMS Website", "Completed"));
            postList.add(new PostPreview("CCMS Website", "Pending"));
            adapter = new PostAdapter(postList, R.layout.students_projects);
            rvPosts.setAdapter(adapter);
        }

        if (headerTitle != null) {
            headerTitle.setText(R.string.profile_details);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // STEP 1: Kunin muna ang Student ID mula sa "users" collection
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String fName = userDoc.getString("firstName");
                            String lName = userDoc.getString("lastName");
                            String studID = userDoc.getString("studentID");

                            // I-set ang Name at Initials
                            if (tvFullName != null) tvFullName.setText(fName + " " + lName);
                            if (tvInitials != null && fName != null) {
                                tvInitials.setText(fName.substring(0, 1).toUpperCase() + lName.substring(0, 1).toUpperCase());
                            }

                            // STEP 2: Gamitin ang studID para kunin ang Program/Year sa "student_master_list"
                            if (studID != null) {
                                db.collection("student_masterlist").document(studID).get()
                                        .addOnSuccessListener(masterDoc -> {
                                            if (masterDoc.exists()) {
                                                String prog = masterDoc.getString("program");
                                                String yr = masterDoc.getString("year");

                                                if (tvProgramYear != null) {
                                                    tvProgramYear.setText(prog + " | " + yr);
                                                }
                                            } else {
                                                if (tvProgramYear != null) tvProgramYear.setText("ID not in Master List");
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}