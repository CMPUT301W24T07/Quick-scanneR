package com.example.quickscanner.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

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
                    toggleGeolocation.setChecked(myUser.getGeolocationEnabled());
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
                    }
                } else if (!isChecked) { // switching to false: allow every time.
                    myUser.setGeolocationEnabled(false);
                    fbUserController.updateUser(myUser);
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
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showSettingsDialog();
        }

    }

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

}