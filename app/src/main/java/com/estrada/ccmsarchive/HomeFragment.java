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

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<ProjectPreview> projectList;

        @Nullable
        @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            RecyclerView rvHome = view.findViewById(R.id.rvHome);

            rvHome.setLayoutManager(new LinearLayoutManager(getContext()));

            // Dummy data
            List<ProjectPreview> list = new ArrayList<>();
            list.add(new ProjectPreview("CCMS Website", "The name is CCMS... a web portal for archiving school files.", "Estrada"));
            list.add(new ProjectPreview("Mobile App", "The name is Mobile... a native Android archive app.", "Admin"));

            // added R.layout.item_post
            adapter = new ProjectAdapter(list,R.layout.item_post);
            rvHome.setAdapter(adapter);

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            // Ensure profile initials are visible when on the list
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setUIVisibility(true);
            }
        }
    }