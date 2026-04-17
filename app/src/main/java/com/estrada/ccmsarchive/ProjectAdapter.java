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

        holder.tvProjectName.setText(project.getProjectName());
        holder.tvDescription.setText(project.getDescription());
        holder.tvUploader.setText("Uploaded by: " + project.getUploader());

        if (project.getImageData() != null && !project.getImageData().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(project.getImageData().get(0), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivPreview.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.ivPreview.setImageResource(R.drawable.gallery);
            }
        }

        if (holder.tvProgram != null) {
            holder.tvProgram.setText(project.getProgram());
        }

        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(v -> {
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
        }
    }

    @Override
    public int getItemCount() { return projectList.size(); }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        androidx.cardview.widget.CardView cardView;
        TextView tvProjectName, tvDescription, tvUploader, tvProgram;
        ImageView ivPreview;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.postCardView);
            tvProjectName = itemView.findViewById(R.id.projectName);
            tvDescription = itemView.findViewById(R.id.projectDescription);
            tvUploader = itemView.findViewById(R.id.uploaderName);
            ivPreview = itemView.findViewById(R.id.projectPreview);
            tvProgram = itemView.findViewById(R.id.programTv);
        }
    }
}