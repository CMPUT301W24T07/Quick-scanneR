package com.example.quickscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu; // Import Menu class
import android.view.MenuItem; // Import Menu class
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.singletons.SettingsDataSingleton;
import com.example.quickscanner.ui.my_events.MyEvents_Activity;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;

import com.example.quickscanner.ui.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quickscanner.databinding.ActivityMainBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import ch.hsr.geohash.GeoHash;

public class MainActivity extends AppCompatActivity {
    /**
     * Our Main Activity hosts our three main menu options.
     * Events Page, QR-Scanner Page, and Announcements Page.
     * Also handles user login, such that every phone is a unique user
     * and everytime you open the app on your phone, you keep the same user info.
     */


    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private ListView eventsListView;
    private ArrayList<Event> eventsDataList;
    private FirebaseUserController fbUserController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // references
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fbUserController = new FirebaseUserController();

        // create Singletons
        initSingletons();

        // Check user sign-in status
        boolean isFirstSignIn = fbUserController.isFirstSignIn();
        Log.e("Testing", "Is first sign in? " + isFirstSignIn);
        if (fbUserController.isFirstSignIn()) {
            Log.e("Testing", "Entered the if statement");
            //creates an anonymous user if not signed in
            createUserAndSignIn();
        } else {
            Log.e("Testing", "first signin not detected");
            requestHashedGeolocation();
        }



        // Create bottom menu for MainActivity.
        createBottomMenu();


    }




    /*                                           *
     *            Menu Functions                 *
     *                                           */

    /*         Create and Inflate bottom menu        */
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

    /*         Inflate Handle Top Menu Options        */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }
    /*    Handle click events for the Top Menu Bar    */
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
            return true;
        } else if (itemId == R.id.navigation_myEvents) {
            // Handle My Events click
            Intent intent = new Intent(MainActivity.this, MyEvents_Activity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.navigation_settings) {
            // Handle Settings click
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /*                                           *
     *            User Sign in Functions         *
     *                                           */

    public void createUserAndSignIn() {
        // Creates anonymous user
        fbUserController.createAnonymousUser().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If user creation is successful
                if (task.isSuccessful()) {
                    User user = new User();
                    String userId;
                    userId = fbUserController.getCurrentUserUid();
                    // Sets user UID
                    user.setUid(userId);
                    user.getUserProfile().setImageUrl(user.getUserProfile().genereteProfilePicture(user.getUid()));
                    // Adds user to database
                    fbUserController.addUser(user).addOnCompleteListener(task1 -> {
                        // If user addition is successful
                        if (task1.isSuccessful()) {
                            // Logs success
                            Log.d("Testing", "User added successfully");
                            requestHashedGeolocation();
                        } else {
                            // Logs error
                            Log.w("Testing", "Error adding user", task1.getException());
                        }
                    });
                } else {
                    // Logs error
                    Log.w("Testing", "Error creating anonymous user", task.getException());
                }
            }
        });
    }

    /*                                          *
     *            Geolocation Functions          *
     *                                           */

    /*
     *   This functions returns a hashed (String) version of geolocation
     *   If User or Phone has geolocation disabled, returns NULL.
     *   Otherwise, returns a hashed String
     */
    private void requestHashedGeolocation() {
        // get current user
        String uid = fbUserController.getCurrentUserUid();
        if (uid == null)
            return;
        fbUserController.getUserTask(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                // Convert task to User class
                User user = document.toObject(User.class);
                if (!document.exists())
                    // Error Handling
                    Log.w("error", "user document doesn't exist");
                else {
                    // Query current User is Successful
                    if (user == null)
                        return;
                    // verify permissions are enabled
                    if (validGeolocationPermissions(user)) {
                        SettingsDataSingleton.getInstance().setHashedGeoLocation(getDeviceGeolocation(user));
                        Log.w("Geolocation: ", "Successful pull");
                    }
                }
            }
        });
    }

    /*
     *   Checks if a User and Device have Geolocation Tracking enabled
     *   This is used in conjunction with "getDeviceGeolocation"
     *   Returns true if enabled on both
     *   Returns false if not enabled on at least one
     */
    private Boolean validGeolocationPermissions(User user) {
        if (!user.getIsGeolocationEnabled()) {
            // User has geolocation disabled
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // Phone has geolocation disabled
            return false;
        }
        return true;
    }

    /*
     * Gets and then Returns the Geolocation from this Device.
     *   Use this in conjunction with "validGeolocationPermissions"
     *   to ensure the device and user has geolocation enabled.
     */
    @SuppressLint("MissingPermission") // we use our own permission checker
    private String getDeviceGeolocation(User user){
        // check if permissions are valid
        if (!validGeolocationPermissions(user)) {
            return null;
        }
        // Query the Device's Geolocation
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String hashLoc = null;
        // Try another Provider if the previous failed
        if (lastKnownLoc == null)
            lastKnownLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // Hash the Geolocation
        if (lastKnownLoc != null) {
            double longitude = (lastKnownLoc.getLongitude());
            double latitude = (lastKnownLoc.getLatitude());
            hashLoc = hashCoordinates(latitude, longitude);
        }
        return hashLoc;
    }

    /* Turns a Latitude and Longitude into a Hashed String
     *  Returns a hashed GeoLocation
     */
    public static String hashCoordinates(double latitude, double longitude) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 12); // int is precision
        return geoHash.toBase32();
    }

    /*
     *   Singleton Initialization
     */
    protected void initSingletons(){
        SettingsDataSingleton.initInstance();
    }


}