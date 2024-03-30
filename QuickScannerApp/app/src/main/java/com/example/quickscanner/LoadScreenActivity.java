package com.example.quickscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.quickscanner.singletons.ConferenceConfigSingleton;

public class LoadScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        ConferenceConfigSingleton.initInstance();
        ConferenceConfigSingleton.getInstance().setOnConfigFetchedListener(new ConferenceConfigSingleton.OnConfigFetchedListener() {
            @Override
            public void onConfigFetched() {
                // Start MainActivity
                Intent intent = new Intent(LoadScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}