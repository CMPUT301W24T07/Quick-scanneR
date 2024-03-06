package com.example.quickscanner.ui.addevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.databinding.FragmentScanBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class AddEventActivity extends AppCompatActivity {

    private FragmentScanBinding binding;
    private TextView eventDescriptionTextView;
    private TextView eventNameEditText;
    private ImageView eventImageView;
    private String editedEventName;
    private String editedEventDescription;
    private String editedImagePath;
    private ArrayAdapter<Event> eventAdapter;
    private List<Event> eventDataList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference eventsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);

        // Initialize views
        eventDescriptionTextView = findViewById(R.id.EventDescription);
        eventNameEditText = findViewById(R.id.EventName);
        eventImageView = findViewById(R.id.imageView);

        // Initialize the event data list and ArrayAdapter
        eventDataList = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventDataList);

        // Firebase references
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");

        // back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Edit Button 1
        ImageButton editBtn1 = findViewById(R.id.editBtn1);
        editBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextDialog("Edit Event Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editedEventName = ((EditText) ((AlertDialog) dialog).findViewById(R.id.input)).getText().toString();
                        eventNameEditText.setText(editedEventName);
                    }
                });
            }
        });

        // Edit Button 2
        ImageButton editBtn2 = findViewById(R.id.editBtn2);
        editBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextDialog("Edit Event Description", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editedEventDescription = ((EditText) ((AlertDialog) dialog).findViewById(R.id.input)).getText().toString();
                        eventDescriptionTextView.setText(editedEventDescription);
                    }
                });
            }
        });

        // Edit Image Button
        ImageButton editImageButton = findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        // Create Event Button
        Button createEventInsideBtn = findViewById(R.id.CreateEventInsideBtn);
        User testUser = new User();
        createEventInsideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if necessary fields are filled
                if (editedEventName != null && !editedEventName.isEmpty() &&
                        editedEventDescription != null && !editedEventDescription.isEmpty()) {

                    // Create an Event object with the edited values
                    Event newEvent = new Event(editedEventName, editedEventDescription, editedImagePath, testUser);

                    // Add the event to the database
                    addEventToFirestore(newEvent);

                    // Add the event to the list and update the ArrayAdapter
                    eventDataList.add(newEvent);
                    eventAdapter.notifyDataSetChanged();

                    // Pass the new event data back to the calling fragment
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newEvent", newEvent);
                    setResult(Activity.RESULT_OK, resultIntent);

                    // finish the activity or perform other actions
                    finish();
                }
            }
        });
    }

    private void addEventToFirestore(Event event) {
        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddEventActivity", "Event added with ID: " + documentReference.getId());
                    // additional actions if needed
                })
                .addOnFailureListener(e -> {
                    Log.e("AddEventActivity", "Error adding event", e);
                    // Handle the error appropriately
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

    private void showTextDialog(String title, DialogInterface.OnClickListener positiveClickListener) {
        final EditText input = new EditText(this);
        input.setId(R.id.input); // Set the ID for the EditText
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.equals("Edit Event Name")) {
                            editedEventName = input.getText().toString();
                            eventNameEditText.setText(editedEventName);
                        } else if (title.equals("Edit Event Description")) {
                            editedEventDescription = input.getText().toString();
                            eventDescriptionTextView.setText(editedEventDescription);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickImage() {
        ActivityResultLauncher<String> pickMedia =
                registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        editedImagePath = uri.toString();
                        Log.d("PhotoPicker", "Selected URI: " + editedImagePath);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Launch the photo picker
        pickMedia.launch("image/*");
    }


}