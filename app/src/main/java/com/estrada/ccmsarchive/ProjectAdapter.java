package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<ProjectPreview> projectList;

    public ProjectAdapter(List<ProjectPreview> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectPreview project = projectList.get(position);

        holder.tvProjectName.setText(project.getProjectName());
        holder.tvDescription.setText(project.getDescription());
        holder.tvUploader.setText("Uploaded by: " + project.getUploader());

        // Add dynamic images logic here.
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName, tvDescription, tvUploader;
        ImageView ivPreview;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.projectName);
            tvDescription = itemView.findViewById(R.id.projectDescription);
            tvUploader = itemView.findViewById(R.id.uploaderName);
            ivPreview = itemView.findViewById(R.id.projectPreview);
        }
    }
}