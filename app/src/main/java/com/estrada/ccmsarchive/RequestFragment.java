package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class RequestFragment extends Fragment {

    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private List<ProjectPreview> projectList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        rvRequests = view.findViewById(R.id.rvRequest);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        projectList = new ArrayList<>();
        adapter = new RequestAdapter(projectList);
        rvRequests.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchPendingRequests();

        return view;
    }

    private void fetchPendingRequests() {
        String currentInstructorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Pending_Projects")
                .whereEqualTo("instructorUid", currentInstructorId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    projectList.clear();
                    for (QueryDocumentSnapshot doc : value) {

                        String docId = doc.getId();
                        String title = doc.getString("title");
                        String desc = doc.getString("description");
                        String uploader = doc.getString("uploader");
                        String prog = doc.getString("program");
                        String yr = doc.getString("year");
                        String course = doc.getString("course");
                        String tech = doc.getString("technologies");
                        String contrib = doc.getString("contributors");
                        List<String> images = (List<String>) doc.get("imageData");

                        ProjectPreview project = new ProjectPreview(
                                title, desc, uploader, prog, yr, images,
                                "Pending", course, tech, contrib
                        );

                        project.setDocumentId(docId);

                        projectList.add(project);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}