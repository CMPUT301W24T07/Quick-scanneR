package com.example.quickscanner.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.quickscanner.R;
import com.example.quickscanner.databinding.FragmentScanBinding;
import com.example.quickscanner.ui.profile.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private FragmentScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    }
}