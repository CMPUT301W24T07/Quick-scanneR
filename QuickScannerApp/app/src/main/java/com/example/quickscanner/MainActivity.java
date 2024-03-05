package com.example.quickscanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu; // Import Menu class
import android.view.MenuItem; // Import Menu class
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quickscanner.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private FirebaseFirestore db;
    private FirebaseStorage idb;
    private StorageReference storeRef;
    private CollectionReference profileRef;
    private CollectionReference userEventsRef;
    private CollectionReference imagesRef;

    // events fragment
    private CollectionReference eventsRef;
    private ListView eventsListView;
    private ArrayList<Event> eventsDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        idb = FirebaseStorage.getInstance();
        profileRef = db.collection("Profiles");
        eventsRef = db.collection("Events");
        imagesRef = db.collection("Images");

        // Create bottom menu for MainActivity.
        createBottomMenu();

        // FireStore Listener for Events
        userEventsRef = db.collection("User Events");
        eventsListView = findViewById(R.id.event_listview);
        db.collection("Events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    // listens to changes in db
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // error check
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        // refresh list of events
                        eventsDataList.clear(); // clear old data
                        // adds every event from db to the list of events
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getId() != null) {
                                String name = doc.getId(); // event name
                                String description = doc.getString("Description");
                                String eventPoster = doc.getString("Event Poster"); // event Image Path
                                String location = doc.getString("Location");
                                String organizers = doc.getString("Organizers");
                                // TODO String time = doc.getString("Time");
                                eventsDataList.add(Event(name, description, imagePath));
                            }
                        }
                        Log.d(TAG, "Current Event: " + events); // logs list of events
                    }
                });








    }



    private void createBottomMenu(){
        /*
            Creates the bottom menu of our Main Activity
            Usage: call once.
            no inputs/outputs
         */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_events, R.id.navigation_scanner, R.id.navigation_announcements)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }



    /*            Handle Top Menu Options         */
    // Create the Top Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }
    // Handle click events for the Top Menu Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_profile) {// Handle Edit Profile click
            // Handle Edit Profile click
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.navigation_adminPage) {
            // Handle Admin Page Click
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.navigation_myEvents) {
            // Handle Events click
            Toast.makeText(this, "Events Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_settings) {
            // Handle Scanner click
            Toast.makeText(this, "Scanner Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_notifications) {
            // Handle Notification Bell Click
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_notificationlist);
            ListView lv = (ListView ) dialog.findViewById(R.id.lv);
            dialog.setCancelable(true);
            dialog.setTitle("ListView");
            dialog.show();
        }
        return false;
    }


}