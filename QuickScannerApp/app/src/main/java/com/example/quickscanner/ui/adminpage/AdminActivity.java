package com.example.quickscanner.ui.adminpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.databinding.ActivityAdminBinding;
import com.example.quickscanner.databinding.FragmentScanBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    private FragmentScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button browseEventsButton = findViewById(R.id.BrowseEventsButton);
        Button browseProfilesButton = findViewById(R.id.BrowseProfilesButton);
        Button browseImagesButton = findViewById(R.id.BrowseImagesButton);

        Button adminQRButton = findViewById(R.id.AdminQRButton);

        // back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        browseEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
        });

        browseProfilesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, BrowseProfilesActivity.class);
            startActivity(intent);
        });

        browseImagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, BrowseImagesActivity.class);
            startActivity(intent);
        });

        //show a dialog with QR image encoding the admin auth code
        adminQRButton.setOnClickListener(v -> {

            launchAdminQRDialog();

        });



    }

    private void launchAdminQRDialog() {

        //get admin auth code from firebase
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("config").document("Admin Auth Code");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String adminAuthCode = task.getResult().getString("Code");
                Log.d("codeeee", "admin code is: "+adminAuthCode);
                generateQRCode(adminAuthCode);
            }
        });


    }
        public void generateQRCode(String code) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                // Increase the size of the QR code
                int qrCodeSize = 500;
                BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                // Create a dialog with an ImageView
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                ImageView imageView = new ImageView(this);
                Drawable drawable = new BitmapDrawable(getResources(), bmp);
                imageView.setImageDrawable(drawable);

                // Set the ImageView to be square and larger
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                builder.setView(imageView);
                AlertDialog dialog = builder.create();

                // Set the dialog to be square and larger
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                }

                dialog.show();
            }
        catch (WriterException e) {
            e.printStackTrace();
        }

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

}
