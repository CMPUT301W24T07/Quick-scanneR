package com.example.quickscanner.ui.addevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.controller.FirebaseImageController;;
import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class AddEventActivity extends AppCompatActivity {
    private TextView eventDescriptionTextView;
    private TextView eventNameEditText;
    private ImageView eventImageView;
    private String editedEventName;
    private String editedEventDescription;
    private String editedImagePath;
    private EditText locationEditText;
    private EditText timeEditText;

    private ArrayAdapter<Event> eventAdapter;
    private List<Event> eventDataList = new ArrayList<>();
    private ActivityResultLauncher<Intent> resultLauncher;
    private FirebaseEventController fbEventController;
    private FirebaseImageController fbImageController;


    private Bitmap eventImageMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        fbEventController = new FirebaseEventController();
        fbImageController = new FirebaseImageController();

        // Initialize views
        eventDescriptionTextView = findViewById(R.id.EventDescription);
        eventNameEditText = findViewById(R.id.EventName);
        eventImageView = findViewById(R.id.imageView);

        locationEditText = findViewById(R.id.location_textview);
        timeEditText = findViewById(R.id.time_textview);

        // Initialize the event data list and ArrayAdapter
        eventDataList = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventDataList);



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

        // Image Button
        ImageButton editImageButton = findViewById(R.id.editImageButton);
        registerResult();
        editImageButton.setOnClickListener(view -> pickImage());

//        // QR code generation
//        ImageButton generateQRbtn = findViewById(R.id.generateQRbtn);
//        generateQRbtn.setOnClickListener(v -> {
//            QRCodeDialogFragment.newInstance(null).show(getSupportFragmentManager(), "QRCodeDialogFragment");
//        });

        // Create Event Button
        Button createEventInsideBtn = findViewById(R.id.CreateEventInsideBtn);
        User testUser = new User();
        createEventInsideBtn.setOnClickListener(v -> {
            // Check if necessary fields are filled
            if (editedEventName != null && !editedEventName.isEmpty() &&
                    editedEventDescription != null && !editedEventDescription.isEmpty()) {

                // Retrieve the text from the EditText fields
                String location = locationEditText.getText().toString();
                String time = timeEditText.getText().toString();

                // Create an Event object with the edited values
                Event newEvent = new Event(editedEventName, editedEventDescription, testUser, time, location);

                // Add the event to the database
                addEventToFirestore(newEvent);

                // Add the event to the list and update the ArrayAdapter
                eventDataList.add(newEvent);
                eventAdapter.notifyDataSetChanged();

                // Assign the eventId here
                String eventId = newEvent.getEventID();

                // Update the QR code with the eventId and show the dialog
                QRCodeDialogFragment.newInstance(eventId).show(getSupportFragmentManager(), "QRCodeDialogFragment");


                // Pass the new event data back to the calling fragment
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newEvent", newEvent);
                setResult(Activity.RESULT_OK, resultIntent);

                // finish the activity or perform other actions
                finish();
            }
        });
    }

    // Add event to Firestore
    private void addEventToFirestore(Event event) {
        fbEventController.addEvent(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddEventActivity", "Event added with ID: " + documentReference.getId());
                    // additional actions if needed
                    event.setEventID(documentReference.getId());

                    if (eventImageMap != null) {
                        event.setImagePath(event.getEventID() + "primary");
                        ByteArrayOutputStream boas = new ByteArrayOutputStream();
                        eventImageMap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                        byte[] imageData = boas.toByteArray();
                        fbImageController.uploadImage(event.getImagePath(), imageData);
                        fbEventController.updateEvent(event);
                    }
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

    // To pick an image from the gallery
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    // Register the result of the image picker
    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        try {
                            eventImageMap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            eventImageView.setImageBitmap(eventImageMap);
                        } catch (Exception e) {
                            Toast.makeText(AddEventActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}