package com.example.quickscanner.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem; // Import Menu class
import androidx.annotation.NonNull;
import android.os.Bundle;

import com.example.quickscanner.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    // Handles The Options Bar clicks
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Back button press
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}