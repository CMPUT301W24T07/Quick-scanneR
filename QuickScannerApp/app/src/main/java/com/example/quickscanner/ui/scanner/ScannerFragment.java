package com.example.quickscanner.ui.scanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.QRScanner;
import com.example.quickscanner.databinding.FragmentScanBinding;

public class ScannerFragment extends Fragment {

    private TextView textView;
    private QRScanner qrScanner;

    private String returnedText="";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        // Access the TextView
        textView = view.findViewById(R.id.scanner_text);

        qrScanner = new QRScanner(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView.setText("Ready to rock and roll");
        qrScanner.scanQRCode(new QRScanner.QRCodeScanCallback() {
            @Override
            public void onQRCodeScanned(String qrCodeValue) {
                // Update the TextView with the scanned QR code value
                returnedText = qrCodeValue;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        textView.setText(returnedText);

        // Start the QR code scanning process when the fragment becomes visible

    }
}