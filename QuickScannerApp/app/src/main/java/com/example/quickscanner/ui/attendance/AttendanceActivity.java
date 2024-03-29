package com.example.quickscanner.ui.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quickscanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AttendanceActivity extends AppCompatActivity
{
    private SharedViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");


        // Add the top bar (ActionBar)
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attendance Information");
        }

        // Create bottom menu for Attendance Activity.
        createBottomMenu(eventID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return false;
    }

    private void createBottomMenu(String eventID)
    {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_signed_up, R.id.navigation_checked_in)
                .build();

        // Find the NavController for your main host fragment
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_attendance_activity);

        // Set up the BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.attendance_nav);

        // Create the bundle
        Bundle bundle = new Bundle();
        bundle.putString("eventID", eventID); // Replace with your actual string

        // Set up the OnNavigationItemSelectedListener

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_signed_up)
                {
                    navController.navigate(R.id.navigation_signed_up, bundle);
                    return true;
                }
                else if (itemId == R.id.navigation_checked_in)
                {
                    navController.navigate(R.id.navigation_checked_in, bundle);
                    return true;
                }
                return false;
            }
        });
    }
}