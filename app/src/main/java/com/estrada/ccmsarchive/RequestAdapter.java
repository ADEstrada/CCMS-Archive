package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        // Format and set the date
        if (project.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String dateStr = sdf.format(project.getTimestamp().toDate());
            holder.tvDate.setText("Date: " + dateStr);
        } else {
            holder.tvDate.setText("Date: Unknown");
        }

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
        TextView tvTitle, tvStudent, tvDescription, tvDate;
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.req_project_title);
            tvStudent = itemView.findViewById(R.id.req_student_name);
            tvDate = itemView.findViewById(R.id.req_date);
            tvDescription = itemView.findViewById(R.id.req_description);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }

    //update status and find id of instructor
    private void updateStatus(String docId, String newStatus, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentAuthUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentAuthUid).get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String instructorIdNum = userDoc.getString("idNumber");

                        if (instructorIdNum != null) {
                            db.collection("instructor_masterlist").document(instructorIdNum).get()
                                    .addOnSuccessListener(masterlistDoc -> {
                                        String fullName = "an Instructor";
                                        if (masterlistDoc.exists()) {
                                            String first = masterlistDoc.getString("firstName");
                                            String last = masterlistDoc.getString("lastName");
                                            fullName = first + " " + last;
                                        }

                                        processProjectUpdate(docId, newStatus, view, fullName);
                                    });
                        } else {
                            processProjectUpdate(docId, newStatus, view, "an Instructor");
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void processProjectUpdate(String docId, String newStatus, View view, String instructorName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pending_Projects").document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentUid = documentSnapshot.getString("uploaderUid");
                        String projName = documentSnapshot.getString("title");

                        if (newStatus.equals("Approved")) {
                            Map<String, Object> data = documentSnapshot.getData();
                            data.put("status", "Approved");

                            db.collection("Projects").add(data)
                                    .addOnSuccessListener(ref -> {
                                        db.collection("Pending_Projects").document(docId).delete();
                                        sendNotification(studentUid, projName, newStatus, instructorName);
                                        Toast.makeText(view.getContext(), "Approved!", Toast.LENGTH_SHORT).show();
                                    });
                        } else if (newStatus.equals("Rejected")) {
                            db.collection("Pending_Projects").document(docId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        sendNotification(studentUid, projName, newStatus, instructorName);
                                        Toast.makeText(view.getContext(), "Rejected", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
    }

    //send notification to users
    private void sendNotification(String studentUid, String projName, String status, String instructor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notif = new HashMap<>();

        notif.put("userID", studentUid);

        if ("Approved".equalsIgnoreCase(status)) {
            notif.put("title", "Project Approved!");
            notif.put("message", "Your project '" + projName + "' has been approved by " + instructor + ". and is now live!");
        } else {
            notif.put("title", "Project Rejected!");
            notif.put("message", "Your project '" + projName + "' was not approved by " + instructor + ". Please check the guidelines.");
        }

        notif.put("status", status);
        notif.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("Notifications").add(notif);
    }
}