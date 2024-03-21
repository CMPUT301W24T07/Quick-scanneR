package com.example.quickscanner.ui.adminpage;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrowseProfilesActivity extends AppCompatActivity {

    // ProfileList References
    ListView profileListView;
    ArrayList<User> profilesDataList;
    ArrayAdapter<User> profileAdapter;

    // FirebaseController Reference
    private FirebaseUserController fbUserController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_profiles);

        // Enable the back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // FirebaseUserController instance
        fbUserController = new FirebaseUserController();

        // Store view references
        profileListView = findViewById(R.id.BrowseProfilesListView);

        // Initialize the profile data list and ArrayAdapter
        profilesDataList = new ArrayList<User>();
        profileAdapter = new ProfileArrayAdapter(this, profilesDataList);
        // Set the adapter to the ListView
        profileListView.setAdapter(profileAdapter);

        fbUserController.getUsers().addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                if (task.isSuccessful()) {
                    profilesDataList.clear();  // removes current data
                    List<User> users = task.getResult();
                    if (users != null) {
                        profilesDataList.addAll(users); // adds new data from db
                    }
                } else {
                    Log.e("Firestore", task.getException().toString());
                }
                profileAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
