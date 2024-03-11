package com.example.quickscanner.ui.adminpage;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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

        // Create FireStore Listener for Updates to the Profiles List.
        fbUserController.getUsers().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                profilesDataList.clear();  // removes current data
                for (QueryDocumentSnapshot doc : task.getResult()) { // set of documents
                    User qryUser = doc.toObject(User.class);

                    //associate user ID with the retrieved user
                    qryUser.setUid(doc.getId());

                    profilesDataList.add((qryUser)); // adds new data from db
                }
            } else {
                Log.e("Firestore", task.getException().toString());
            }
            profileAdapter.notifyDataSetChanged();
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
