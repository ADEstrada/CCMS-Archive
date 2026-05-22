package com.estrada.ccmsarchive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private CardView profileCard;
    private BottomNavigationView bottomNav;
    private TextView profileInitials;
    private TextView sidebarInitials;
    private TextView btnProfileDetails;
    private TextView btnEditProfile;
    private TextView btnChangePassword;
    private TextView btnAboutCCMS;
    private TextView sidebarFullName;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private com.google.firebase.auth.FirebaseAuth mAuth;
    private com.google.firebase.firestore.FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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

        // BTN SIGN OUT
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        TextView btnSignOut = navigationView.findViewById(R.id.btn_sign_out);

        btnSignOut.setOnClickListener(v -> {
            logoutUser();
        });

        // NAME INITIALS
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String year = documentSnapshot.getString("year");

                            if (firstName != null && lastName != null) {
                                String initial1 = firstName.substring(0, 1).toUpperCase();
                                String initial2 = lastName.substring(0, 1).toUpperCase();
                                profileInitials.setText(initial1 + initial2);
                                sidebarInitials.setText(initial1 + initial2);

                                if (sidebarFullName != null) {
                                    sidebarFullName.setText(firstName + " " + lastName);
                                }

                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        sidebarFullName.setText("Error loading data");
                    });
        } else {
            // Pag walang naka-login, balik sa LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
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
               Intent intent = new Intent(MainActivity.this, PostActivity.class);
               startActivity(intent);
            } else if (id == R.id.nav_notification) {
                selectedFragment = new NotificationFragment();
                findViewById(R.id.headerview).setVisibility(View.GONE);
            }

            return loadFragment(selectedFragment);
        });

        // MESSAGE BUTTON
        findViewById(R.id.btn_message).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, ChatList.class));
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
            Intent intent = new Intent(MainActivity.this, ProfileDetails.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
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

    private void logoutUser() {

        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

        android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish(); // Isara ang MainActivity
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}