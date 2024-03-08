package com.example.quickscanner.ui.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.QRScanner;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;

public class ScannerFragment extends Fragment {

    private static final int REQUEST_CODE_GALLERY = 10;
    private static final int RESULT_OK = 30;
    int REQUEST_CODE_STORAGE_PERMISSION = 1;

    private TextView textView;
    private QRScanner qrScanner;


    private Button galleryButton;
    private Button scanButton;

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        final String[] returnedText = new String[1];
        textView.setText("Ready to rock and roll");
        //idk why it needs to make an intent array instead of a single intent object
        //but this is how it works so I'm not going to question it
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scan the QR code
                qrScanner.scanQRCode(new QRScanner.QRCodeScanCallback() {
                    @Override
                    public void onQRCodeScanned(String qrCodeValue) {
                        // Update the TextView with the scanned QR code value
//                        returnedText[0] = qrCodeValue;
                        launchEventDetails(getContext(),qrCodeValue);
                    }
                });
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the READ_EXTERNAL_STORAGE permission is already granted
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("YEET","permission denied bench");
                    // If not, request the permission
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
                } else {
                    openGallery();
                }
            }
        });

    }

    private void launchEventDetails(Context context, String returnedText) {
        Log.d("plshalp","launched event details launcher");
        String eventId = returnedText;

        // Create an Intent to start the event details activity
        Intent intent = new Intent(context, ViewEventActivity.class);

        // Put the event ID as an extra in the Intent
        intent.putExtra("eventID", eventId);

        Log.d("plshalp", intent.toString());

        // Start the activity with the Intent
        context.startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Check if the result comes from the gallery Intent
//        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//            // Get the Uri of the selected image
//            Uri selectedImageUri = data.getData();
//
//            // TODO: Handle the selected image (e.g., display it in an ImageView)
//            // Scan the QR code
//            qrScanner.scanQRCodeFromGallery(selectedImageUri, new QRScanner.QRCodeScanCallback() {
//                @Override
//                public void onQRCodeScanned(String qrCodeValue) {
//                    // Update the TextView with the scanned QR code value
//                    returnedText = qrCodeValue;
//                    textView.setText(returnedText);
//                }
//            });
//        }
//    }

//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        textView.setText(returnedText);
//
//        // Start the QR code scanning process when the fragment becomes visible
//
//    }
}