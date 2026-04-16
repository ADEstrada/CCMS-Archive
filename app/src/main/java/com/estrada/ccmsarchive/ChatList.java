package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatList extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;
    private List<ChatUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        TextView headerTitle = findViewById(R.id.header_title);
        ImageView btnBack = findViewById(R.id.btn_back);

        if (headerTitle != null) {
            headerTitle.setText(R.string.messageTitle);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        rvChatList = findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();

        chatAdapter = new ChatAdapter(userList, user -> {
            Intent intent = new Intent(ChatList.this, MessageActivity.class);
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_INITIALS", user.getInitials());
            startActivity(intent);
        });

        rvChatList.setAdapter(chatAdapter);
    }

    private void rvChatManager() {
        rvChatList.setLayoutManager(new LinearLayoutManager(this));
    }
}