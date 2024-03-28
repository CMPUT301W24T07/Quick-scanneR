package com.example.quickscanner.ui.adminpage;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.homepage_event.EventArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseEventsActivity extends AppCompatActivity {

    // EventList References
    ListView eventListView;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;

    // Firestore References
    private FirebaseEventController fbEventController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Enable the back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Firebase references
        fbEventController = new FirebaseEventController();

        // Store view references
        eventListView = findViewById(R.id.BrowseEventsListView);

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(this, eventsDataList);
        // Set the adapter to the ListView
        eventListView.setAdapter(eventAdapter);

        // Create FireStore Listener for Updates to the Events List.
        fbEventController.getEvents().addOnCompleteListener(events ->
        {
            if (events.isSuccessful())
            {
                // Clear the current list
                eventsDataList.clear();
                eventsDataList.addAll(events.getResult());
                // Notify the adapter that the data has changed
                eventAdapter.notifyDataSetChanged();
            }
            else
            {
                Log.d("BrowseEventsActivity", "Error getting documents: ", events.getException());
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
