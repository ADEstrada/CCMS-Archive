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
    private List<ProjectPreview> filteredList;
    private List<ProjectPreview> fullList;
    private List<String> masterYearList = new ArrayList<>();
    private List<String> masterCourseList = new ArrayList<>();
    private List<String> masterProgramList = new ArrayList<>();

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

        //fullList and filtered list
        fullList = new ArrayList<>();
        filteredList = new ArrayList<>();

        masterYearList.add("All");
        masterCourseList.add("All");
        masterProgramList.add("All");
        fetchFilterOptions();

        list = new ArrayList<>();
        adapter = new ProjectAdapter(list, R.layout.item_post);
        rvHome.setAdapter(adapter);

        db.collection("Projects")
                .whereEqualTo("status", "Approved")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        fullList.clear();
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

                            fullList.add(new ProjectPreview(
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
                        applyFilters();


                        adapter.notifyDataSetChanged();
                    }
                });

        btnYear = view.findViewById(R.id.btnYearFilter);
        btnYear.setOnClickListener(v -> {
            String[] yearOptions = masterYearList.toArray(new String[0]);
            FilterBottomSheet sheet = new FilterBottomSheet("Select Year", yearOptions, selection -> {
                btnYear.setText(selection + " ▼");
                currentYear = selection;
                applyFilters();
            });
            sheet.show(getActivity().getSupportFragmentManager(), "yearFilter");
        });

        btnCourse = view.findViewById(R.id.btnCourseFilter);
        btnCourse.setOnClickListener(v -> {
            String[] courseOptions = masterCourseList.toArray(new String[0]);
            FilterBottomSheet sheet = new FilterBottomSheet("Select Course", courseOptions, selection -> {
                btnCourse.setText(selection + " ▼");
                currentCourse = selection;
                applyFilters();
            });
            sheet.show(getActivity().getSupportFragmentManager(), "courseFilter");
        });

        btnProgram = view.findViewById(R.id.btnProgramFilter);

        btnProgram.setOnClickListener(v -> {
            String[] programOptions = masterProgramList.toArray(new String[0]);
            FilterBottomSheet sheet = new FilterBottomSheet("Select Program", programOptions, selection -> {
                btnProgram.setText(selection + " ▼");
                currentProgram = selection;
                applyFilters();
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

    private void applyFilters() {
        filteredList.clear();

        for (ProjectPreview project : fullList) {
            boolean matchesYear = currentYear.equals("All") || project.getYear().equals(currentYear);

            String courseValue = project.getCourse().trim();
            String courseId = courseValue.contains("-")
                    ? courseValue.split("-")[0].trim()
                    : courseValue;
            boolean matchesCourse = currentCourse.equals("All") || courseId.equals(currentCourse);
            boolean matchesProgram = currentProgram.equals("All") || project.getProgram().equals(currentProgram);

            if (matchesYear && matchesCourse && matchesProgram) {
                filteredList.add(project);
            }
        }
        adapter.updateList(filteredList);
    }

    //method for filter ng year, course, and program
    private void fetchFilterOptions() {
        db.collection("Year").get().addOnSuccessListener(queryDocumentSnapshots -> {
            masterYearList.clear();
            masterYearList.add("All");
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                masterYearList.add(doc.getId());
            }
        });

        db.collection("Programs").get().addOnSuccessListener(queryDocumentSnapshots -> {
            masterProgramList.clear();
            masterProgramList.add("All");
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                masterProgramList.add(doc.getId());
            }
        });

        db.collection("Courses").get().addOnSuccessListener(queryDocumentSnapshots -> {
            masterCourseList.clear();
            masterCourseList.add("All");
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                masterCourseList.add(doc.getId());
            }
        });
    }

    //search
    private String currentSearchQuery = "";

    private void applySearch() {
        filteredList.clear();
        for (ProjectPreview project : fullList) {
            boolean matchesYear = currentYear.equals("All") || project.getYear().equals(currentYear);

            String courseValue = project.getCourse().trim();
            String courseId = courseValue.contains("-")
                    ? courseValue.split("-")[0].trim()
                    : courseValue;
            boolean matchesCourse = currentCourse.equals("All") || courseId.equals(currentCourse);
            boolean matchesProgram = currentProgram.equals("All") || project.getProgram().equals(currentProgram);

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    project.getProjectName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                    project.getDescription().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchesYear && matchesCourse && matchesProgram && matchesSearch) {
                filteredList.add(project);
            }
        }
        adapter.updateList(filteredList);
    }

    public void performSearch(String searchQuery) {
        this.currentSearchQuery = searchQuery;
        applySearch();
    }
}
