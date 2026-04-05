package com.estrada.ccmsarchive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private List<ProjectPreview> projectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        headerTitle = view.findViewById(R.id.header_title);
        btnBack = view.findViewById(R.id.btn_back);
        rvBookmarks = view.findViewById(R.id.rvBookmarks);

        rvBookmarks.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ProjectPreview> list = new ArrayList<>();
        list.add(new ProjectPreview("CCMS Website", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "Application Development","Ann Dominique C. Estrada"));
        list.add(new ProjectPreview("CCMS Archive", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "Mobile Applications","Jasmine C. Raviz"));
        list.add(new ProjectPreview("PiñaQualify", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "Computer Architecture and Robotics","six7"));

        adapter = new ProjectAdapter(list, R.layout.saved_project);
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

}