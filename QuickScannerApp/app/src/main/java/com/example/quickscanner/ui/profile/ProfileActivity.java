package com.example.quickscanner.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.view.MenuItem; // Import Menu class
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.quickscanner.R;
import com.example.quickscanner.databinding.FragmentScanBinding;
import com.example.quickscanner.ui.profile.ProfileViewModel;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FragmentScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // backbutton
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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