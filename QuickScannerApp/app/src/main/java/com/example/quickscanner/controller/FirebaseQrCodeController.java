package com.example.quickscanner.controller;

import androidx.annotation.NonNull;

import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Controller for managing QR codes in Firebase Firestore.
 */
public class FirebaseQrCodeController {
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;

    /**
     * Constructor for FirebaseQrCodeController.
     * Initializes Firestore and the users collection reference.
     */
    public FirebaseQrCodeController() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
    }
    /**
     * Validates the provided ID.
     * @param id The ID to validate.
     * @throws IllegalArgumentException if the ID is null or empty.
     */
    private void validateId(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("ID cannot be null or empty in Qrcode controller");
        }
    }

    /**
     * Adds a new QR code to the Firestore for a specific user.
     * @param userId The ID of the user.
     * @return A Task that represents the operation of adding the QR code.
     */
    public Task<DocumentReference> addQrCode(String userId) {
        validateId(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", FieldValue.serverTimestamp()); // Add a timestamp
        return usersRef.document(userId).collection("QrCodes").add(data);
    }

    /**
     * Deletes a specific QR code from the Firestore for a specific user.
     * @param userId The ID of the user.
     * @param qrCodeId The ID of the QR code to delete.
     * @return A Task that represents the operation of deleting the QR code.
     */
    public Task<Void> deleteQrCode(String userId, String qrCodeId) {
        validateId(userId);
        validateId(qrCodeId);
        DocumentReference qrCodeRef = usersRef.document(userId).collection("QrCodes").document(qrCodeId);
        return qrCodeRef.delete();
    }

    /**
     * Retrieves all QR codes for a specific user from the Firestore, ordered by timestamp in descending order.
     *
     * @param userId The ID of the user.
     * @return A Task that represents the operation of retrieving the QR codes.
     * The result of the Task is a list of QR code IDs.
     */
    public Task<List<String>> getUserQrCodes(String userId) {
        validateId(userId);
        CollectionReference qrCodesRef = usersRef.document(userId).collection("QrCodes");
        // Orders qr code strings by timestamp in descending order
        Query query = qrCodesRef.orderBy("timestamp", Query.Direction.DESCENDING);
        return query.get().continueWith(new Continuation<QuerySnapshot, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    List<String> qrCodeIds = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        //adds each Qr codes id to the list
                        qrCodeIds.add(document.getId());
                    }
                    return qrCodeIds;
                } else {
                    throw task.getException();
                }
            }
        });
    }
    /**
     * Retrieves an event from the Firestore that has QR code id input
     * in the checkInQrCode field. throws exception if not found
     *
     * @param qrCode The QR code string.
     * @return A Task that represents the operation of retrieving the event.
     * The result of the Task is an Event object.
     */
    public Task<Event> getCheckInEventFromQr(String qrCode) {
        validateId(qrCode);
        CollectionReference eventsRef = db.collection("events");
        Query query = eventsRef.whereEqualTo("checkInQrCode", qrCode).limit(1);
        return query.get().continueWith(new Continuation<QuerySnapshot, Event>() {
            @Override
            public Event then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        return document.toObject(Event.class);
                    }
                    return null; // No event found with the provided QR code
                } else {
                    throw task.getException();
                }
            }
        });
    }
    /**
     * Retrieves an event from the Firestore that has the provided QR code string in the "promoQrCode" field.
     *
     * @param qrCode The QR code string.
     * @return A Task that represents the operation of retrieving the event.
     * The result of the Task is an Event object.
     */
    public Task<Event> getPromoEventFromQr(String qrCode) {
        validateId(qrCode);
        CollectionReference eventsRef = db.collection("events");
        Query query = eventsRef.whereEqualTo("promoQrCode", qrCode).limit(1);
        return query.get().continueWith(new Continuation<QuerySnapshot, Event>() {
            @Override
            public Event then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        return document.toObject(Event.class);
                    }
                    return null; // No event found with the provided QR code
                } else {
                    throw task.getException();
                }
            }
        });
    }
    /**
     * Checks if there is an event in the Firestore that the provided QR code string is
     * used either in check ins or promotion page.
     *
     * @param qrCode The QR code string.
     * @return A Task that represents the operation of checking the existence of the event.
     * The result of the Task is a boolean value indicating whether its used in any event.
     */
    public Task<Boolean> isQrUnused(String qrCode) {
        validateId(qrCode);
        CollectionReference eventsRef = db.collection("events");
        // Create a query for the "checkInQrCode" field
        Query queryCheckIn = eventsRef.whereEqualTo("checkInQrCode", qrCode).limit(1);
        // Create a query for the "promoQrCode" field
        Query queryPromo = eventsRef.whereEqualTo("promoQrCode", qrCode).limit(1);

        // Execute the queries
        Task<QuerySnapshot> taskCheckIn = queryCheckIn.get();
        Task<QuerySnapshot> taskPromo = queryPromo.get();

        // Combine the tasks and transform the result into a boolean value
        Task<Boolean> combinedTask = Tasks.whenAllSuccess(taskCheckIn, taskPromo).continueWith(task -> {
            // Get the results of the queries
            QuerySnapshot checkInResult = taskCheckIn.getResult();
            QuerySnapshot promoResult = taskPromo.getResult();

            // Return true if both of the query results are empty (i.e., there is no event with the provided QR code), false otherwise
            return checkInResult.isEmpty() && promoResult.isEmpty();
        });

        return combinedTask;
    }

}