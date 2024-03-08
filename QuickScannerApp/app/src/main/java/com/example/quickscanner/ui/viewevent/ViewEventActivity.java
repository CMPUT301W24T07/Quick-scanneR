package com.example.quickscanner.ui.viewevent;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MenuItem; // Import Menu class

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.widget.TextView;


import com.example.quickscanner.FirebaseController;
import com.example.quickscanner.R;
import com.example.quickscanner.databinding.ActivityVieweventBinding;
import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity {
    String eventID;
    private FirebaseController firebaseController;

    private Event event;

    private ActivityVieweventBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVieweventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseController = new FirebaseController();

        // Display Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Grab any Intent bundle/parameters
        Bundle inputBundle = getIntent().getExtras();
        if (inputBundle != null) {
            eventID = inputBundle.getString("eventID");
            Log.d("Beans", eventID);
        }
        if (eventID == null) {
            Log.e("ViewEventActivity", "Error: Event ID is null");
            finish();
        }
        fetchEventData();

    }

    // Fetches the event data from Firestore
    private void fetchEventData() {
        firebaseController.getEvent(eventID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                event = documentSnapshot.toObject(Event.class);
                Log.d("BEANS", "DocumentSnapshot data: " + documentSnapshot.getData());
                if (event != null) {
                    // Set the event data to the UI
                    setEventDataToUI();
                }
            }

            private void setEventDataToUI() {

                //use event object to update all the views
                binding.eventTitleText.setText(event.getName());
                binding.eventDescriptionText.setText(event.getDescription());
                binding.locationText.setText(event.getLocation());
                binding.organiserText.setText(event.getOrganizer().getUserProfile().getName());
                binding.eventTimeText.setText(event.getTime());

                //just generate the qr code from the event id and set it to the qr code image view
                //and get the image from the firebase storage and set it to the image view


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ViewEventActivity", "Error fetching event data: " + e.getMessage());
            }
        });
    }


    // Handles The Top Bar menu clicks
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Back button press
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}