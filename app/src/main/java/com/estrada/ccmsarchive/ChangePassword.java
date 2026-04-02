package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        TextView headerTitle = findViewById(R.id.header_title);
        ImageView btnBack = findViewById(R.id.btn_back);
        EditText newPassField = findViewById(R.id.newPassField);
        TextView confirmLabel = findViewById(R.id.confirmPassLabel);
        EditText confirmField = findViewById(R.id.confirmPassField);

        // HEADER
        if (headerTitle != null) {
            headerTitle.setText(R.string.menu_change_password);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        // CONFIRM PASSWORD SHOW
        newPassField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If user has typed anything, show the confirm fields
                if (s.length() > 0) {
                    confirmLabel.setVisibility(View.VISIBLE);
                    confirmField.setVisibility(View.VISIBLE);
                } else {
                    confirmLabel.setVisibility(View.GONE);
                    confirmField.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

}