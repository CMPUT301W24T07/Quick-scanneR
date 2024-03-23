package com.example.quickscanner.ui.addevent;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;


import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.quickscanner.R;

import org.osmdroid.views.overlay.Marker;

import java.util.Objects;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;

/* Uses Open Street Maps to allow
*  User to select their event location
 *  Credits: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
 *           https://developer.android.com/training/permissions/requesting
 */
public class MapActivity extends AppCompatActivity {

    // References
    private MapView map = null;
    private IMapController mapController;
    private String hashedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("myPreferences", Context.MODE_PRIVATE));

        // create map reference and initialize map settings
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(18.0);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // Display Current Geolocation
        Intent intent = getIntent();
        String hashedLocation = intent.getStringExtra("geoHash");
        if ((hashedLocation != null) && !hashedLocation.isEmpty()){
            // If a geo location is passed to the activity, display it.
            createMarker(hashedLocation);
            centerFromHash(hashedLocation);
        } else {
            // Center at a default location
            displayDefault();
            centerFromHash(hashCoordinates(53.5282,-113.5257));
        }

        // Allow user to touch on the map to set their event location
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) { // click action
                    // Get the point where the map was touched
                    float x = event.getX();
                    float y = event.getY();

                    // Convert the screen position to geo coordinates
                    IGeoPoint touchedPoint = map.getProjection().fromPixels((int) x, (int) y);

                    // Use the touchedPoint as needed
                    handleTouchedPoint(touchedPoint);

                    return true; // Return true to indicate we've selected our location
                }
                return false;
            }
        });
    }

    private void handleTouchedPoint(IGeoPoint touchedPoint) {
        // Delete any prior touched points
        map.getOverlays().clear();
        map.invalidate(); // Refresh

        // This method handles the touched point
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(touchedPoint.getLatitude(), touchedPoint.getLongitude()));
        hashedLocation = hashCoordinates(touchedPoint.getLatitude(), touchedPoint.getLongitude()); // save our coordinates
        map.getOverlays().add(marker);
        map.invalidate(); // Refresh the map


        // Or return the location to another part of your app
        Log.d("Selected Location", "Lat: " + touchedPoint.getLatitude() + ", Lon: " + touchedPoint.getLongitude());
    }

    /* Turns a coordinate string (hash) into latitude/longitude and places
     * a corresponding marker on the map
     * @param hash: represents the latitude and longitude of a coordinate in String format.
     */
    public void createMarker(String hash){
        GeoPoint point = decodeHash(hash);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);
    }

    /* Turns a coordinate string (hash) into latitude/longitude and places
     * a corresponding marker on the map
     * And then centers the map on the marker
     * @param hash: represents the latitude and longitude of a coordinate in String format.
     */
    public void centerFromHash(String hash){
        // decode hash
        GeoPoint point = decodeHash(hash);
        // center at location
        mapController.setCenter(point);
    }


    /* Turns a coordinate string (hash) into latitude/longitude and places
     * @param hash: represents the latitude and longitude of a coordinate in String format.
     */
    public GeoPoint decodeHash(String hash) {
        GeoHash geoHash = GeoHash.fromGeohashString(hash);
        BoundingBox boundingBox = geoHash.getBoundingBox();
        // Calculate the center of the bounding box
        double latitude = (boundingBox.getNorthLatitude() + boundingBox.getSouthLatitude()) / 2.0;
        double longitude = (boundingBox.getEastLongitude() + boundingBox.getWestLongitude()) / 2.0;
        // return GeoPoint
        return new GeoPoint(latitude, longitude);
    }


    /* Turns a latitude and longitude into a geo String
     */
    public static String hashCoordinates(double latitude, double longitude) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 12); // int is precision
        return geoHash.toBase32();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(R.string.grant_permission);


        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            openAppSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void displayDefault() {
        GeoPoint point = new GeoPoint(53.5282, -113.5257);
        mapController.setCenter(point);
    }


    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }




    /*         Inflate Handle Top Menu Options        */
    // Create the Top Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }
    // Handles The Top Bar menu clicks
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Back button press
            // Return selected coordinates
            Intent returnIntent = new Intent();
            returnIntent.putExtra("geoHash", hashedLocation);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return false;

    }
}

