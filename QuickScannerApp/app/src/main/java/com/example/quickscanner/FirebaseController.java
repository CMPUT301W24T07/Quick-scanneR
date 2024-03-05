package com.example.quickscanner;

import android.net.Uri;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseController {
    private FirebaseFirestore db;
    private FirebaseStorage idb;
    private CollectionReference usersRef;
    private CollectionReference imagesRef;
    private CollectionReference eventsRef;

    public FirebaseController() {
        db = FirebaseFirestore.getInstance();
        idb = FirebaseStorage.getInstance();
        usersRef = db.collection("users");
        imagesRef = db.collection("images");
        eventsRef = db.collection("events");
    }
    //used Tasks here as it allows us to put custom listeners on them, so we can
    //adjust what happens when the task is complete if it fails or not.
    //if we dont think we will need anything special i can change it from tasks.

    // User operations

    // Adds new user to Firestore
    public Task<DocumentReference> addUser(User user) {

        return usersRef.add(user);
    }

    // Updates existing user in Firestore
    public Task<Void> updateUser(String id, User user) {

        return usersRef.document(id).set(user);
    }

    // Deletes user from Firestore
    public Task<Void> deleteUser(String id) {
        return usersRef.document(id).delete();
    }

    // Retrieves all users from Firestore
    public Task<QuerySnapshot> getUsers() {
        return usersRef.get();
    }

    // Retrieves specific user from Firestore
    public Task<DocumentSnapshot> getUser(String id) {
        return usersRef.document(id).get();
    }



    // Event operations

    // Adds new event to Firestore
    public Task<DocumentReference> addEvent(Event event) {
        return eventsRef.add(event);
    }

    // Updates existing event in Firestore
    public Task<Void> updateEvent(String id, Event event) {
        return eventsRef.document(id).set(event);
    }

    // Deletes event from Firestore
    public Task<Void> deleteEvent(String id) {
        return eventsRef.document(id).delete();
    }

    // Retrieves all events from Firestore
    public Task<QuerySnapshot> getEvents() {
        return eventsRef.get();
    }

    // Retrieves specific event from Firestore
    public Task<DocumentSnapshot> getEvent(String id) {
        return eventsRef.document(id).get();
    }

    public void signUp(String eventId, String userId) {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("check ins and sign ups");
        attendanceRef.update("signed up", FieldValue.arrayUnion(userId));
    }

    public void checkIn(String eventId, String userId) {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("check ins and sign ups");
        attendanceRef.update("check ins", FieldValue.arrayUnion(userId));
    }
    // Image operations

    // Adds new image to Firestore
    public Task<DocumentReference> addImage(Image image) {
        return imagesRef.add(image);
    }

    // Updates existing image in Firestore
    public Task<Void> updateImage(String id, Image image) {
        return imagesRef.document(id).set(image);
    }

    // Deletes image from Firestore
    public Task<Void> deleteImage(String id) {
        return imagesRef.document(id).delete();
    }

    // Retrieves all images from Firestore
    public Task<QuerySnapshot> getImages() {
        return imagesRef.get();
    }

    // Retrieves specific image from Firestore
    public Task<DocumentSnapshot> getImage(String id) {
        return imagesRef.document(id).get();
    }

    //Image Firebase Storage operations

    // Uploads image to Firebase Storage
    public UploadTask uploadImage(String path, Uri imageUri) {
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.putFile(imageUri);
    }

    // Downloads image from Firebase Storage
    public Task<Uri> downloadImage(String path) {
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.getDownloadUrl();
    }

    // Deletes image from Firebase Storage
    public Task<Void> deleteImageStorage(String path) {
        StorageReference imageRef = idb.getReference().child(path);
        return imageRef.delete();
    }
}
