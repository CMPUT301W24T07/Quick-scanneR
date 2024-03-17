package com.example.quickscanner.ui.settings;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.*;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem; // Import Menu class
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    // Model/Controllers ref
    FirebaseUserController fbUserController;
    User myUser;

    // UI ref
    Switch toggleGeolocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // references
        fbUserController = new FirebaseUserController();
        toggleGeolocation = findViewById(R.id.toggle_geolocation);

        // Fetch user data from Firebase.
        fbUserController.getUser(fbUserController.getCurrentUserUid()).addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                Log.w("error", "get user task is not successful");
            if (task.isSuccessful()) {
                // Process the result when the user data retrieval is successful.
                DocumentSnapshot document = task.getResult();
                if (!document.exists())
                    Log.w("error", "user document doesn't exist");
                else {
                    // Extract user information from the document.
                    myUser = document.toObject(User.class);
                    // set geolocation switch to match user's preferences
                    boolean oldAllowGeolocation = myUser.getGeolocationEnabled();
                    toggleGeolocation.setChecked(myUser.getGeolocationEnabled());
                    myUser.setGeolocationEnabled(oldAllowGeolocation);
                    fbUserController.updateUser(myUser);
                    Toast.makeText(this, valueOf(myUser.getUid()), LENGTH_SHORT).show();
                }
            }
        });


        // implement geolocation switch behaviour
        toggleGeolocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
                // toggle user's geolocation preferences
                myUser.toggleAllowsGeolocation();
                // update user's geolocation preferences in firebase
                fbUserController.updateUser(myUser);
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

}