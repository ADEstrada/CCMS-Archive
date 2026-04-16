package com.estrada.ccmsarchive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<ProjectPreview> projectList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

        @Nullable
        @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            RecyclerView rvHome = view.findViewById(R.id.rvHome);
            rvHome.setLayoutManager(new LinearLayoutManager(getContext()));

            List<ProjectPreview> list = new ArrayList<>();
            adapter = new ProjectAdapter(list,R.layout.item_post);
            rvHome.setAdapter(adapter);

            db.collection("Projects")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            return;
                        }

                        if (value != null) {
                            list.clear();
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                String title = doc.getString("title");
                                String desc = doc.getString("description");
                                String uploader = doc.getString("uploader");
                                String program = doc.getString("program");
                                List<String> images = (List<String>) doc.get("imageData");

                                list.add(new ProjectPreview(title, desc, uploader, program, images));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });

            return view;
        }



        @Override
        public void onResume() {
            super.onResume();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setUIVisibility(true);
            }
        }
    }