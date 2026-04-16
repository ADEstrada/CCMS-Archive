package com.estrada.ccmsarchive;

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

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText etInput;
    private ImageView btnSend, btnBack;
    private TextView tvName, tvInitials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

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

            tvName.setText(name != null ? name : "User");
            tvInitials.setText(initials != null ? initials : "--");

            btnBack.setOnClickListener(v -> finish());

            messageList = new ArrayList<>();

            adapter = new MessageAdapter(messageList, initials);
            rvMessages.setLayoutManager(new LinearLayoutManager(this));
            rvMessages.setAdapter(adapter);


            btnSend.setOnClickListener(v -> {
                String text = etInput.getText().toString().trim();
                if (!text.isEmpty()) {
                    messageList.add(new Message(text, "4:00 PM", true));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    rvMessages.scrollToPosition(messageList.size() - 1);
                    etInput.setText("");
                }
            });
    }
}
