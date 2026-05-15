package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotification;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList; //
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageView btnBack;
    private TextView headerTitle;

    public NotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        headerTitle = view.findViewById(R.id.header_title);
        btnBack = view.findViewById(R.id.btn_back);
        rvNotification = view.findViewById(R.id.rvNotifications);

        if (headerTitle != null) headerTitle.setText(R.string.txt_notif);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    MainActivity main = (MainActivity) getActivity();
                    main.findViewById(R.id.nav_home).performClick();
                }
            });
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        rvNotification.setAdapter(adapter);

        adapter.setOnNotificationLongClickListener((notification, position) -> {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Delete Notification")
                    .setMessage("Are you sure you want to delete this notification?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        notificationList.remove(position);
                        adapter.notifyItemRemoved(position);

                        db.collection("Notifications").document(notification.getNotificationId())
                                .delete()
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    fetchNotifications();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        fetchNotifications();
        return view;
    }

    private void fetchNotifications() {
        if (mAuth.getCurrentUser() == null) return;

        String userID = mAuth.getCurrentUser().getUid();

        db.collection("Notifications")
                .whereEqualTo("userID", userID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        notificationList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            NotificationModel notification = doc.toObject(NotificationModel.class);
                            if (notification != null) {
                                notification.setNotificationId(doc.getId());
                                notificationList.add(notification);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
