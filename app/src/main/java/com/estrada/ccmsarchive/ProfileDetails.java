package com.estrada.ccmsarchive;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetails extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<PostPreview> postList;
    private TextView headerTitle;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_details);

        headerTitle = findViewById(R.id.header_title);
        btnBack = findViewById(R.id.btn_back);
        rvPosts = findViewById(R.id.rvPosts);
        if (rvPosts != null) {

            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postList = new ArrayList<>();
            postList.add(new PostPreview("CCMS Website", "Completed"));
            postList.add(new PostPreview("CCMS Website", "Completed"));
            postList.add(new PostPreview("CCMS Website", "Pending"));
            postList.add(new PostPreview("CCMS Website", "Completed"));
            postList.add(new PostPreview("CCMS Website", "Pending"));
            postList.add(new PostPreview("CCMS Website", "Completed"));

            adapter = new PostAdapter(postList, R.layout.students_projects);
            rvPosts.setAdapter(adapter);
        } else {
            android.util.Log.e("ERROR", "RecyclerView not found! Check your XML ID.");
        }

        if (headerTitle != null) {
            headerTitle.setText(R.string.profile_details);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }
    }
}