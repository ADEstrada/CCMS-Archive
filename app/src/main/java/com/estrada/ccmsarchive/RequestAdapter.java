package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<ProjectPreview> projectList;

    public RequestAdapter(List<ProjectPreview> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ProjectPreview project = projectList.get(position);

        holder.tvTitle.setText(project.getProjectName());
        holder.tvStudent.setText("Submitted by: " + project.getUploader());
        holder.tvDescription.setText(project.getDescription());

        holder.btnApprove.setOnClickListener(v -> {
            if (project.getDocumentId() != null) {
                updateStatus(project.getDocumentId(), "Approved", holder.itemView);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (project.getDocumentId() != null) {
                updateStatus(project.getDocumentId(), "Rejected", holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStudent, tvDescription;
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.req_project_title);
            tvStudent = itemView.findViewById(R.id.req_student_name);
            tvDescription = itemView.findViewById(R.id.req_description);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }

    private void updateStatus(String docId, String newStatus, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Pending_Projects").document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (newStatus.equals("Approved")) {

                            java.util.Map<String, Object> data = documentSnapshot.getData();
                            data.put("status", "Approved");

                            db.collection("Projects").add(data)
                                    .addOnSuccessListener(documentReference -> {
                                        db.collection("Pending_Projects").document(docId).delete();
                                        Toast.makeText(view.getContext(), "Project Approved and Moved to Home!", Toast.LENGTH_SHORT).show();
                                    });
                        } else if (newStatus.equals("Rejected")) {
                            db.collection("Pending_Projects").document(docId).delete();
                            Toast.makeText(view.getContext(), "Project Rejected", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}