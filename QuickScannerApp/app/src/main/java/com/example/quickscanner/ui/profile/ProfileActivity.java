package com.example.quickscanner.ui.profile;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem; // Import Menu class
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.Profile;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.adminpage.BrowseProfilesActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    Button editButton;
    EditText nameEdit;
    EditText emailEdit;
    EditText linkedinEdit;
    ImageView profileImage;
    Bitmap profileBitMap;
    boolean editMode;

    User myUser;
    Profile myProfile;
    FirebaseUserController fbUserController;
    FirebaseImageController fbImageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fbUserController = new FirebaseUserController();
        fbImageController = new FirebaseImageController();

        String selectedProfileId = getIntent().getStringExtra("selectedProfileId");
        if (selectedProfileId == null) {
            selectedProfileId = fbUserController.getCurrentUserUid();
        }

        Log.w("error", selectedProfileId);
        Log.w("error", "weird p1");

        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        try {
                            profileBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileImage.setImageBitmap(profileBitMap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        fbUserController.getUser(selectedProfileId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    myUser = document.toObject(User.class);
                    myProfile = myUser.getUserProfile();

                    editButton = findViewById(R.id.edit_button);
                    nameEdit  = findViewById(R.id.nameEdit);
                    emailEdit = findViewById(R.id.emailEdit);
                    linkedinEdit  = findViewById(R.id.socialEdit);
                    profileImage = findViewById(R.id.profileImage);

                    nameEdit.setText(myProfile.getName());
                    emailEdit.setText(myProfile.getEmail());
                    linkedinEdit.setText(myProfile.getWebsite());
                    fbImageController.downloadImage(myProfile.getImageUrl()).addOnCompleteListener(task1 -> {
                        String url = String.valueOf(task1.getResult());
                        Picasso.get().load(url).into(profileImage);
                    });

                    profileImage.setOnClickListener(v -> {
                        if (editMode) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                            galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(galleryIntent);
                        }
                    });

                    boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
                    if (isAdmin) {
                        editButton.setText("Delete");
                        editButton.setOnClickListener(v -> {
                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setTitle("Delete Profile")
                                    .setMessage("Are you sure you want to delete this profile?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        fbUserController.deleteUser(myUser.getUid())
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(ProfileActivity.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ProfileActivity.this, BrowseProfilesActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to delete profile", Toast.LENGTH_SHORT).show());
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        });
                    } else {
                        editButton.setOnClickListener(v -> {
                            if (!editMode) {
                                nameEdit.setInputType(TYPE_CLASS_TEXT);
                                emailEdit.setInputType(TYPE_CLASS_TEXT);
                                linkedinEdit.setInputType(TYPE_CLASS_TEXT);
                                editMode = !editMode;
                                editButton.setText("Save");
                            } else {
                                myProfile.setName(String.valueOf(nameEdit.getText()));
                                myProfile.setEmail(String.valueOf(emailEdit.getText()));
                                myProfile.setWebsite(String.valueOf(linkedinEdit.getText()));
                                if (profileBitMap != null) {
                                    myProfile.setImageUrl(myUser.getUid());
                                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                                    profileBitMap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                                    byte[] imageData = boas.toByteArray();
                                    fbImageController.uploadImage(myUser.getUid(), imageData);
                                }

                                myUser.setUserProfile(myProfile);
                                fbUserController.updateUser(myUser);

                                nameEdit.setInputType(TYPE_NULL);
                                emailEdit.setInputType(TYPE_NULL);
                                linkedinEdit.setInputType(TYPE_NULL);
                                editMode = !editMode;
                                editButton.setText("Edit");
                            }
                        });
                    }
                } else {
                    Log.w("error", "not working:( exists");
                }
            } else {
                Log.w("error", "not working:( isSuccessful");
            }
        }).addOnFailureListener(e -> Log.w("error", e)).addOnCanceledListener(() -> Log.w("error", ":33"));

        editMode = false;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}