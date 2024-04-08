package com.example.quickscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.quickscanner.singletons.ConferenceConfigSingleton;

//javadocs
/**
 * This activity is the loading screen for the app.
 * It fetches the conference configuration from the database and then starts the MainActivity.
 */
public class LoadScreenActivity extends Activity {

    //javadocs
    /**
     * This method initializes the ConferenceConfigSingleton and sets the OnConfigFetchedListener.
     * Usage: call once.
     * @param savedInstanceState
     */
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