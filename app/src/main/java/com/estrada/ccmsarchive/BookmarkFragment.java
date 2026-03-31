package com.estrada.ccmsarchive;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BookmarkFragment extends Fragment {

    public BookmarkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        TextView headerTitle = view.findViewById(R.id.header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (headerTitle != null) {
            headerTitle.setText(R.string.title_saved_projects);
        }

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