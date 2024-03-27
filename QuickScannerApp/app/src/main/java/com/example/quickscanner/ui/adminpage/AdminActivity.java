package com.example.quickscanner.ui.adminpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.databinding.FragmentScanBinding;

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
