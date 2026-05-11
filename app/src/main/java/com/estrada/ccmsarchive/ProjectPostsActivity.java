package com.estrada.ccmsarchive;
import android.content.Intent;
import com.bumptech.glide.Glide;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;

public class ProjectPostsActivity extends AppCompatActivity {

    public TextView headerTitle;
    private TextView tvProjectName, tvDescription, tvStudentName, tvProgram, tvYear ,tvInitial, tvCourse, tvContributors;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private CardView contributorsCard;
    private ChipGroup techChipGroup;

    private Button btnContact;
    private String realUploaderUid;
    private String currentProjectId;
    private ImageView btnBack, ivArrowContributors, btnMoreOptions; // Added btnMoreOptions

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

        // Initialize Views
        techChipGroup = findViewById(R.id.techChipGroup);
        tvProjectName = findViewById(R.id.projectName);
        tvDescription = findViewById(R.id.description);
        tvStudentName = findViewById(R.id.studentNameTv);
        tvProgram = findViewById(R.id.programTv);
        tvYear = findViewById(R.id.yearTv);
        tvInitial = findViewById(R.id.tvInitial);
        tvCourse = findViewById(R.id.courseTV);
        viewPager2 = findViewById(R.id.viewPagerImages);
        tabLayout = findViewById(R.id.tabLayoutIndicator);
        tvContributors = findViewById(R.id.tvContributors);
        contributorsCard = findViewById(R.id.contributorsCard);
        ivArrowContributors = findViewById(R.id.ivArrowContributors);

        View backHeader = findViewById(R.id.back_header);
        headerTitle = backHeader.findViewById(R.id.header_title);
        btnBack = backHeader.findViewById(R.id.btn_back);
        btnMoreOptions = backHeader.findViewById(R.id.btnMoreOptions);

        btnContact = findViewById(R.id.btn_contact);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        //more options for bookmark
        if (btnMoreOptions != null) {
            btnMoreOptions.setOnClickListener(v -> showBottomSheet());
        }

        String name = getIntent().getStringExtra("PROJECT_NAME");
        String desc = getIntent().getStringExtra("DESCRIPTION");
        String uploader = getIntent().getStringExtra("UPLOADER");
        String program = getIntent().getStringExtra("PROGRAM");
        String year = getIntent().getStringExtra("YEAR");
        String course = getIntent().getStringExtra("COURSE");
        String contributors = getIntent().getStringExtra("CONTRIBUTORS");
        String technologies = getIntent().getStringExtra("TECHNOLOGIES");

        if (name != null) {
            FirebaseFirestore.getInstance().collection("Projects")
                    .whereEqualTo("title", name)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                            currentProjectId = doc.getId();
                            realUploaderUid = doc.getString("uploaderUid");

                            String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // ADDED - TO MAKE SURE SELF CANT CONTACT SELF
                            if (realUploaderUid != null && realUploaderUid.equals(currentUserId)) {
                                btnContact.setVisibility(View.GONE);
                            } else {
                                btnContact.setVisibility(View.VISIBLE);
                            }

                            List<String> images = (List<String>) doc.get("imageData");
                            if (images != null && !images.isEmpty()) {
                                ImageSliderAdapter adapter = new ImageSliderAdapter(images);
                                viewPager2.setAdapter(adapter);
                                new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {}).attach();
                            }
                        }
                    });
        }

        btnContact.setOnClickListener(v -> {
            if (realUploaderUid != null) {
                Intent intent = new Intent(ProjectPostsActivity.this, MessageActivity.class);
                intent.putExtra("USER_NAME", uploader);
                intent.putExtra("USER_INITIALS", tvInitial.getText().toString());
                intent.putExtra("RECEIVER_ID", realUploaderUid);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Fetching uploader info... please wait.", Toast.LENGTH_SHORT).show();
            }
        });

        if (course != null && !course.isEmpty()) {
            tvCourse.setText(course);
        } else {
            tvCourse.setText("No Course Data");
        }

        tvProjectName.setText(name != null ? name : "No Title");
        tvDescription.setText(desc != null ? desc : "No Description");
        tvStudentName.setText(uploader != null ? uploader : "Unknown Student");
        tvProgram.setText(program != null ? program : "N/A");
        tvYear.setText(year != null ? year : "N/A");
        tvCourse.setText(course != null ? course : "No Course Assigned");

        if (contributors != null && !contributors.isEmpty()) {
            String cleanContributors = contributors.trim().replaceAll(",\\s*$", "");
            String[] parts = cleanContributors.split(",");
            StringBuilder formattedList = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                String contributorName = parts[i].trim();
                if (!contributorName.isEmpty()) {
                    formattedList.append(contributorName);
                    if (i < parts.length - 1) {
                        formattedList.append(", ");
                    }
                }
            }
            tvContributors.setText(formattedList.toString());
        } else {
            tvContributors.setText("No other contributors listed.");
        }

        if (uploader != null && !uploader.isEmpty()) {
            String[] nameParts = uploader.trim().split("\\s+");
            String initials = "";
            if (nameParts.length > 0) {
                initials += nameParts[0].charAt(0);
                if (nameParts.length > 1) {
                    initials += nameParts[nameParts.length - 1].charAt(0);
                }
            }
            tvInitial.setText(initials.toUpperCase());
        }

        if (year != null) {
            tvYear.setText(" | " + year);
        } else {
            tvYear.setText("N/A");
        }

        if (technologies != null && !technologies.isEmpty()) {
            techChipGroup.removeAllViews();
            String[] techs = technologies.split(",");
            for (String t : techs) {
                String cleanTech = t.trim();
                if (!cleanTech.isEmpty()) {
                    Chip chip = new Chip(this);
                    chip.setText(cleanTech);
                    chip.setChipStrokeWidth(2f);
                    chip.setChipStrokeColorResource(android.R.color.darker_gray);
                    chip.setChipBackgroundColorResource(android.R.color.transparent);
                    techChipGroup.addView(chip);
                }
            }
        }

        if (headerTitle != null) headerTitle.setText(R.string.title_post);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        setupExpandableCard(contributorsCard, tvContributors, ivArrowContributors);
    }

    class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {
        private List<String> images;
        public ImageSliderAdapter(List<String> images) { this.images = images; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String imageUrl = images.get(position);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.gallery)
                    .error(R.drawable.gallery)
                    .centerCrop()
                    .into((ImageView) holder.itemView);
        }
        @Override
        public int getItemCount() { return images != null ? images.size() : 0; }
        class ViewHolder extends RecyclerView.ViewHolder { public ViewHolder(@NonNull View itemView) { super(itemView); } }
    }

    private void setupExpandableCard(CardView card, final View content, final ImageView arrow) {
        card.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) card.getParent(), new AutoTransition());
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                arrow.animate().rotation(180).setDuration(250).start();
            } else {
                content.setVisibility(View.GONE);
                arrow.animate().rotation(0).setDuration(250).start();
            }
        });
    }

    private void showBottomSheet() {
        if (currentProjectId == null) {
            Toast.makeText(this, "Project loading, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        isProjectBookmarked(currentProjectId, isBookmarked -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.layout_bookmark, null);

            TextView btnSave = view.findViewById(R.id.bs_save);
            btnSave.setText(isBookmarked ? "Remove from Bookmarks" : "Save to Bookmarks");

            btnSave.setOnClickListener(v -> {
                toggleBookmark(currentProjectId);
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        });
    }

    //method saving and checking bookmakrs
    private void isProjectBookmarked(String projectId, BookmarkCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            callback.onResult(false);
            return;
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("Users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> bookmarks = (List<String>) documentSnapshot.get("bookmarks");
                        callback.onResult(bookmarks != null && bookmarks.contains(projectId));
                    } else{
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }
    interface BookmarkCallback {
        void onResult(boolean isBookmarked);
    }

    private void toggleBookmark(String projectId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in to bookmark a project.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.firestore.DocumentReference userReference =
                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Users").document(userID);

        isProjectBookmarked(projectId, isBookmarked -> {
            if (isBookmarked) {
                userReference.update("bookmarks", FieldValue.arrayRemove(projectId))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Removed from Bookmarks", Toast.LENGTH_SHORT).show();
                        })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error removing bookmark", Toast.LENGTH_SHORT).show());

        } else {
                Map<String, Object> data = new java.util.HashMap<>();
                data.put("bookmarks", FieldValue.arrayUnion(projectId));

                userReference.set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Saved to Bookmarks", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error saving bookmark", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Log.e("ProjectPostActivity", "Error saving bookmark", e);
                            Toast.makeText(this, "Error saving bookmark", Toast.LENGTH_SHORT).show();

                        });
            }
        });
    }
}