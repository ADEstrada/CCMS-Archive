package com.estrada.ccmsarchive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<ProjectPreview> projectList;
    private int layout_id;

    public ProjectAdapter(List<ProjectPreview> projectList, int layout_id) {
        this.projectList = projectList;
        this.layout_id = layout_id;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectPreview project = projectList.get(position);

        if (holder.tvProjectName != null) holder.tvProjectName.setText(project.getProjectName());
        if (holder.tvDescription != null) holder.tvDescription.setText(project.getDescription());
        if (holder.tvUploader != null) holder.tvUploader.setText("Author: " + project.getUploader());
        if (holder.tvCourse != null) holder.tvCourse.setText("Course: " + project.getCourse()); // NEW

        if (holder.tvProgram != null) holder.tvProgram.setText(project.getProgram());

        if (holder.ivPreview != null) {
            List<String> images = project.getImageData();
            if (images != null && !images.isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(images.get(0)).placeholder(R.drawable.gallery).into(holder.ivPreview);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ProjectPostsActivity.class);
            intent.putExtra("PROJECT_NAME", project.getProjectName());
            intent.putExtra("DESCRIPTION", project.getDescription());
            intent.putExtra("UPLOADER", project.getUploader());
            intent.putExtra("PROGRAM", project.getProgram());
            intent.putExtra("YEAR", project.getYear());
            intent.putExtra("COURSE", project.getCourse());
            intent.putExtra("TECHNOLOGIES", project.getTechUsed());
            intent.putExtra("CONTRIBUTORS", project.getContributors());
            context.startActivity(intent);
        });
        
        holder.descScrollView = holder.itemView.findViewById(R.id.descScrollView);
        
        if (holder.descScrollView != null) {
            holder.descScrollView.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });
        }

    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        public View descScrollView;
        androidx.cardview.widget.CardView cardView;
        TextView tvProjectName, tvDescription, tvUploader, tvCourse, tvProgram;
        ImageView ivPreview;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.postCardView);
            tvProjectName = itemView.findViewById(R.id.projectName);
            tvDescription = itemView.findViewById(R.id.projectDescription);
            tvUploader = itemView.findViewById(R.id.uploaderName);
            tvCourse = itemView.findViewById(R.id.courseName);
            ivPreview = itemView.findViewById(R.id.projectPreview);
            tvProgram = itemView.findViewById(R.id.programTv);
        }
    }

    public void updateList(List<ProjectPreview> newList) {
        this.projectList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}