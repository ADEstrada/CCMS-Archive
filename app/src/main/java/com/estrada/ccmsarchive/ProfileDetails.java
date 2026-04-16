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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetails extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<PostPreview> postList;
    private TextView headerTitle;
    private ImageView btnBack;

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

        tvFullName = findViewById(R.id.studentNameText);
        tvProgramYear = findViewById(R.id.programYearText);
        tvInitials = findViewById(R.id.tvInitial);

        fetchUserData();

        if (rvPosts != null) {
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postList = new ArrayList<>();
            adapter = new PostAdapter(postList, R.layout.students_projects);
            rvPosts.setAdapter(adapter);

            fetchUserPosts();
        }

        if (headerTitle != null) {
            headerTitle.setText(R.string.profile_details);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void fetchUserPosts() {
        if (mAuth.getCurrentUser() == null) return;

        String currentUid = mAuth.getCurrentUser().getUid();

        db.collection("Pending_Projects")
                .whereEqualTo("uploaderUid", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("title");
                        String status = doc.getString("status");

                        if (title != null) {
                            postList.add(new PostPreview(title, status != null ? status : "Pending"));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load your posts", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String fName = userDoc.getString("firstName");
                            String lName = userDoc.getString("lastName");
                            String studID = userDoc.getString("studentID");

                            if (tvFullName != null) tvFullName.setText(fName + " " + lName);

                            // Safer initials logic
                            if (tvInitials != null && fName != null && lName != null) {
                                String initials = fName.substring(0, 1).toUpperCase() + lName.substring(0, 1).toUpperCase();
                                tvInitials.setText(initials);
                            }

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