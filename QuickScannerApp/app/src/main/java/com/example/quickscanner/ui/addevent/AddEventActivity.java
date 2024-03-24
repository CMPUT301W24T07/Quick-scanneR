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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.quickscanner.controller.FirebaseImageController;

import com.example.quickscanner.MainActivity;
import com.example.quickscanner.controller.FirebaseImageController;;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseQrCodeController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;

import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.gson.Gson;
import com.example.quickscanner.ui.profile.ProfileActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class AddEventActivity extends AppCompatActivity {
    /* Uses Open Street Maps to display user's
     *  check-in geolocation
     *  Credits: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
     *           https://developer.android.com/training/permissions/requesting
     */

    private TextView eventDescriptionTextView;
    private TextView eventNameEditText;
    private ImageView eventImageView;
    private String editedEventName;
    private String editedEventDescription;
    private String editedImagePath;
    private EditText locationEditText;
    private EditText geolocationEditText;
    private EditText timeEditText;

    private ArrayAdapter<Event> eventAdapter;
    private List<Event> eventDataList = new ArrayList<>();
    private ActivityResultLauncher<Intent> resultLauncher;
    private FirebaseEventController fbEventController;
    private FirebaseImageController fbImageController;
    private FirebaseUserController fbUserController;
    private FirebaseQrCodeController fbQrCodeController;


    private Bitmap eventImageMap;

    // Used to get an event location string back from the Map Activity Fragment.
    // Credit: https://developer.android.com/training/basics/intents/result
    ActivityResultLauncher<Intent> mapGetLocation = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String geoHash = data.getStringExtra("geoHash");
                        // Handle the location string
                        geolocationEditText.setText(geoHash);

                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        fbEventController = new FirebaseEventController();
        fbImageController = new FirebaseImageController();
        fbUserController = new FirebaseUserController();
        fbQrCodeController = new FirebaseQrCodeController();

        // Initialize views
        eventDescriptionTextView = findViewById(R.id.EventDescription);
        eventNameEditText = findViewById(R.id.EventName);
        eventImageView = findViewById(R.id.imageView);

        locationEditText = findViewById(R.id.location_textview);
        geolocationEditText = findViewById(R.id.geolocation_textview);
        timeEditText = findViewById(R.id.time_textview);

        // Initialize the event data list and ArrayAdapter
        eventDataList = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventDataList);

        // back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Location Text Click behaviour
        geolocationEditText = findViewById(R.id.geolocation_textview);
        geolocationEditText.setFocusable(false); // can't type, must interact with listener
        geolocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open map activity to choose your location
                // Credit: https://developer.android.com/training/basics/intents/result
                Intent intent = new Intent(AddEventActivity.this, MapActivity.class);
                // Pass the current location to the map activity
                Bundle bundle = new Bundle(1);
                bundle.putString("geoHash", geolocationEditText.getText().toString());
                intent.putExtras(bundle);
                // start map activity
                mapGetLocation.launch(intent);
            }
        });

        // Edit Button 1
        ImageButton editBtn1 = findViewById(R.id.editBtn1);
        editBtn1.setOnClickListener(v -> showTextDialog("Edit Event Name", (dialog, which) -> {
            editedEventName = ((EditText) ((AlertDialog) dialog).findViewById(R.id.input)).getText().toString();
            eventNameEditText.setText(editedEventName);
        }));

        // Edit Button 2
        ImageButton editBtn2 = findViewById(R.id.editBtn2);
        editBtn2.setOnClickListener(v -> showTextDialog("Edit Event Description", (dialog, which) -> {
            editedEventDescription = ((EditText) ((AlertDialog) dialog).findViewById(R.id.input)).getText().toString();
            eventDescriptionTextView.setText(editedEventDescription);
        }));

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
        createEventInsideBtn.setOnClickListener(v -> {
            // Check if necessary fields are filled
            if (editedEventName != null && !editedEventName.isEmpty() &&
                    editedEventDescription != null && !editedEventDescription.isEmpty()) {

                // Retrieve the text from the EditText fields
                String location = locationEditText.getText().toString();
                String geolocation = geolocationEditText.getText().toString();
                String time = timeEditText.getText().toString();

                // Create an Event object with the edited values
                Event newEvent = new Event(editedEventName, editedEventDescription, fbUserController.getCurrentUserUid(), time, location);
                newEvent.setGeoLocation(geolocation);

                // Add the event to the database
                addEventToFirestore(newEvent);

                /*
                // Add the event to the list and update the ArrayAdapter
                eventDataList.add(newEvent);
                eventAdapter.notifyDataSetChanged();

                // Update the QR code with the eventId and show the dialog
                QRCodeDialogFragment.newInstance(newEvent.getEventID()).show(getSupportFragmentManager(), "QRCodeDialogFragment");


                // finish the activity or perform other actions
                finish();
                */
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
                }


                //this should happen after qr code is added
//                // Pass the JSON string to the next activity
//                Intent intent = new Intent(AddEventActivity.this, ViewEventActivity.class);
//                intent.putExtra("eventJson", new Gson().toJson(event));
//                startActivity(intent);

            })
            .addOnFailureListener(e -> {
                Log.e("AddEventActivity", "Error adding event", e);
                // Handle the error appropriately
            });

        //promo qr code
        fbQrCodeController.addQrCode(fbUserController.getCurrentUserUid())
                .addOnSuccessListener(documentReference -> {
                    Log.d("testerrrr", "QR code added with ID: " + documentReference.getId());
                    // additional actions if needed
                    event.setPromoQrCode(documentReference.getId());
                    Log.d("testerrrr","event has set check in code: "+event.getPromoQrCode());


                    //show the event details page on this event

//                    // Pass the JSON string to the next activity
//                    Intent intent = new Intent(AddEventActivity.this, ViewEventActivity.class);
//                    Bundle bundle = new Bundle(1);
//                    // Pass the Event Identifier to the New Activity
//                    bundle.putString("eventID", event.getEventID());
//                    startActivity(intent);
//                    finish();

                    //commenting this out cos it is bugging out the app for now
                    // Pass the JSON string to the next activity
//                Intent intent = new Intent(AddEventActivity.this, ViewEventActivity.class);
//                intent.putExtra("eventJson", new Gson().toJson(event));
//                startActivity(intent);
//                 finish();

                })
                .addOnFailureListener(e -> {
                    Log.e("AddEventActivity", "Error adding QR code", e);
                    // Handle the error appropriately
                });

        //check in qr code
        fbQrCodeController.addQrCode(fbUserController.getCurrentUserUid())
                .addOnSuccessListener(documentReference -> {
                    Log.d("testerrrr", "QR code added with ID: " + documentReference.getId());
                    // additional actions if needed
                    event.setCheckInQrCode(documentReference.getId());

                    Log.d("testerrrr","event has set check in code: "+event.getCheckInQrCode());
                    Log.d("uteeee","event is is: "+event.getEventID());
                    addEventToUserOrganizedEvents(event.getEventID());
                    fbEventController.updateEvent(event);
                    finish();

                    //show the event details page on this event
                });

        //get current event id instance


    }

    private void addEventToUserOrganizedEvents(String eventId) {
        // Get the current user's ID
        String currentUserId = fbUserController.getCurrentUserUid();

        // Retrieve the current user's data from Firestore
        fbUserController.getUser(currentUserId)
                .addOnSuccessListener(user -> {
                    // Add the new event's ID to the user's organizedEvents list

                    user.getOrganizedEvents().add(eventId);

                    // Update the user's data in Firestore
                    fbUserController.updateUser(user)
                            .addOnSuccessListener(aVoid -> {
//                                Log.d("uteeee", eventId.toString());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("AddEventActivity", "Error updating user's organized events", e);
                            });
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
            .setPositiveButton("OK", (dialog, which) -> {
                if (title.equals("Edit Event Name")) {
                    editedEventName = input.getText().toString();
                    eventNameEditText.setText(editedEventName);
                } else if (title.equals("Edit Event Description")) {
                    editedEventDescription = input.getText().toString();
                    eventDescriptionTextView.setText(editedEventDescription);
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
                        // Set the selected image bitmap to the eventImageView
                        eventImageView.setImageBitmap(eventImageMap); // Update this line
                    } catch (Exception e) {
                        Toast.makeText(AddEventActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

}