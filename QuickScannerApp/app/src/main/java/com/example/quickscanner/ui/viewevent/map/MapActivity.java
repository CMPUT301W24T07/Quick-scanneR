package com.example.quickscanner.ui.viewevent.map;

import static org.osmdroid.views.overlay.mylocation.IMyLocationProvider.*;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.checkerframework.checker.units.qual.A;
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
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.Objects;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;


public class MapActivity extends AppCompatActivity {
    /* Uses Open Street Maps to display user's
     *  check-in geolocation
     *  Credits: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
     *           https://developer.android.com/training/permissions/requesting
     */

    // Map References
    private MapView map = null;
    IMapController mapController;
    // Firestore references
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ListenerRegistration checkInListener;

    // Other References
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // instantiate intent bundles
        intent = getIntent();

        // instantiate firestore ref
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");

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


        // If the Event's geolocation is passed to the activity, display it.
        String hashedLocation = intent.getStringExtra("geoHash");
        if (hashedLocation != null && !hashedLocation.isEmpty()){
            displayEventGeolocation(hashedLocation);
        } else {
            // default location CCIS
            displayDefault();
        }

        // Display geolocation of Checked-In Users.
        String eventID = intent.getStringExtra("eventID"); // grab eventID passed into this activity
        assert(eventID != null);
        checkInListener = db.collection("Events").document(eventID).collection("checkIns")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot data, @Nullable FirebaseFirestoreException error) {
                        // handle query error
                        if (error != null) {
                            Log.w("MapActivity", "Listen for check-in users failed",error);
                        }
                        // pull geolocation data
                        for (QueryDocumentSnapshot checkInDoc : data) {
                            if (checkInDoc.get("geolocation") != null) {
                                // plot the geolocation
                                createMarker(checkInDoc.getString("geolocation"));
                            }
                        }
                    }
                });

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
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
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
            if (checkInListener != null)
                // delete listener
                checkInListener.remove();
            // pop this activity from the activity stack (managed by app)
            finish();
            return true;
        }
        return false;

    }
}

