
package com.estrada.ccmsarchive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Secret code variables
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

        // --- SECRET CODE LOGIC START ---
        logo.setOnClickListener(v -> {
            secretClickCount++;
            if (secretClickCount == REQUIRED_CLICKS) {
                // Reset count para hindi mag-loop
                secretClickCount = 0;

                // Lilipat agad sa Seeder Activity
                Toast.makeText(this, "Admin Mode: Data Seeder Activated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, DataSeederActivity.class));
                finish();

                // Tatanggalin natin ang pending na transition sa Login para hindi magulo
                System.exit(0); // Optional: Pwede ring alisin ang handler logic sa baba
            }
        });
        // --- SECRET CODE LOGIC END ---

        new Handler().postDelayed(() -> {
            // I-check kung nakapag-secret code na (kung finish na ang activity, hindi na ito tatakbo)
            if (!isFinishing()) {
                if (isFirstTime) {
                    pref.edit().putBoolean("isFirstTime", false).apply();
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000); // Ginawa nating 3 seconds para may time kang mag-tap
    }
}