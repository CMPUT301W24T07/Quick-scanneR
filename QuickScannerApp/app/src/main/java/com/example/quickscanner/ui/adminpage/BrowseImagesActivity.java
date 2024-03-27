package com.example.quickscanner.ui.adminpage;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.model.Image;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseImagesActivity extends AppCompatActivity {

    // ImageList References
    ListView imageListView;
    ArrayList<Image> imagesDataList;
    ArrayAdapter<Image> imageAdapter;

    // FirebaseController Reference
    private FirebaseImageController fbImageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_images);

        // Enable the back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // FirebaseImageController instance
        fbImageController = new FirebaseImageController();

        // Store view references
        imageListView = findViewById(R.id.BrowseImagesListView);

        // Initialize the image data list and ArrayAdapter
        imagesDataList = new ArrayList<>();
        imageAdapter = new ImageArrayAdapter(this, imagesDataList);
        // Set the adapter to the ListView
        imageListView.setAdapter(imageAdapter);

        // Create FireStore Listener for Updates to the Images List.
        fbImageController.getImages().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imagesDataList.clear();  // removes current data
                for (QueryDocumentSnapshot doc : task.getResult()) { // set of documents
                    Image qryImage = doc.toObject(Image.class);
                    imagesDataList.add(qryImage); // adds new data from db
                }
            } else {
                Log.e("Firestore", task.getException().toString());
            }
            imageAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}