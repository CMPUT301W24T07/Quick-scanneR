package com.example.quickscanner.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.io.InputStream;
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

    public QRScanner(Context context) {
        this.context = context;
    }

    public static String readQRCodeFromUri(Context context, Uri imageUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // Convert the bitmap to a binary bitmap
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels)));

        // Use ZXing to decode the QR code
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            Result result = multiFormatReader.decode(binaryBitmap);
            return result.getText(); // Return the decoded text (QR code value)
        } catch (NotFoundException e) {
            Log.d("QRScanner", "QR code not found",e);
            return null; // Return null if QR code is not found
        }
    }

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
