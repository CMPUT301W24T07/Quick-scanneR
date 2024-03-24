package com.example.quickscanner.ui.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickscanner.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.controller.QRScanner;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import ch.hsr.geohash.GeoHash;

public class ScannerFragment extends Fragment {

    private static final int REQUEST_CODE_GALLERY = 10;
    //private static final int RESULT_OK = 30;
    int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 100;


    private TextView textView;
    private QRScanner qrScanner;
    private String hashedUserLocation;

    private Button galleryButton;
    private Button scanButton;

    private FirebaseUserController firebaseUserController;
    private FirebaseController firebaseController;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        // Access the TextView
        textView = view.findViewById(R.id.scanner_text);
        scanButton = view.findViewById(R.id.scan_button);
        galleryButton = view.findViewById(R.id.gallery_button);

        qrScanner = new QRScanner(getContext());

        firebaseController = new FirebaseController();
        firebaseUserController = new FirebaseUserController();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        final String[] returnedText = new String[1];
        textView.setText("Ready to rock and roll");

        // Get the Hashed Location from MainActivity
        // Basically the location is pulled in Main Activity, instead of the fragment
        // So you only have to pull it once.
        hashedUserLocation = ((MainActivity) requireActivity()).MainActivityHashedUserLocation;
        Toast.makeText(getContext(), "Hash Geolocation" + hashedUserLocation, Toast.LENGTH_SHORT).show();


        //idk why it needs to make an intent array instead of a single intent object
        //but this is how it works so I'm not going to question it
        galleryButton.setOnClickListener(v -> {
            // Check if the READ_EXTERNAL_STORAGE permission is already granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("YEET","permission denied bench");
                // If not, request the permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            } else {
                openGallery();
            }
        });

        scanButton.setOnClickListener(v -> {
            // Scan the QR code
            qrScanner.scanQRCode(qrCodeValue ->  {
                // Update the TextView with the scanned QR code value
//                        returnedText[0] = qrCodeValue;
                // Extract the event ID from the scanned QR code
                String eventId = qrCodeValue;
                // Check if the QR code is valid by passing an instance of EventValidationCallback
                firebaseController.isValidEvent(eventId, new FirebaseController.EventValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid) {
                        if (isValid) {
                            // Perform check-in for the current user
                            checkInUser(eventId);
                        } else {
                            // Display an error message indicating that the QR code is not related to the event
                            Toast.makeText(getContext(), "Invalid QR code for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        });

    }

    private boolean isValidQRCode(String qrCodeValue) {
        // Perform validation logic here
        // For example, check if the QR code corresponds to an event in the database
        // Return true if valid, false otherwise
        return qrCodeValue != null && !qrCodeValue.isEmpty(); // Placeholder logic, replace with actual validation
    }

    // Method to perform check-in for the current user
    private void checkInUser(String eventId) {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Call the checkIn method from FirebaseController to add the user to the check-ins array for the event
        //FirebaseController firebaseController = new FirebaseController();
        firebaseController.checkIn(eventId, userId);

        // Inform the user that they have checked in successfully
        Toast.makeText(getContext(), "Checked in successfully!", Toast.LENGTH_SHORT).show();

        // Launch MyEventsActivity and pass the event ID
        // TODO: Replace with the actual activity to launch
        /*
        Intent intent = new Intent(getContext(), MyEventsActivity.class);
        intent.putExtra("eventID", eventId);
        startActivity(intent);
         */

        // Launch the event details activity
        launchEventDetails(getContext(), eventId);
    }

    private void launchEventDetails(Context context, String eventId) {
        // Create an Intent to start the event details activity
        Intent intent = new Intent(context, ViewEventActivity.class);

        // Put the event ID as an extra in the Intent
        intent.putExtra("eventID", eventId);

        // Start the activity with the Intent
        startActivity(intent);

        // Inform the user that they have checked in successfully
        firebaseController.checkIn(eventId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        Toast.makeText(getContext(), "Checked in successfully!", Toast.LENGTH_SHORT).show();

        // TODO: Make addToMyEvents method in FirebaseController
        // Add the event to the user's MyEvent list (assuming you have a method to do this in FirebaseController)
        //firebaseController.addToMyEvents(eventId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Inform the user that the event has been added to their MyEvent page
        //Toast.makeText(getContext(), "Event added to MyEvent page", Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result comes from the gallery Intent
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            // Get the Uri of the selected image
            Uri imageUri = data.getData();

            try {
                // Read QR code from the selected image
                String qrCodeValue = QRScanner.readQRCodeFromUri(requireContext(), imageUri);
                if (qrCodeValue != null && !qrCodeValue.isEmpty()) {
                    firebaseController.isValidEvent(qrCodeValue, new FirebaseController.EventValidationCallback() {
                        @Override
                        public void onValidationResult(boolean isValid) {
                            if (isValid) {
                                // Perform check-in for the current user
                                checkInUser(qrCodeValue);
                            } else {
                                // Display an error message indicating that the QR code is not related to the event
                                Toast.makeText(getContext(), "Invalid QR code for this app", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Handle the case when qrCodeValue is null or empty
                    Toast.makeText(getContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error reading QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }





}