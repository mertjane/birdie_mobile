package com.dev.birdie;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabHome;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables full screen
        setContentView(R.layout.activity_main);

        // Initialize Views
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        fabHome = findViewById(R.id.fabHome);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        // Inside MainActivity.java -> onCreate

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_coordinator), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // 1. The White Background Bar
            bottomAppBar.setPadding(0, 0, 0, systemBars.bottom);

            // 2. The Icons (THE FIX)
            // We calculate 12dp in pixels to push the icons down slightly
            int topPadding = (int) (12 * getResources().getDisplayMetrics().density);

            // Apply topPadding (2nd argument) to push icons down
            bottomNavigation.setPadding(0, topPadding, 0, 0);

            // 3. The FAB
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fabHome.getLayoutParams();
            params.setMargins(0, 0, 0, systemBars.bottom + 20);
            fabHome.setLayoutParams(params);

            return insets;
        });

        // Disable the placeholder item in the middle so clicking it does nothing
        bottomNavigation.getMenu().findItem(R.id.homeTab).setEnabled(false);
        // Important: Make the placeholder invisible or null icon to ensure spacing
        // (You already did this in XML with @android:color/transparent, which is correct)

        setupNavigation();

        // Load Home by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            clearBottomNavSelection(); // Home is the FAB, so no tab selected
        }
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
            clearBottomNavSelection();
            loadFragment(new HomeFragment());
        });
    }

    private void clearBottomNavSelection() {
        // Uncheck all items in the bottom nav
        int size = bottomNavigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigation.getMenu().getItem(i).setChecked(false);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}