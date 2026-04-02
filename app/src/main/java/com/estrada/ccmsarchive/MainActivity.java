package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private CardView profileCard;
    private BottomNavigationView bottomNav;
    private TextView profileInitials;
    private TextView btnEditProfile;
    private TextView btnChangePassword;
    private TextView btnAboutCCMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        profileInitials = findViewById(R.id.profile_initials);
        profileCard = findViewById(R.id.cardProfile);
        bottomNav = findViewById(R.id.bottomNavigationView);

        // SIDE BAR
        btnEditProfile = findViewById(R.id.menu_edit_profile);
        btnChangePassword = findViewById(R.id.menu_change_password);
        btnAboutCCMS = findViewById(R.id.menu_about);

        // Retrieve the strings passed from the Intent
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");

        // Logic to combine and display initials
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            String initial1 = firstName.substring(0, 1).toUpperCase();
            String initial2 = lastName.substring(0, 1).toUpperCase();
            profileInitials.setText(initial1 + initial2);
        } else {
            // Logging in, fetch from database later. Using a default rn.
            profileInitials.setText("AE");
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // BOTTOM NAVIGATION FOR FRAGMENTS
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                findViewById(R.id.headerview).setVisibility(View.VISIBLE);
            } else if (id == R.id.nav_bookmark) {
                selectedFragment = new BookmarkFragment();
                findViewById(R.id.headerview).setVisibility(View.GONE);
            } else if (id == R.id.nav_add) {
                selectedFragment = new PostFragment();
                findViewById(R.id.headerview).setVisibility(View.GONE);
                findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
            }

            return loadFragment(selectedFragment);
        });


        // SIDEBAR
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        CardView cardProfile = findViewById(R.id.cardProfile);

        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // EDIT PROFILE IN SIDEBAR
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        // CHANGE PASSWORD IN SIDEBAR
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangePassword.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        // ABOUT CCMS IN SIDEBAR
        btnAboutCCMS.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutCCMS.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });
    }

    public void setUIVisibility(boolean isMainPage) {
        if (profileCard != null) {
            profileCard.setVisibility(isMainPage ? View.VISIBLE : View.GONE);
        }
        if (bottomNav != null) {
            bottomNav.setVisibility(isMainPage ? View.VISIBLE : View.GONE);
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}