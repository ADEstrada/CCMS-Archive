package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText etInput;
    private ImageView btnSend, btnBack;
    private TextView tvName, tvInitials;

    private FirebaseFirestore db;
    private String currentUserId;
    private String receiverId;
    private com.google.firebase.firestore.ListenerRegistration chatListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMessages = findViewById(R.id.rvChatMessages);
        etInput = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btn_back_message);
        tvName = findViewById(R.id.tvChatName);
        tvInitials = findViewById(R.id.tvChatInitial);

        String name = getIntent().getStringExtra("USER_NAME");
        String initials = getIntent().getStringExtra("USER_INITIALS");
        receiverId = getIntent().getStringExtra("RECEIVER_ID");

        if (receiverId == null || receiverId.isEmpty()) {
            android.widget.Toast.makeText(this, "Error: Could not start chat (Missing User ID)", android.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(name != null ? name : "User");
        tvInitials.setText(initials != null ? initials : "--");

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, ChatList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, initials);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        listenForMessages();

        btnSend.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty() && receiverId != null) {
                saveMessageToFirestore(text);
                etInput.setText("");
            }
        });

    }

    private void saveMessageToFirestore(String text) {
        String chatId = getChatId(currentUserId, receiverId);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentUserId);
        messageData.put("text", text);
        messageData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Chats").document(chatId)
                .collection("messages").add(messageData);

        Map<String, Object> chatData = new HashMap<>();
        chatData.put("lastMessage", text);
        chatData.put("timestamp", FieldValue.serverTimestamp());
        chatData.put("participants", Arrays.asList(currentUserId, receiverId));

        db.collection("Chats").document(chatId).set(chatData, SetOptions.merge());
    }

    private String getChatId(String id1, String id2) {
        // 1. STRENGTHENED SAFETY CHECK: Iwas NullPointerException sa compareTo
        if (id1 == null || id2 == null || id1.isEmpty() || id2.isEmpty()) {
            android.util.Log.e("CHAT_ERROR", "One of the IDs is null! id1: " + id1 + ", id2: " + id2);
            return "temp_chat_id";
        }

        if (id1.compareTo(id2) < 0) {
            return id1 + "_" + id2;
        } else {
            return id2 + "_" + id1;
        }
    }

    private void listenForMessages() {
        String chatId = getChatId(currentUserId, receiverId);
        chatListener = db.collection("Chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        messageList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String text = doc.getString("text");
                            String senderId = doc.getString("senderId");

                            android.util.Log.d("CHAT_DEBUG", "Sender: " + senderId + " | Me: " + currentUserId);

                            boolean isMe = false;
                            if (senderId != null && currentUserId != null) {
                                isMe = senderId.trim().equals(currentUserId.trim()); // Gumamit ng trim() para sigurado
                            }

                            Timestamp timestamp = doc.getTimestamp("timestamp");
                            String timeString = "Sending...";
                            if (timestamp != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                timeString = sdf.format(timestamp.toDate());
                            }

                            messageList.add(new Message(text, timeString, isMe));
                        }
                        adapter.notifyDataSetChanged();
                        if (messageList.size() > 0) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (chatListener != null) {
            chatListener.remove();
        }
    }
}