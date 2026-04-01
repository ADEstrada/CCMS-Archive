package com.estrada.ccmsarchive;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.estrada.ccmsarchive.databinding.FragmentPostBinding;

public class PostFragment extends Fragment {
    public TextView headerTitle;
    public ImageView btnBack;
    public PostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);

        headerTitle = view.findViewById(R.id.header_title);
        btnBack = view.findViewById(R.id.btn_back);

        if (headerTitle != null) {
            headerTitle.setText(R.string.title_post);
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