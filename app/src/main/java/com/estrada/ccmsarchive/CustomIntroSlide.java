package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CustomIntroSlide extends Fragment {

    private String title, description;
    private int imageRes;

    public static CustomIntroSlide newInstance(String title, String description, int imageRes) {
        CustomIntroSlide fragment = new CustomIntroSlide();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putInt("image", imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_slide, container, false);

        if (getArguments() != null) {
            ((TextView) view.findViewById(R.id.intro_title)).setText(getArguments().getString("title"));
            ((TextView) view.findViewById(R.id.intro_description)).setText(getArguments().getString("description"));
            ((ImageView) view.findViewById(R.id.intro_image)).setImageResource(getArguments().getInt("image"));
        }

        return view;
    }
}