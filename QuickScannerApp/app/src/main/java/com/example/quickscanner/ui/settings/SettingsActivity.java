package com.example.quickscanner.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem; // Import Menu class
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.example.quickscanner.singletons.SettingsDataSingleton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import ch.hsr.geohash.GeoHash;

public class SettingsActivity extends AppCompatActivity {
    /**
     * Our Settings Activity handles global app settings
     * For instance, geolocation preferences
     * and everytime you open the app on your phone, you keep the same settings.
     */

    // Model/Controllers reference
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private FirebaseUserController fbUserController;
    private User myUser;

    // UI reference
    private Switch toggleGeolocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // references
        fbUserController = new FirebaseUserController();
        toggleGeolocation = findViewById(R.id.toggle_geolocation);
        Toast.makeText(this, fbUserController.getCurrentUserUid(), Toast.LENGTH_SHORT).show();

        // Request UserLocation Permissions
        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissionsIfNecessary(permissionList);

        // Fetch user data from Firebase.
        fbUserController.getUserTask(fbUserController.getCurrentUserUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Process the result when the user data retrieval is successful.
                DocumentSnapshot document = task.getResult();
                if (!document.exists())
                    Log.w("error", "user document doesn't exist");
                else {
                    // Extract user information from the document.
                    myUser = document.toObject(User.class);
                    assert myUser != null;
                    // set geolocation switch to match user's preferences
                    if (myUser != null)
                        toggleGeolocation.setChecked(myUser.getIsGeolocationEnabled());
                }
            }
        });


        // implement geolocation switch behaviour
        toggleGeolocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
                if (isChecked) {
                    // check for permissions to enable geolocation on this device.
                    if ((ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        // permissions are not allowed
                        Log.d("permissions denied:","User did not allow permission to access their location");
                        // uncheck
                        toggleGeolocation.setChecked(false);
                        myUser.setGeolocationEnabled(false);
                        fbUserController.updateUser(myUser);
                        return;
                    } else {
                        // are permissions allowed
                        Log.d("permissions allowed:","User did allow permission to access their location");
                        myUser.setGeolocationEnabled(true);
                        fbUserController.updateUser(myUser);
                        requestHashedGeolocation();
                    }
                } else if (!isChecked) { // switching to false: allow every time.
                    myUser.setGeolocationEnabled(false);
                    fbUserController.updateUser(myUser);
                    SettingsDataSingleton.getInstance().setHashedGeoLocation(null);
                }
            }
        });

    }


    // Handles The Options Bar clicks
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Back button press
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /*
    * Credits: Android studio Wiki
    * Explains why we need user location permissions, and explains how to grant them.
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(R.string.grant_permission);

        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            openAppSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        builder.show();
    }

    /*
    * Opens app settings
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /*
     *   This function handles results from "requestPermissionsIfNecessary"
     *   We open a custom dialog box to explain why we need permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showSettingsDialog();
        }

    }

    /*
     *   This function uses ActivityCompat to request permissions from the user
     */
    private void requestPermissionsIfNecessary(ArrayList<String> permissions) {
        if (ContextCompat.checkSelfPermission(this, permissions.get(0))
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
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



}