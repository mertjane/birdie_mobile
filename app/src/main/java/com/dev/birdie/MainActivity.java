package com.dev.birdie;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigationView);
        fabHome = findViewById(R.id.fabHome);

        // Disable placeholder home item
        bottomNavigation.getMenu().findItem(R.id.homeTab).setEnabled(false);

        // Load home fragment by default
        loadFragment(new HomeFragment());

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.profileTab) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.notificationTab) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.messagesTab) {
                selectedFragment = new MessagesFragment();
            } else if (itemId == R.id.settingsTab) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        fabHome.setOnClickListener(v -> {
            // Clear all selections
            bottomNavigation.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
                bottomNavigation.getMenu().getItem(i).setChecked(false);
            }
            bottomNavigation.getMenu().setGroupCheckable(0, true, true);

            // Load home fragment
            loadFragment(new HomeFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}