package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notificationList;

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }
    // 1. Define the interface
    public interface OnNotificationLongClickListener {
        void onLongClick(NotificationModel notification, int position);
    }

    private OnNotificationLongClickListener longClickListener;

    // 2. Setter for the listener
    public void setOnNotificationLongClickListener(OnNotificationLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());

        //change icon based on the notif
        if (notification.getStatus() != null) {
            if (notification.getStatus().equalsIgnoreCase("Approved")) {
                holder.imgIcon.setImageResource(R.drawable.check);
            } else if (notification.getStatus().equalsIgnoreCase("Rejected")) {
                holder.imgIcon.setImageResource(R.drawable.reject);
            } else {
                holder.imgIcon.setImageResource(R.drawable.default_notif);
            }
        }

        //timestamp
        if (notification.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = sdf.format(notification.getTimestamp().toDate());
            holder.tvTimestamp.setText(time);
        } else {
            holder.tvTimestamp.setText("");
        }

        //deleting a notification - long click listener to  card
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(notification, position);
            }
            return true; // Return true to indicate that the long click event is handled
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimestamp;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txtNotifContext);
            tvMessage = itemView.findViewById(R.id.txtNotificationMessage);
            tvTimestamp = itemView.findViewById(R.id.txtTimestamp);
            imgIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}