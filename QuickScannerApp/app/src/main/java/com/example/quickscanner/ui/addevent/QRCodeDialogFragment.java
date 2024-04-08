// QRCodeDialogFragment.java
package com.example.quickscanner.ui.addevent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import com.example.quickscanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.OutputStream;

public class QRCodeDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final int REQUEST_WRITE_STORAGE = 112;

    //javadocs
    /**
     * This method creates a new instance of the QRCodeDialogFragment with the given event ID.
     * @param eventId The ID of the event to generate the QR code for.
     * @return A new instance of QRCodeDialogFragment.
     */
    public static QRCodeDialogFragment newInstance(String eventId) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    //javadocs
    /**
     * This method inflates the layout for the QRCodeDialogFragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root View of the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code_dialog, container, false);
    }


    //javadocs
    /**
     * This method sets up the views and listeners for the QRCodeDialogFragment.
     * @param view The root View of the inflated layout.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        // Retrieve the event ID from arguments
        String eventId = getArguments().getString(ARG_EVENT_ID);

        // Generate QR code based on the event ID
        generateQRCode(eventId, qrCodeImageView);

        // Set up the "X" button click listener
        view.findViewById(R.id.closeButton).setOnClickListener(v -> dismiss());

        // Set up the download button click listener
        Button downloadButton = view.findViewById(R.id.downloadbtn);
        downloadButton.setOnClickListener(v -> downloadQRCodeImage());
    }


    //javadocs
    /**
     * This method generates a QR code image based on the given event ID and sets it to the given ImageView.
     * @param eventId The ID of the event to generate the QR code for.
     * @param qrCodeImageView The ImageView to set the generated QR code image to.
     */
    private void generateQRCode(String eventId, ImageView qrCodeImageView) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("QRCodeDialogFragment", "Failed to generate QR code", e);
        }
    }

    //javadocs
    /**
     * This method requests permission to write to external storage and saves the QR code image to the device's gallery.
     */
    private void downloadQRCodeImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else {
            saveImageToGallery();
        }
    }

    //javadocs
    /**
     * This method saves the QR code image to the device's gallery.
     */
    private void saveImageToGallery() {
        ImageView qrCodeImageView = requireView().findViewById(R.id.qrCodeImageView);
        BitmapDrawable drawable = (BitmapDrawable) qrCodeImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Use MediaStore to insert the image into the device's gallery
        ContentResolver contentResolver = requireContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            contentValues.put(MediaStore.Images.Media.DATA, directory.getAbsolutePath());
        }

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (imageUri != null) {
            try {
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(requireContext(), "QR Code image saved to Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save QR Code image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Failed to save QR Code image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to save QR Code image", Toast.LENGTH_SHORT).show();
        }
    }


    //javadocs
    /**
     * This method handles the result of the permission request to write to external storage.
     * @param requestCode The request code passed to requestPermissions.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Unable to save QR Code image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
