package com.example.quickscanner.ui.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.EventListener;


public class AttendanceActivity extends AppCompatActivity
{
    private TextView liveAttendanceCount;
    private FirebaseAttendanceController fbAttendanceController;


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
        liveAttendanceCount = findViewById(R.id.live_attendance_count);
        fbAttendanceController = new FirebaseAttendanceController();
        DocumentReference liveCountRef = fbAttendanceController.getLiveCountRef(eventID);
        liveCountRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("live count listener", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Long liveCount = snapshot.getLong("attendanceCount");
                    liveAttendanceCount.setText("Live Attendance Count: " + liveCount);
                } else {
                    liveAttendanceCount.setText("Live Attendance Count: 0");
                }
            }
        });
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
        bundle.putString("eventID", eventID);

        // kinda a hack fix but oh well
        navController.navigate(R.id.navigation_signed_up, bundle);

        // Set up the OnNavigationItemSelectedListener

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_signed_up)
            {   //log of id for navigation signed up
                Log.d("ItemID", String.valueOf(itemId));
                //log of id for navigation signed up
                Log.d("ID for navigation signed up", String.valueOf(R.id.navigation_signed_up));
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
            return false;
        });
    }
}