package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String receiverInitials;

    public MessageAdapter(List<Message> messageList, String receiverInitials) {
        this.messageList = messageList;
        this.receiverInitials = receiverInitials;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isSentByMe()) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (getItemViewType(position) == TYPE_SENT) {
            ((SentViewHolder) holder).bind(message);
        } else {
            ((ReceivedViewHolder) holder).bind(message, receiverInitials);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        SentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageSent);
            tvTime = itemView.findViewById(R.id.tvTimeSent);
        }
        void bind(Message message) {
            tvMessage.setText(message.getText());
            tvTime.setText(message.getTime());
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvInitials;
        ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageReceived);
            tvTime = itemView.findViewById(R.id.tvTimeReceived);
            tvInitials = itemView.findViewById(R.id.tvInitialsReceived);
        }
        void bind(Message message, String initials) {
            tvMessage.setText(message.getText());
            tvTime.setText(message.getTime());
            tvInitials.setText(initials);
        }
    }
}