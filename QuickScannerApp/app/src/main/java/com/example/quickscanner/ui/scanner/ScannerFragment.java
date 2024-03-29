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
import com.example.quickscanner.singletons.SettingsDataSingleton;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseQrCodeController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.controller.QRScanner;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.io.IOException;
import java.util.Objects;

/*basically:
    if user scans sign up/registration/rsvp code, register them for event
    and show them the event details
    if user scans check-in code, check them in and show them the event details
    if user scans invalid code, show them an error message
    if user scans admin code, make them an admin
* */

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
    private FirebaseAttendanceController fbAttendanceController;
    private FirebaseUserController fbUserController;
    private FirebaseQrCodeController fbQrCodeController;
    private FirebaseEventController fbEventController;

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

        fbAttendanceController = new FirebaseAttendanceController();
        fbUserController = new FirebaseUserController();
        fbQrCodeController = new FirebaseQrCodeController();
        fbEventController = new FirebaseEventController();
        firebaseUserController = new FirebaseUserController();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        final String[] returnedText = new String[1];
        textView.setText("Ready to rock and roll");

        // Example Usage: Get the Hashed Location from the Singleton
        /*         - Note: This usage returns NULL if device permissions are disabled
        *                  If permissions are enabled, returns hashed geolocation as a String
        */
        hashedUserLocation = SettingsDataSingleton.getInstance().getHashedGeoLocation();


        //idk why it needs to make an intent array instead of a single intent object
        //but this is how it works so I'm not going to question it
        galleryButton.setOnClickListener(v -> {
            // Check if the READ_EXTERNAL_STORAGE permission is already granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("YEET", "permission denied bench");
                // If not, request the permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            } else {
                openGallery();
            }
        });

        scanButton.setOnClickListener(v ->
        {
            // Scan the QR code
            qrScanner.scanQRCode(qrCodeValue ->
            {
                String usedId = fbUserController.getCurrentUserUid();
                //get the event the qr is used for check ins in

                fbQrCodeController.getCheckInEventFromQr(qrCodeValue).addOnCompleteListener(task ->
                {
                    //if it doesnt error
                    if (task.isSuccessful()) {
                        Event event = task.getResult();

                        //check code was found to correspond to an event
                        if (event != null) {
                            Log.d("testerrr","Check-in code recognised, event not null");
                            //if it is, try checking in with it.
                            textView.setText(qrCodeValue);
                            tryCheckIn(usedId, event);
                        }
                        else {
                            //if it wasnt found, check if it was a promo code
                            fbQrCodeController.getPromoEventFromQr(qrCodeValue).addOnCompleteListener(promotask ->
                            {
                                if (promotask.isSuccessful()) {
                                    Event promoEvent = promotask.getResult();

                                    //if it was found, do whatever gets it to event details.
                                    if (promoEvent != null) {
                                        //do whatever gets it to event details.
                                        launchEventDetails(getContext(), promoEvent.getEventID());
                                        Log.d("testerrr","event name: "+promoEvent.getName());
                                    } else {
                                        Log.d("testerrr","event is null");
                                        //if it wasnt found, tell the user that the qr code is invalid

//                                        todo: here we would check if the code is an admin code

                                        Toast.makeText(getContext(),
                                                "EEEEENIUSSSSSSS",
                                                Toast.LENGTH_LONG).show();

                                        textView.setText(qrCodeValue);
                                    }
                                }
                            });
                        }
                    }
                });
            });
        });

    }

    private Void tryCheckIn(String attendee, Event event) {
        if (Objects.equals(attendee, event.getOrganizerID())) {
            Log.e("testerrr", "You are the organizer of this event");
            Toast.makeText(getContext(), "You are the organizer of this event", Toast.LENGTH_LONG).show();
            return null;
        }

        fbAttendanceController.checkIn(attendee, event.getEventID()).addOnSuccessListener(checkedIn ->
        {
            Log.d("testerrr", "Checked in successfully!");
            Toast.makeText(getContext(), "Checked in successfully!", Toast.LENGTH_LONG).show();

            launchEventDetails(getContext(), event.getEventID());


        }).addOnFailureListener(e ->
        {
            Toast.makeText(getContext(), "Failed to check in", Toast.LENGTH_LONG).show();
                Log.e("testerrr", "Failed to check in: " + e.getMessage());
        });
        return null;
    }

    private void launchEventDetails(Context context, String eventId) {
        // Create an Intent to start the event details activity
        Intent intent = new Intent(context, ViewEventActivity.class);

        // Put the event ID as an extra in the Intent
        intent.putExtra("eventID", eventId);

        // Start the activity with the Intent
        startActivity(intent);

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
}