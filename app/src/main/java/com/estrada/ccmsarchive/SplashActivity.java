
package com.estrada.ccmsarchive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private int secretClickCount = 0;
    private static final int REQUIRED_CLICKS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("CCMS_PREFS", MODE_PRIVATE);
        boolean isFirstTime = pref.getBoolean("isFirstTime", true);

        ImageView logo = findViewById(R.id.splash_logo);
        logo.setAlpha(0f);
        logo.animate().alpha(1f).setDuration(1200).start();

        // SECRET CODE LOGIC START
        logo.setOnClickListener(v -> {
            secretClickCount++;
            if (secretClickCount == REQUIRED_CLICKS) {
                secretClickCount = 0;

                Toast.makeText(this, "Admin Mode: Data Seeder Activated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, DataSeederActivity.class));
                finish();

                System.exit(0);
            }
        });

        // --- SECRET CODE LOGIC END ---

        new Handler().postDelayed(() -> {

            if (!isFinishing()) {
                if (isFirstTime) {
                    pref.edit().putBoolean("isFirstTime", false).apply();
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}