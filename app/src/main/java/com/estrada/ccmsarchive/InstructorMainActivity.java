package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class InstructorMainActivity extends AppCompatActivity {

    private CardView profileCard;
    private BottomNavigationView bottomNav;
    private TextView profileInitials;
    private TextView sidebarInitials;

    private TextView btnProfileDetails;
    private TextView btnEditProfile;
    private TextView btnChangePassword;
    private TextView btnAboutCCMS;
    private TextView sidebarFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructor_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileInitials = findViewById(R.id.profile_initials);
        profileCard = findViewById(R.id.cardProfile);
        bottomNav = findViewById(R.id.bottomNavigationView);

        // SIDE BAR
        btnProfileDetails = findViewById(R.id.sidebar_initials);
        btnEditProfile = findViewById(R.id.menu_edit_profile);
        btnChangePassword = findViewById(R.id.menu_change_password);
        btnAboutCCMS = findViewById(R.id.menu_about);
        sidebarFullName = findViewById(R.id.sidebar_user_name);
        sidebarInitials = findViewById(R.id.sidebar_initials);

        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");

        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            String initial1 = firstName.substring(0, 1).toUpperCase();
            String initial2 = lastName.substring(0, 1).toUpperCase();
            profileInitials.setText(initial1 + initial2);
            sidebarInitials.setText(initial1 + initial2);

            if (sidebarFullName != null) {
                sidebarFullName.setText(firstName + " " + lastName);
            }
        } else {
            profileInitials.setText("AE");
            if (sidebarFullName != null) sidebarFullName.setText("Guest User");
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // BOTTOM NAVIGATION FOR FRAGMENTS
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                if (findViewById(R.id.headerview) != null) {
                    findViewById(R.id.headerview).setVisibility(View.VISIBLE);
                }
            } else if (itemId == R.id.nav_bookmarks) {
                selectedFragment = new BookmarkFragment();
                if (findViewById(R.id.headerview) != null) {
                    findViewById(R.id.headerview).setVisibility(View.GONE);
                }
            } else if (itemId == R.id.nav_request) {
                selectedFragment = new RequestFragment();
                if (findViewById(R.id.headerview) != null) {
                    findViewById(R.id.headerview).setVisibility(View.GONE);
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, selectedFragment)
                        .commit();
            }
            return true;
        });

        // MESSAGE BUTTON
        findViewById(R.id.btn_message).setOnClickListener(v -> {
            startActivity(new android.content.Intent(InstructorMainActivity.this, ChatList.class));
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

        // PROFILE DETAILS IN SIDEBAR
        btnProfileDetails.setOnClickListener(v -> {
            Intent intent = new Intent(InstructorMainActivity.this, ProfileDetails.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        // EDIT PROFILE IN SIDEBAR
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(InstructorMainActivity.this, EditProfile.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        // CHANGE PASSWORD IN SIDEBAR
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(InstructorMainActivity.this, ChangePassword.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        // ABOUT CCMS IN SIDEBAR
        btnAboutCCMS.setOnClickListener(v -> {
            Intent intent = new Intent(InstructorMainActivity.this, AboutCCMS.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        //SEARCH BAR

        EditText searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            // Not needed for this example

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                if (currentFragment instanceof HomeFragment) {
                    ((HomeFragment) currentFragment).performSearch(s.toString());
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}

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