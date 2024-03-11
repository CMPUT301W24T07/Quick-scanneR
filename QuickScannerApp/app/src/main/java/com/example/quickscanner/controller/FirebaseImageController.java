package com.example.quickscanner.controller;

import android.net.Uri;
import android.util.Log;

import com.example.quickscanner.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseImageController
{
    private final FirebaseFirestore db;
    private final FirebaseStorage idb;
    private final CollectionReference imagesRef;
    private final StorageReference imageStorage;

    public FirebaseImageController()
    {
        db = FirebaseFirestore.getInstance();
        idb = FirebaseStorage.getInstance();
        imageStorage = idb.getReference();
        imagesRef = db.collection("Images");

    }
    /**
     * Adds new image to Firestore.
     *
     * @param image the image to be added
     * @return a Task that will be completed once the image is added
     */
    public Task<DocumentReference> addImage(Image image)
    {
        return imagesRef.add(image);
    }

    /**
     * Updates existing image in Firestore.
     *
     * @param imageId the ID of the image to be updated
     * @param image   the new image data
     * @return a Task that will be completed once the image is updated
     */
    public Task<Void> updateImage(String imageId, Image image)
    {
        return imagesRef.document(imageId).set(image);
    }

    /**
     * Deletes image from Firestore.
     *
     * @param imageId the ID of the image to be deleted
     * @return a Task that will be completed once the image is deleted
     */
    public Task<Void> deleteImage(String imageId)
    {
        return imagesRef.document(imageId).delete();
    }

    /**
     * Retrieves all images from Firestore.
     *
     * @return a Task that will be completed once the image list is fetched
     */
    public Task<QuerySnapshot> getImages()
    {
        return imagesRef.get();
    }

    /**
     * Retrieves specific image from Firestore.
     *
     * @param imageId the ID of the image to retrieve
     * @return a Task that will be completed once the image is fetched
     */
    public Task<DocumentSnapshot> getImage(String imageId)
    {
        return imagesRef.document(imageId).get();
    }

// Image Firebase Storage operations

    /**
     * Uploads image to Firebase Storage.
     *
     * @param path      the path where the image will be stored
     * @param imageData the byte data of the image to upload
     * @return an UploadTask that can be used to monitor the upload
     */
    public UploadTask uploadImage(String path, byte[] imageData) {
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.putBytes(imageData);
    }

    /**
     * Downloads image from Firebase Storage.
     *
     * @param path the path of the image to download
     * @return a Task that will be completed with the download Uri
     */
    public Task<Uri> downloadImage(String path)
    {
        Log.d("plshalp", "downloadImage: " + path);
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.getDownloadUrl();
    }

    /**
     * Deletes image from Firebase Storage.
     *
     * @param path the path of the image to delete
     * @return a Task that will be completed once the image is deleted
     */
    public Task<Void> deleteImageStorage(String path)
    {
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.delete();
    }

}
