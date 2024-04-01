package com.example.quickscanner.ui.my_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.databinding.ActivityMainBinding;
import com.example.quickscanner.databinding.ActivityMyEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;
import java.util.Objects;

public class MyEvents_Activity extends AppCompatActivity {
    /**
     * Our MyEvents Activity hosts our two main menu options.
     * Attending Events Page, and Organized Events Page..
     */


    private AppBarConfiguration appBarConfiguration;
    private ListView eventsListView;
    private ArrayList<Event> eventsDataList;
    private FirebaseUserController fbUserController;

    private ActivityMyEventsBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyEventsBinding.inflate(getLayoutInflater());

        // Set the content view to the my events activity layout.
        setContentView(binding.getRoot());
        fbUserController = new FirebaseUserController();

        // Create bottom menu for My Events Activity.
        createBottomMenu();

        // Set up the back button in the action bar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);



    }

    private void createBottomMenu(){
        /*
            Creates the bottom menu of our my events Activity
            Usage: call once.
            no inputs/outputs
         */
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_attending_events,  R.id.navigation_organized_events)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_my_events_activity);
        NavigationUI.setupWithNavController(binding.eventNavView, navController);
    }



    /*         Inflate Handle Top Menu Options        */
    // Create the Top Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }
    /*    Handle click events for the Top Menu Bar    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            // Handle the Back button press by finishing the activity.
            finish();
            return true;
        }
        return false;
    }


}