package com.estrada.ccmsarchive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashSet;
import java.util.Set;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {

    public BookmarkFragment() {
    }

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
        SharedPreferences preferences = requireContext().getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
        Set<String> bookmarkedIds = preferences.getStringSet("bookmarked_ids", new HashSet<>());

        projectList.clear();

        if (bookmarkedIds.isEmpty()) {
            adapter.updateList(projectList);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String id : bookmarkedIds) {
            db.collection("Projects").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String title = documentSnapshot.getString("title");
                            String description = documentSnapshot.getString("description");
                            String uploader = documentSnapshot.getString("uploader");
                            String program = documentSnapshot.getString("program");
                            String year = documentSnapshot.getString("year");
                            List<String> imageData = (List<String>) documentSnapshot.get("imageData");
                            String course = documentSnapshot.getString("course");
                            String status = documentSnapshot.getString("status");
                            String contributors = documentSnapshot.getString("contributors");
                            String techUsed = documentSnapshot.getString("technologies");

                            ProjectPreview project = new ProjectPreview(title, description, uploader, program, year, imageData, status, course, techUsed, contributors);


                            projectList.add(project);
                            adapter.updateList(projectList);
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }
}