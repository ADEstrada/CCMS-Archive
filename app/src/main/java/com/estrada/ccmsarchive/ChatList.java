package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatList extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;
    private List<ChatUser> userList;
    private TextView headerTitle;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        headerTitle = findViewById(R.id.header_title);

        if (headerTitle != null) {
            headerTitle.setText(R.string.messageTitle);
        }

        db = FirebaseFirestore.getInstance();
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rvChatList = findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        chatAdapter = new ChatAdapter(userList, user -> {
            Intent intent = new Intent(ChatList.this, MessageActivity.class);
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_INITIALS", user.getInitials());
            intent.putExtra("RECEIVER_ID", user.getUid());
            startActivity(intent);
        });
        rvChatList.setAdapter(chatAdapter);

        // LOAD CONVERSATIONS
        db.collection("Chats")
                .whereArrayContains("participants", myId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        userList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            List<String> participants = (List<String>) doc.get("participants");
                            if (participants == null) continue;

                            String otherId = participants.get(0).equals(myId) ? participants.get(1) : participants.get(0);
                            String lastMsg = doc.getString("lastMessage");

                            Timestamp time = doc.getTimestamp("timestamp");
                            String formattedTime = "";
                            if (time != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
                                formattedTime = sdf.format(time.toDate());
                            }
                            final String finalTime = formattedTime;

                            db.collection("users").document(otherId).get().addOnSuccessListener(userDoc -> {
                                if (userDoc.exists()) {
                                    String firstName = userDoc.getString("firstName");
                                    String lastName = userDoc.getString("lastName");

                                    String fInitial = (firstName != null && !firstName.isEmpty()) ? String.valueOf(firstName.charAt(0)) : "";
                                    String lInitial = (lastName != null && !lastName.isEmpty()) ? String.valueOf(lastName.charAt(0)) : "";

                                    String fullName = firstName + " " + lastName;
                                    String combinedInitials = (fInitial + lInitial).toUpperCase();

                                    userList.add(new ChatUser(fullName, lastMsg, finalTime, combinedInitials, otherId));
                                    chatAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}