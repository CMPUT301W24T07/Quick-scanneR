package com.example.quickscanner;

import android.os.Bundle;
import android.view.Menu; // Import Menu class
import android.view.MenuItem; // Import Menu class
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quickscanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;

    // Function to handle redirecting to other activities
    private final ActivityResultLauncher<Intent> startProfileActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // Handle the result if needed
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create bottom menu
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_events, R.id.navigation_scanner, R.id.navigation_announcements)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    // Create the Top Bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }
    // Handle click events for the Top Bar Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_profile) {// Handle Edit Profile click
            // Handle Edit Profile click
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startProfileActivityLauncher.launch(intent);
            return true;
        } else if (itemId == R.id.navigation_adminPage) {
            // Handle Admin Page Click
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startProfileActivityLauncher.launch(intent);
        } else if (itemId == R.id.navigation_myEvents) {
            // Handle Events click
            Toast.makeText(this, "Events Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_settings) {
            // Handle Scanner click
            Toast.makeText(this, "Scanner Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


}