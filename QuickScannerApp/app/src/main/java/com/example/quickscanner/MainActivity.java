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
import com.example.quickscanner.model.Profile;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private FirebaseController fbController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Testing", "in onCreate");
        Toast.makeText(this, "First sign in detected", Toast.LENGTH_SHORT).show();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fbController = new FirebaseController();
        // Check user sign-in status
        Log.e("Testing", "outside the if statement");
        if (fbController.isFirstSignIn()) {
            Log.e("Testing", "Entered the if statement");
            //creates an anonymous user if not signed in
            createUserAndSignIn();
        }
        // Create bottom menu for MainActivity.
        createBottomMenu();


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
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
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
    public void createUserAndSignIn() {
            // Creates anonymous user
            fbController.createAnonymousUser().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If user creation is successful
                    if (task.isSuccessful()) {
                        User user = new User();
                        String userId;
                        userId = fbController.getCurrentUserUid();
                        // Sets user UID
                        user.setUid(userId);
                        // Adds user to database
                        fbController.addUser(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                // If user addition is successful
                                if (task.isSuccessful()) {
                                    // Logs success
                                    Log.d("Testing", "User added successfully");
                                } else {
                                    // Logs error
                                    Log.w("Testing", "Error adding user", task.getException());
                                }
                            }
                        });
                    } else {
                        // Logs error
                        Log.w("Testing", "Error creating anonymous user", task.getException());
                    }
                }
            });
    }
//    public void testNewEvent(User user) {
//        // Creates a new Event object
//        Event event = new Event();
//
//        // Sets the organizerUID of the event to the UID of the user
//        event.setOrganizerUid(user.getUid());
//
//        // Adds the event to Firestore
//        fbController.addEvent(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                // Gets the ID of the newly created document
//                String docId = documentReference.getId();
//
//                // Updates the document in Firestore to include the ID as a field
//                fbController.updateEvent(docId, event);
//
//                // Logs the ID of the newly created event
//                Log.d(TAG, "Event added with ID: " + docId);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Logs an error if the document could not be added
//                // This is important for understanding and resolving failures
//                Log.w(TAG, "Error adding document", e);
//            }
//        });
//    }
}