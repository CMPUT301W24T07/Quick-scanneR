package com.example.quickscanner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is a controller used for handling attendance operations in Firestore.
 */
public class FirebaseAttendanceController
{
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;
    private final CollectionReference usersRef;

    public FirebaseAttendanceController()
    {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");
        usersRef = db.collection("Users");
    }

    /**
     * validates the id of the user or event, throwing a exception if the id is null or empty.
     *
     * @param id
     */
    private void validateId(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("ID cannot be null or empty in attendance controller");
        }
    }

    /**
     * Signs up a user for an event, ensuring that the current attendees does not exceed the max.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return A Task that completes when the transaction is finished.
     */
    public Task<Void> signUp(final String userId, final String eventId) {
        validateId(userId);
        validateId(eventId);
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference userSignUpsRef = userRef.collection("Attendance").document("signedUpEvents");

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                DocumentSnapshot signUpSnapshot = transaction.get(signUpRef);

                Event event = eventSnapshot.toObject(Event.class);
                boolean isSignedUp = signUpSnapshot.exists();

                if (!isSignedUp) {
                    if (event.getMaxSpots() != null && event.getTakenSpots() >= event.getMaxSpots()) {
                        throw new FirebaseFirestoreException(
                                "Event is full",
                                FirebaseFirestoreException.Code.ABORTED
                        );
                    }

                    // Increment the takenSpots field
                    transaction.update(eventRef, "takenSpots", FieldValue.increment(1));
                    event.setTakenSpots(event.getTakenSpots() + 1);

                    // Add a document to the signUps subcollection
                    transaction.set(signUpRef, new HashMap<>());

                    // Add the event to the user's signed-up events
                    transaction.update(userSignUpsRef, "eventIds", FieldValue.arrayUnion(eventId));
                }

                return null;
            }
        });
    }


    /**
     * Checks a user into an event. This method performs the following steps in a transaction:
     * 1. Reads the Event and signedUp documents.
     * 2. Checks if the user is signed up and if the event is full.
     * 3. If the user is not signed up and the event is not full, increments the
     * takenSpots field of the Event document and adds a document to the
     * checkIns subcollection.
     * <p>
     * If any step fails, the transaction is aborted and no changes are made to the database.
     *
     * @param userId  The ID of the user to check in.
     * @param eventId The ID of the event to check the user into.
     * @return A Task that will be completed when the transaction is finished.
     */
    public Task<Void> checkIn(final String userId, final String eventId) {
        validateId(userId);
        validateId(eventId);
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference checkInRef = eventRef.collection("checkIns").document(userId);
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference userCheckInsRef = userRef.collection("Attendance").document("checkedInEvents");
        final DocumentReference userSignUpsRef = userRef.collection("Attendance").document("signedUpEvents");
        final DocumentReference liveCountRef = eventRef.collection("liveCounts").document("currentAttendance");


        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                DocumentSnapshot signUpSnapshot = transaction.get(signUpRef);
                DocumentSnapshot checkInSnapshot = transaction.get(checkInRef);

                Event event = eventSnapshot.toObject(Event.class);
                boolean isSignedUp = signUpSnapshot.exists();
                boolean isCheckedIn = checkInSnapshot.exists();

                if (!isSignedUp && !isCheckedIn) {
                    if (event.getMaxSpots() != null && event.getTakenSpots() >= event.getMaxSpots()) {
                        throw new FirebaseFirestoreException(
                                "Event is full",
                                FirebaseFirestoreException.Code.ABORTED
                        );
                    }

                    // Increment the takenSpots field
                    transaction.update(eventRef, "takenSpots", FieldValue.increment(1));
                    event.setTakenSpots(event.getTakenSpots() + 1);
                    // Increment the current attendance field in liveCounts
                    transaction.update(liveCountRef, "currentAttendance", FieldValue.increment(1));
                    // Add a document to the signUps and checkIns subcollections
                    transaction.set(signUpRef, new HashMap<>());

                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("timesCheckedIn", 1);
                    transaction.set(checkInRef, checkInData);

                    // Add the event to the user's signed-up and checked-in events
                    transaction.update(userSignUpsRef, "eventIds", FieldValue.arrayUnion(eventId));
                    transaction.update(userCheckInsRef, "eventIds", FieldValue.arrayUnion(eventId));
                }
                else if (isSignedUp && !isCheckedIn) {
                    // Add a document to the checkIns subcollection
                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("timesCheckedIn", 1);
                    transaction.set(checkInRef, checkInData);
                    transaction.update(liveCountRef, "currentAttendance", FieldValue.increment(1));

                    // Add the event to the user's checked-in events
                    transaction.update(userCheckInsRef, "eventIds", FieldValue.arrayUnion(eventId));
                }
                else if (isCheckedIn) {
                    transaction.update(checkInRef, "timesCheckedIn", FieldValue.increment(1));
                }

                return null;
            }
        });
    }


    /**
     * Removes a user from the check-ins of an event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return A Task that completes when the user is removed from the check-ins.
     */
    public Task<Void> removeFromCheckIn(final String userId, final String eventId)
    {
        validateId(userId);
        validateId(eventId);
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference checkInRef = eventRef.collection("checkIns").document(userId);
        final DocumentReference userRef = usersRef.document(userId);
        final DocumentReference userAttendanceRef = userRef.collection("Attendance")
                .document("checkedInEvents");


        return db.runTransaction(new Transaction.Function<Void>()
        {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException
            {
                DocumentSnapshot checkInSnapshot = transaction.get(checkInRef);

                if (checkInSnapshot.exists())
                {
                    // Decrement the currentAttendees event doc
                    transaction.update(eventRef, "currentAttendees", FieldValue.increment(-1));

                    // Delete the check-in document
                    transaction.delete(checkInRef);

                    // Remove the event from the user's checked-in events
                    transaction.update(userAttendanceRef, "eventIds", FieldValue.arrayRemove(eventId));
                }

                return null;
            }
        });
    }
    public Task<Void> removeFromSignUp(final String userId, final String eventId) {
        validateId(userId);
        validateId(eventId);
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference userSignUpsRef = userRef.collection("Attendance").document("signedUpEvents");

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot signUpSnapshot = transaction.get(signUpRef);

                boolean isSignedUp = signUpSnapshot.exists();

                if (isSignedUp) {
                    // Decrement the takenSpots field
                    transaction.update(eventRef, "takenSpots", FieldValue.increment(-1));

                    // Remove the document from the signUps subcollection
                    transaction.delete(signUpRef);

                    // Remove the event from the user's signed-up events
                    transaction.update(userSignUpsRef, "eventIds", FieldValue.arrayRemove(eventId));
                }

                return null;
            }
        });
    }
}

