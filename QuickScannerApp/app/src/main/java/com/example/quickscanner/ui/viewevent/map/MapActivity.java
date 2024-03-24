package com.example.quickscanner.ui.viewevent.map;

import static org.osmdroid.views.overlay.mylocation.IMyLocationProvider.*;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;

import com.example.quickscanner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Objects;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;

/* Uses Open Street Maps to display user's
 *  check-in geolocation
 *  Credits: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
 *           https://developer.android.com/training/permissions/requesting
 */
public class MapActivity extends AppCompatActivity {

    // References
    private MapView map = null;
    IMapController mapController;


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


        // display user location
        displayUserGeolocation();


        // If a geo location is passed to the activity, display it.
        Intent intent = getIntent();
        String hashedLocation = intent.getStringExtra("geoHash");
        if (hashedLocation != null && !hashedLocation.isEmpty()){
            displayEventGeolocation(hashedLocation);
        } else {
            // default location CCIS
            displayDefault();
        }


        // TODO: Still hardcoded coordinates so far.
        ArrayList<String> geoPoints = new ArrayList<>();
        geoPoints.add(hashCoordinates(53.5283, -113.52625));
        geoPoints.add(hashCoordinates(53.52833,-113.52623));
        geoPoints.add(hashCoordinates(53.52798,-113.52632));
        geoPoints.add(hashCoordinates(53.52812,-113.52685));
        geoPoints.add(hashCoordinates(53.52843,-113.52549));
        geoPoints.add(hashCoordinates(53.52740,-113.52524));
        geoPoints.add(hashCoordinates(53.52814,-113.52461));
        for (String geopoint : geoPoints) {
            createMarker(geopoint);
        }


    }



    public void displayUserGeolocation() {
        // check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // User did not allow permission to access their location
            Log.d("permissions","User did not allow permission to access their location");
            return;
        }
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location lastKnownLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLoc != null) {
            double longitude = (lastKnownLoc.getLongitude());
            double latitude = (lastKnownLoc.getLatitude());
            createMarker(hashCoordinates(latitude,longitude));
        }
    }

    public void displayEventGeolocation(String hash) {
        // start at Event Location
        GeoHash geoHash;
        try {
            geoHash = GeoHash.fromGeohashString(hash);
        } catch (Exception e){
            // invalid geo hash
            displayDefault();
            Log.d("GeoHash error", "Invalid Geo Hash");
            return;
        }
        BoundingBox boundingBox = geoHash.getBoundingBox();
        // Calculate the center of the bounding box
        double latitude = (boundingBox.getNorthLatitude() + boundingBox.getSouthLatitude()) / 2.0;
        double longitude = (boundingBox.getEastLongitude() + boundingBox.getWestLongitude()) / 2.0;
        // Mark Event location on the map
        GeoPoint eventLocation = new GeoPoint(latitude, longitude);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(eventLocation);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_noun_stadium_446594));
        map.getOverlays().add(startMarker);
        map.getController().setCenter(eventLocation); // center map at event location
    }

    /* Turns a coordinate string (hash) into latitude/longitude and places
     * a corresponding marker on the map
     * @param hash: represents the latitude and longitude of a coordinate in String format.
     */
    public void createMarker(String hash){
        GeoHash geoHash = GeoHash.fromGeohashString(hash);
        BoundingBox boundingBox = geoHash.getBoundingBox();
        // Calculate the center of the bounding box
        double latitude = (boundingBox.getNorthLatitude() + boundingBox.getSouthLatitude()) / 2.0;
        double longitude = (boundingBox.getEastLongitude() + boundingBox.getWestLongitude()) / 2.0;
        // Marker !!
        GeoPoint point = new GeoPoint(latitude, longitude);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);
    }

    /* Turns a latitude and longitude into a geo String
     */
    public static String hashCoordinates(double latitude, double longitude) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 12); // int is precision
        return geoHash.toBase32();
    }

    /*        Display default point at CCIS
     */
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
            finish();
            return true;
        }
        return false;

    }
}

