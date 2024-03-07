package com.example.quickscanner;

import android.net.Uri;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.Image;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class FirebaseController {
    private FirebaseFirestore db;
    private FirebaseStorage idb;
    private FirebaseAuth auth;
    private CollectionReference usersRef;
    private CollectionReference imagesRef;
    private CollectionReference eventsRef;

    public FirebaseController() {
        db = FirebaseFirestore.getInstance();
        idb = FirebaseStorage.getInstance();
         auth = FirebaseAuth.getInstance();
        usersRef = db.collection("users");
        imagesRef = db.collection("images");
        eventsRef = db.collection("events");
    }
    //sign in operation
    // Checks if it's the first sign in
    public boolean isFirstSignIn() {
        return auth.getCurrentUser() == null;
    }
    public String getCurrentUserUid() {
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    // Creates anonymous user
    public Task<AuthResult> createAnonymousUser() {
        return auth.signInAnonymously();
    }

    // User operations

    // Adds new user to Firestore
    public Task<Void> addUser(User user) {
        return usersRef.document(user.getUid()).set(user);
    }

    // Updates existing user in Firestore
    public Task<Void> updateUser(User user) {

        return usersRef.document(user.getUid()).set(user);
    }

    // Deletes user from Firestore
    public Task<Void> deleteUser(String userId) {
        return usersRef.document(userId).delete();
    }

    // Retrieves all users from Firestore
    public Task<QuerySnapshot> getUsers() {
        return usersRef.get();
    }

    // Retrieves specific user from Firestore
    public Task<DocumentSnapshot> getUser(String userId) {
        return usersRef.document(userId).get();
    }



    // Event operations

    // Adds new event to Firestore
    public Task<DocumentReference> addEvent(Event event) {
        return eventsRef.add(event);
    }

    // Updates existing event in Firestore
    // public Task<Void> updateEvent(Event event) {
    //    return eventsRef.document(event.getEventID()).set(event);
    //}

    // Deletes event from Firestore
    public Task<Void> deleteEvent(String eventId) {
        return eventsRef.document(eventId).delete();
    }

    // Retrieves all events from Firestore
    public Task<QuerySnapshot> getEvents() {
        return eventsRef.get();
    }

    // Retrieves specific event from Firestore
    public Task<DocumentSnapshot> getEvent(String eventId) {
        return eventsRef.document(eventId).get();
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
    public Task<Void> updateImage(String imageId, Image image) {
        return imagesRef.document(imageId).set(image);
    }

    // Deletes image from Firestore
    public Task<Void> deleteImage(String imageId) {
        return imagesRef.document(imageId).delete();
    }

    // Retrieves all images from Firestore
    public Task<QuerySnapshot> getImages() {
        return imagesRef.get();
    }

    // Retrieves specific image from Firestore
    public Task<DocumentSnapshot> getImage(String imageId) {
        return imagesRef.document(imageId).get();
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
