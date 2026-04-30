package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
    private CardView btnApprovedTab, btnPendingTab;
    private TextView tvApprovedLabel, tvPendingLabel;

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

        btnApprovedTab = findViewById(R.id.cardView);
        btnPendingTab = findViewById(R.id.cardViewPending);
        tvApprovedLabel = findViewById(R.id.myProjectsLabel);
        tvPendingLabel = findViewById(R.id.myProjectsLabelPending);

        tvApprovedLabel.setText("My Projects (0)");
        tvPendingLabel.setText("Pending (0)");

        fetchUserData();

        if (rvPosts != null) {
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postList = new ArrayList<>();
            adapter = new PostAdapter(postList, R.layout.students_projects);
            rvPosts.setAdapter(adapter);

            fetchUserPosts("Approved");
            updateTabUI("Approved");
        }

        if (headerTitle != null) headerTitle.setText(R.string.profile_details);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnApprovedTab.setOnClickListener(v -> {
            fetchUserPosts("Approved");
            updateTabUI("Approved");
        });

        btnPendingTab.setOnClickListener(v -> {
            fetchUserPosts("Pending");
            updateTabUI("Pending");
        });
    }

    private void fetchUserPosts(String status) {
        if (mAuth.getCurrentUser() == null) return;
        String currentUid = mAuth.getCurrentUser().getUid();

        String collectionName;
        if (status.equals("Pending")) {
            collectionName = "Pending_Projects";
        } else {
            collectionName = "Projects";
        }

        com.google.firebase.firestore.Query query = db.collection(collectionName)
                .whereEqualTo("uploaderUid", currentUid);

        if (status.equals("Approved")) {
            query = query.whereEqualTo("status", "Approved");
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            postList.clear();
            int count = queryDocumentSnapshots.size();

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String title = doc.getString("title");
                String actualStatus = doc.getString("status");
                if (title != null) {
                    postList.add(new PostPreview(title, actualStatus != null ? actualStatus : status));
                }
            }
            adapter.notifyDataSetChanged();
            updateProjectCountLabel(status, count);
        });
    }

    private void updateProjectCountLabel(String status, int count) {
        if (status.equals("Approved")) {
            String text = getString(R.string.my_posts, count);
            tvApprovedLabel.setText(text);
        } else {
            String text = getString(R.string.pending_posts, count);
            tvPendingLabel.setText(text);
        }
    }
    private void updateTabUI(String status) {
        int activeColor = android.graphics.Color.parseColor("#87231f");
        int inactiveColor = android.graphics.Color.parseColor("#B5AEAE");

        if (status.equals("Approved")) {
            btnApprovedTab.setCardBackgroundColor(activeColor);
            tvApprovedLabel.setTextColor(android.graphics.Color.WHITE);

            btnPendingTab.setCardBackgroundColor(inactiveColor);
            tvPendingLabel.setTextColor(android.graphics.Color.BLACK);
        } else {
            btnPendingTab.setCardBackgroundColor(activeColor);
            tvPendingLabel.setTextColor(android.graphics.Color.WHITE);

            btnApprovedTab.setCardBackgroundColor(inactiveColor);
            tvApprovedLabel.setTextColor(android.graphics.Color.BLACK);
        }
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