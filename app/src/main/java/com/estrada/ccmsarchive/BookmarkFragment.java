package com.estrada.ccmsarchive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {

    public BookmarkFragment() {}

    private RecyclerView rvBookmarks;
    private ProjectAdapter adapter;
    private ImageView btnBack;
    private TextView headerTitle;
    private List<ProjectPreview> projectList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        headerTitle = view.findViewById(R.id.header_title);
        btnBack = view.findViewById(R.id.btn_back);
        rvBookmarks = view.findViewById(R.id.rvBookmarks);

        rvBookmarks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProjectAdapter(projectList, R.layout.saved_project);
        rvBookmarks.setAdapter(adapter);

        if (headerTitle != null) headerTitle.setText(R.string.title_saved_projects);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    MainActivity main = (MainActivity) getActivity();
                    main.findViewById(R.id.nav_home).performClick();
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarkedProjects();
    }

    private void loadBookmarkedProjects() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        projectList.clear();

        db.collection("Users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> bookmarkedIDs = (List<String>) documentSnapshot.get("bookmarks");

                        if (bookmarkedIDs != null && !bookmarkedIDs.isEmpty()) {
                            db.collection("Projects")
                                    .whereIn(FieldPath.documentId(), bookmarkedIDs)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        projectList.clear();
                                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                            String title = doc.getString("title");
                                            String desc = doc.getString("description");
                                            String uploader = doc.getString("uploader");
                                            String program = doc.getString("program");
                                            String year = doc.getString("year");
                                            List<String> images = (List<String>) doc.get("imageData");
                                            String status = doc.getString("status");
                                            String course = doc.getString("course");
                                            String tech = doc.getString("technologies");
                                            String contributors = doc.getString("contributors");

                                            ProjectPreview project = new ProjectPreview(
                                                    title, desc, uploader, program, year, images,
                                                    status, course, tech, contributors
                                            );
                                            projectList.add(project);
                                        }
                                        adapter.updateList(projectList);
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching projects", e));
                        } else {
                            projectList.clear();
                            adapter.updateList(projectList);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading bookmarks", e));
    }
}