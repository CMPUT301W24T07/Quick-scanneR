package com.example.quickscanner.controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.concurrent.atomic.AtomicReference;

//javadoc comments

/**
 * This class is used to scan QR codes using the google code scanner for android
 * Pass an instance of context when making the object
 * and call the scanQRCode method to start the scanning process
 * The method returns the raw value of the QR code
 */
public class QRScanner {

    private Context context;

    //nonfunctional for now. will handle this later.
    public void scanQRCodeFromGallery(Uri uri, QRCodeScanCallback qrCodeScanCallback) {
        //decode the QR code from the gallery using ZXing
        //return the value of the QR code
        String decoded = "";

        qrCodeScanCallback.onQRCodeScanned(decoded);

    }

    public interface QRCodeScanCallback {
        void onQRCodeScanned(String qrCodeValue);
    }

    public QRScanner(Context context) {
        this.context = context;
    }

    //configures all the options for the code scanner
    GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build();


    public void scanQRCode(QRCodeScanCallback qrCodeScanCallback){
        AtomicReference<String> barcodeValue = new AtomicReference<>("yeet");
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(context, options);
        scanner.startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully
//                            barcodeValue.set(barcode.getRawValue());
                            Log.d("TESTERRRR",barcodeValue.get());
                            qrCodeScanCallback.onQRCodeScanned(barcode.getRawValue());
                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled
                            qrCodeScanCallback.onQRCodeScanned("Task Canceled");

                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            //produce dialog box to say that the task failed
                            qrCodeScanCallback.onQRCodeScanned("Task Failed");
                        });
    }
}
