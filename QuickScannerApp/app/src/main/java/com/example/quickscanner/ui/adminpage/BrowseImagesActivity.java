package com.example.quickscanner.ui.adminpage;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

//javadocs
/**
 * This Activity is responsible for browsing all images in the database.
 */
public class BrowseImagesActivity extends AppCompatActivity {

    // ImageList References
    ListView imageListView;
    ArrayList<Image> imagesDataList;

    //had to change to ImageArrayAdapter from ArrayAdapter<Image> as it does not implement
    //check box functionality.
    ImageArrayAdapter imageAdapter;

    // FirebaseController References
    private FirebaseImageController fbImageController;

    //javadocs
    /**
     * This method is called when the activity is created.
     * @param savedInstanceState
     */
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
        updateDeleteButtonVisibility();
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

        // Find the delete button
        FloatingActionButton deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> new AlertDialog.Builder(BrowseImagesActivity.this)
                .setTitle("Delete Images")
                .setMessage("Are you sure you want to delete these Image(s)?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSelectedImages())
                .setNegativeButton("Cancel", null)
                .show());
    }

    //javadocs
    /**
     * This method deletes the selected images from the database.
     */
    private void deleteSelectedImages() {
        for (Image image : imagesDataList) {
            if (image.isSelected()) {
                String imageUrl = image.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    fbImageController.deleteImageStorage(imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(BrowseImagesActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                                imagesDataList.remove(image);
                                imageAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Toast.makeText(BrowseImagesActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("BrowseImagesActivity", "Image URL is null or empty");
                }
            }
        }
    }

    //javadocs
    /**
     * This method updates the visibility of the delete button based on the selected images.
     */

    public void updateDeleteButtonVisibility() {
        FloatingActionButton deleteButton = findViewById(R.id.delete_button);
        if (imageAdapter.isAnyImageSelected()) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    //javadocs
    /**
     * This method inflates the top menu bar.
     * @param menu The menu to inflate.
     * @return True if the menu is inflated successfully, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}