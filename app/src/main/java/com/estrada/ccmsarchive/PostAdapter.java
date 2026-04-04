package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostPreview> postList;
    // jasmine 4/3/26
    private int layout_id;

    public PostAdapter(List<PostPreview> postList, int layout_id) {
        this.postList = postList;
        this.layout_id = layout_id;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostPreview project = postList.get(position);

        holder.tvProjectName.setText(project.getProjectName());
        holder.tvStatus.setText(project.getStatus());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName, tvStatus;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.projectName);
            tvStatus = itemView.findViewById(R.id.statusText);
        }
    }
}