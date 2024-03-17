// QRCodeDialogFragment.java
package com.example.quickscanner.ui.addevent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quickscanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_ID = "event_id";

    public static QRCodeDialogFragment newInstance(String eventId) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code_dialog, container, false);
    }


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
    }

    private void generateQRCode(String eventId, ImageView qrCodeImageView) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            String content = eventId;
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
