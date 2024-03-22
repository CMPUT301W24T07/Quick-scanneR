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
}