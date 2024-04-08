package com.example.quickscanner.ui.adminpage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {
    private String imageUrl;
    private FirebaseImageController fbImageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView imageView = findViewById(R.id.image_view);

        imageUrl = getIntent().getStringExtra("imageUrl");
        fbImageController = new FirebaseImageController();

        // Download and set the image to the ImageView
        fbImageController.downloadImage(imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String url = String.valueOf(task.getResult());
                Picasso.get().load(url).into(imageView);
            } else {
                Log.d("ImageDetailActivity", "Failed to download image");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}