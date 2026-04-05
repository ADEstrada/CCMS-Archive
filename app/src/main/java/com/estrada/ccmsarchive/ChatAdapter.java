package com.estrada.ccmsarchive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatUser> chatUserList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatUser user);
    }

    public ChatAdapter(List<ChatUser> chatUserList, OnChatClickListener listener) {
        this.chatUserList = chatUserList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatUser user = chatUserList.get(position);
        holder.tvUserName.setText(user.getName());
        holder.tvLastMessage.setText(user.getLastMessage());
        holder.tvTime.setText(user.getTime());
        holder.tvInitials.setText(user.getInitials());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvLastMessage, tvTime, tvInitials;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvInitials = itemView.findViewById(R.id.tvInitials);
        }
    }
}