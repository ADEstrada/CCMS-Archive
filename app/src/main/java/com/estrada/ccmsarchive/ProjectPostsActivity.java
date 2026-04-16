package com.estrada.ccmsarchive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProjectPostsActivity extends AppCompatActivity {

    public TextView headerTitle;

    private TextView tvProjectName, tvDescription, tvStudentName, tvProgram, tvInitial;
    private ImageView btnBack;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_posts);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvProjectName = findViewById(R.id.projectName);
        tvDescription = findViewById(R.id.description);
        tvStudentName = findViewById(R.id.studentNameTv);
        tvProgram = findViewById(R.id.programTv);
        tvInitial = findViewById(R.id.tvInitial);
        viewPager2 = findViewById(R.id.viewPagerImages);
        tabLayout = findViewById(R.id.tabLayoutIndicator);

        View backHeader = findViewById(R.id.back_header);
        headerTitle = backHeader.findViewById(R.id.header_title);
        btnBack = backHeader.findViewById(R.id.btn_back);

        String name = getIntent().getStringExtra("PROJECT_NAME");
        String desc = getIntent().getStringExtra("DESCRIPTION");
        String uploader = getIntent().getStringExtra("UPLOADER");
        String program = getIntent().getStringExtra("PROGRAM");

        tvProjectName.setText(name);
        tvDescription.setText(desc);
        tvStudentName.setText(uploader);
        tvProgram.setText(program);

        if (name != null) {
            FirebaseFirestore.getInstance().collection("Projects")
                    .whereEqualTo("title", name)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            List<String> images = (List<String>) doc.get("imageData");

                            if (images != null && !images.isEmpty()) {
                                ImageSliderAdapter adapter = new ImageSliderAdapter(images);
                                viewPager2.setAdapter(adapter);
                                new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                                }).attach();
                            }
                        }
                    });
        }

        if (headerTitle != null) {
            headerTitle.setText(R.string.title_post);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }
    }

    class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {
        private List<String> images;

        public ImageSliderAdapter(List<String> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                String base64String = images.get(position);
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ((ImageView) holder.itemView).setImageBitmap(bitmap);
            } catch (Exception e) {
                ((ImageView) holder.itemView).setImageResource(R.drawable.gallery);
            }
        }

        @Override
        public int getItemCount() {
            return images != null ? images.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}