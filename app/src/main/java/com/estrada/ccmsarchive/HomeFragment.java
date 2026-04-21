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
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<ProjectPreview> list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView btnYear, btnCourse, btnProgram;

    private String currentYear = "All";
    private String currentCourse = "All";
    private String currentProgram = "All";

        @Nullable
        @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            RecyclerView rvHome = view.findViewById(R.id.rvHome);
            rvHome.setLayoutManager(new LinearLayoutManager(getContext()));

            list = new ArrayList<>();
            adapter = new ProjectAdapter(list, R.layout.item_post);
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
                                String year = doc.getString("year");

                                List<String> images = (List<String>) doc.get("imageData");

                                String status = doc.getString("status");
                                String course = doc.getString("course");
                                String tech = doc.getString("technologies");
                                String contributors = doc.getString("contributors");

                                list.add(new ProjectPreview(
                                        title,
                                        desc,
                                        uploader,
                                        program,
                                        year,
                                        images,
                                        status != null ? status : "Approved",
                                        course != null ? course : "",
                                        tech != null ? tech : "",
                                        contributors != null ? contributors : ""
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });

            btnYear = view.findViewById(R.id.btnYearFilter);
            String[] yearOptions = getResources().getStringArray(R.array.year_array);

            btnYear.setOnClickListener(v -> {
                FilterBottomSheet sheet = new FilterBottomSheet("Select Year", yearOptions, selection -> {
                    btnYear.setText(selection + " ▼");
                    currentYear = selection;
                });
                sheet.show(getActivity().getSupportFragmentManager(), "yearFilter");
            });

            btnCourse = view.findViewById(R.id.btnCourseFilter);
            String[] courseOptions = getResources().getStringArray(R.array.course_array);

            btnCourse.setOnClickListener(v -> {
                FilterBottomSheet sheet = new FilterBottomSheet("Select Course", courseOptions, selection -> {
                    btnCourse.setText(selection + " ▼");
                    currentCourse = selection;
                });
                sheet.show(getActivity().getSupportFragmentManager(), "courseFilter");
            });

            btnProgram = view.findViewById(R.id.btnProgramFilter);
            String[] programOptions = getResources().getStringArray(R.array.program_array);

            btnProgram.setOnClickListener(v -> {
                FilterBottomSheet sheet = new FilterBottomSheet("Select Program", programOptions, selection -> {
                    btnProgram.setText(selection + " ▼");
                    currentProgram = selection;
                });
                sheet.show(getActivity().getSupportFragmentManager(), "programFilter");
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
