package com.example.quickscanner;

import android.net.Uri;
import android.util.Log;

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

/**
 * This class handles all Firebase operations for the app.
 * It's a controller for Firestore and Firebase Storage (for our images).
 * it also handles our Auth for log ins.
 */
public class FirebaseController
{
    private FirebaseFirestore db;
    private FirebaseStorage idb;
    private FirebaseAuth auth;
    private CollectionReference usersRef;
    private CollectionReference imagesRef;
    private CollectionReference eventsRef;
    private StorageReference imageStorage;

    /**
     * Initializes connections to Firestore, Firebase Storage, and FirebaseAuth.
     * Also sets up references to the "users", "images", and "events" collections in Firestore.
     */
    public FirebaseController()
    {
        db = FirebaseFirestore.getInstance();
        idb = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        usersRef = db.collection("users");
        imageStorage = idb.getReference();
        imagesRef = db.collection("Images");
        eventsRef = db.collection("Events");
    }

    /**
     * Checks if the user is signing in for the first time by checking if the current user from FirebaseAuth is null.
     *
     * @return true if it's the first sign in, false otherwise
     */
    public boolean isFirstSignIn()
    {
        return auth.getCurrentUser() == null;
    }

    /**
     * Gets the unique ID (UID) of the current user from FirebaseAuth.
     *
     * @return the UID of the current user
     */
    public String getCurrentUserUid()
    {
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    /**
     * Creates an anonymous user in FirebaseAuth, allowing users to use the app without creating an account.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @return a Task that represents the asynchronous sign in operation
     */
    public Task<AuthResult> createAnonymousUser()
    {
        return auth.signInAnonymously();
    }

    // User operations

    /**
     * Adds a new user to Firestore. This operation is asynchronous and returns a Task.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @param user the user to be added
     * @return a Task that will be completed once the user is added
     */
    public Task<Void> addUser(User user)
    {
        return usersRef.document(user.getUid()).set(user);
    }

    /**
     * Updates an existing user in Firestore. it is asynchronous and returns a Task.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @param user the user with updated information
     * @return a Task that will do things once the user is updated
     */
    public Task<Void> updateUser(User user)
    {
        return usersRef.document(user.getUid()).set(user);
    }

    // User operations

    /**
     * Deletes a user from Firestore.
     *
     * @param userId the ID of the user to be deleted
     * @return a Task that will be completed once the user is deleted
     */
    public Task<Void> deleteUser(String userId)
    {
        return usersRef.document(userId).delete();
    }

    /**
     * Retrieves all users from Firestore.
     *
     * @return a Task that will be completed after list is fetched
     */
    public Task<QuerySnapshot> getUsers()
    {
        return usersRef.get();
    }

    /**
     * Retrieves specific user from Firestore.
     *
     * @param userId the ID of the user to retrieve
     * @return a Task that will be completed once user is fetched
     */
    public Task<DocumentSnapshot> getUser(String userId)
    {
        return usersRef.document(userId).get();
    }

// Event operations

    /**
     * Adds new event to Firestore.
     *
     * @param event the event to be added
     * @return a Task that will be once the event is added
     */
    public Task<DocumentReference> addEvent(Event event)
    {
        return eventsRef.add(event);
    }


    /**
     * Updates existing event in Firestore.
     * NOTE: this does not update checked in or signed up users.
     *  you must use signUp and checkIn methods for that.
     * use signUp and checkIn methods for that.
     *
     * @param event the event to be updated
     * @return a Task that will be completed once the event is updated
     */
    public Task<Void> updateEvent(Event event)
    {
        return eventsRef.document(event.getEventID()).set(event);
    }

    /**
     * Deletes event from Firestore.
     *
     * @param eventId the ID of the event to be deleted
     * @return a Task that will be completed once the event is deleted
     */
    public Task<Void> deleteEvent(String eventId)
    {
        return eventsRef.document(eventId).delete();
    }

    /**
     * Retrieves all events from Firestore.
     *
     * @return a Task that will be completed once the event list is fetched
     */
    public Task<QuerySnapshot> getEvents()
    {
        return eventsRef.get();
    }

    /**
     * Retrieves specific event from Firestore.
     *
     * @param eventId the ID of the event to retrieve
     * @return a Task that will be completed once the event is fetched
     */
    public Task<DocumentSnapshot> getEvent(String eventId)
    {
        return eventsRef.document(eventId).get();

    }


    // Attendance operations

    /**
     * Adds a user to the sign-ups array for an event.
     * NOTE: this does not update the event object. use updateEvent for that.
     *
     * @param eventId the ID of the event
     * @param userId  the ID of the user
     */
    public void signUp(String eventId, String userId)
    {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("attendance");
        attendanceRef.update("signUps", FieldValue.arrayUnion(userId));
    }

    /**
     * Removes a user from the sign-ups array for an event.
     * NOTE: this does not update the event object. use updateEvent for that.
     *
     * @param eventId the ID of the event
     * @param userId  the ID of the user
     */
    public void cancelSignUp(String eventId, String userId)
    {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("attendance");
        attendanceRef.update("signUps", FieldValue.arrayRemove(userId));
    }

    /**
     * Adds a user to the check-ins array for an event.
     * NOTE: this does not update the event object. use updateEvent for that.
     *
     * @param eventId the ID of the event
     * @param userId  the ID of the user
     */
    public void checkIn(String eventId, String userId)
    {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("attendance");
        attendanceRef.update("checkIns", FieldValue.arrayUnion(userId));
    }

    /**
     * Removes a user from the check-ins array for an event.
     * NOTE: this does not update the event object. use updateEvent for that.
     *
     * @param eventId the ID of the event
     * @param userId  the ID of the user
     */
    public void cancelCheckIn(String eventId, String userId)
    {
        DocumentReference attendanceRef = db.collection("events").document(eventId).collection("Attendance").document("attendance");
        attendanceRef.update("checkIns", FieldValue.arrayRemove(userId));
    }

    /**
     * Gets the attendance data for an event.
     * once attendance is grabbed, you can use .get("signUps") to get the list of signups
     * or .get("checkIns") to get the list of checkins
     *
     * @param eventId the ID of the event
     * @return a Task that will be completed with the result of the operation
     */
    public Task<DocumentSnapshot> getAttendance(String eventId)
    {
        return db.collection("events").document(eventId).collection("Attendance").document("attendance").get();
    }




// Image operations

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
