package com.example.quickscanner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is a controller used for handling attendance operations in Firestore.
 * It provides methods for checking users into events, removing users from check-ins,
 * and validating user and event IDs.
 * it also provides methods for fetching the events a user has signed up for and/or checked into.
 * and fetching the users who have signed up for and/or checked into an event.
 * It interacts with the 'Events' and 'Users' collections in Firestore.
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
     * Validates the ID of the user or event, throwing an IllegalArgumentException if the ID is null or empty.
     *
     * @param id The ID to validate. It should be a non-null and non-empty string.
     * @throws IllegalArgumentException If the ID is null or empty.
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
     * This method performs the following operations in a transaction:
     * 1. Checks if the user is already signed up for the event. If so, no further action is taken.
     * 2. If the user is not signed up and the event is not full, it increments the count of taken spots in the event document.
     * 3. Adds a document to the signUps subcollection for the event, indicating that the user is signed up.
     * 4. Adds the event ID to the user's list of signed-up events.
     *
     * If the event is full (i.e., the number of taken spots is equal to or greater than the maximum spots),
     * a FirebaseFirestoreException with the code ABORTED is thrown.
     *
     * @param userId  The ID of the user to be signed up for the event.
     * @param eventId The ID of the event for which the user is to be signed up.
     * @return A Task that completes when the transaction is finished. The Task's result will be null.
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
     * Checks a user into an event.
     *
     * This method performs the following operations in a transaction:
     * 1. If the user is not signed up, they will be signed up and checked in.
     * 2. If the user is signed up but not checked in, they will be checked in.
     * 3. If the user is already checked in, the timesCheckedIn field will be incremented.
     *
     * This method ensures all these operations are performed atomically, so there's no chance of
     * write errors or too many people joining.
     *
     * If the event is full
     * (i.e., the number of taken spots is equal to or greater than the maximum spots),
     * the transaction is aborted and an exception is thrown.
     *
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return A task that resolves when the check-in operation is complete. The Task's result will be null.
     */
    public Task<Void> checkIn(final String userId, final String eventId) {
        // Validate the user and event IDs
        validateId(userId);
        validateId(eventId);

        // References to various documents in the database
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference checkInRef = eventRef.collection("checkIns").document(userId);
        final DocumentReference liveCountRef = eventRef.collection("liveCounts").document("currentAttendance");

        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference userCheckInsRef = userRef.collection("Attendance").document("checkedInEvents");
        final DocumentReference userSignUpsRef = userRef.collection("Attendance").document("signedUpEvents");


        // Run a transaction to perform the check-in operation
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // Fetch the current state of the event, sign-up, and check-in documents
                DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                DocumentSnapshot signUpSnapshot = transaction.get(signUpRef);
                DocumentSnapshot checkInSnapshot = transaction.get(checkInRef);

                // Convert the event document to an Event object
                Event event = eventSnapshot.toObject(Event.class);

                // Check if the user is signed up and checked in
                boolean isSignedUp = signUpSnapshot.exists();
                boolean isCheckedIn = checkInSnapshot.exists();

                // If the user is neither signed up nor checked in
                if (!isSignedUp && !isCheckedIn) {
                    // If the event is full, abort the transaction
                    if (event.getMaxSpots() != null && event.getTakenSpots() >= event.getMaxSpots()) {
                        throw new FirebaseFirestoreException(
                                "Event is full",
                                FirebaseFirestoreException.Code.ABORTED
                        );
                    }

                    // Increment the number of taken spots for the event
                    transaction.update(eventRef, "takenSpots", FieldValue.increment(1));
                    event.setTakenSpots(event.getTakenSpots() + 1);

                    // Increment the current attendance count
                    transaction.update(liveCountRef, "attendanceCount", FieldValue.increment(1));

                    // Add a document to the sign-ups and check-ins collections
                    transaction.set(signUpRef, new HashMap<>());
                    //makes a hashmap to store the times checked in or other check in data
                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("timesCheckedIn", 1);
                    transaction.set(checkInRef, checkInData);

                    // Add the event to the user's list of signed-up and checked-in events
                    transaction.update(userCheckInsRef, "eventIds", FieldValue.arrayUnion(eventId));
                }

                // If the user is signed up but not checked in
                else if (isSignedUp && !isCheckedIn) {
                    // Add a document to the check-ins collection
                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("timesCheckedIn", 1);
                    transaction.set(checkInRef, checkInData);

                    // Increment the current attendance count
                    transaction.update(liveCountRef, "attendanceCount", FieldValue.increment(1));
                    // Remove the document from the sign-ups collection
                    transaction.delete(signUpRef);

                    // Remove the event from the user's list of signed-up events
                    transaction.update(userSignUpsRef, "eventIds", FieldValue.arrayRemove(eventId));


                    // Add the event to the user's list of checked-in events
                    transaction.update(userCheckInsRef, "eventIds", FieldValue.arrayUnion(eventId));
                }
                // If the user is already checked in
                else if (isCheckedIn) {
                    // Increment the number of times the user has checked in
                    transaction.update(checkInRef, "timesCheckedIn", FieldValue.increment(1));
                }

                return null;
            }
        });
    }


    /**
     * Removes a user from the check-ins of an event.
     *
     * This method performs the following operations in a transaction:
     * 1. Decrements the count of current attendees in the event document.
     * 2. Decrements the count of taken spots in the event document.
     * 3. Deletes the check-in document for the user in the event's check-ins collection.
     * 4. Removes the event ID from the user's list of checked-in events.
     *
     * If the user is not checked into the event, the method does nothing
     *
     * @param userId  The ID of the user to be removed from the check-ins.
     * @param eventId The ID of the event from which the user is to be removed.
     * @return A Task that completes when the transaction is finished. The Task's result will be null.
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
                    // Decrement the currentAttendees event doc and current attendance count
                    transaction.update(eventRef, "attendanceCount", FieldValue.increment(-1));
                    transaction.update(eventRef, "takenSpots", FieldValue.increment(-1));

                    // Delete the check-in document
                    transaction.delete(checkInRef);

                    // Remove the event from the user's checked-in events
                    transaction.update(userAttendanceRef, "eventIds", FieldValue.arrayRemove(eventId));
                }

                return null;
            }
        });
    }
    /**
     * Removes a user from the sign-ups of an event.
     *
     * This method performs the following operations in a transaction:
     * 1. Decrements the count of taken spots in the event document.
     * 2. Deletes the sign-up document for the user in the event's sign-ups collection.
     * 3. Removes the event ID from the user's list of signed-up events.
     *
     * If the user is not signed up for the event, the method does nothing.
     *
     * @param userId  The ID of the user to be removed from the sign-ups.
     * @param eventId The ID of the event from which the user is to be removed.
     * @return A Task that completes when the transaction is finished. The Task's result will be null.
     */

    public Task<Void> removeFromSignUp(final String userId, final String eventId) {
        validateId(userId);
        validateId(eventId);
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference userSignUpsRef = userRef.collection("Attendance")
                .document("signedUpEvents");

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
    /**
     * Fetches the events a user has signed up for.
     *
     * This method performs the following operations:
     * 1. Fetches the user's signed-up events document.
     * 2. Retrieves the list of event IDs from the document.
     * 3. Converts the event IDs to a list of tasks that fetch the corresponding DocumentSnapshots.
     * 4. Waits for all tasks to complete.
     * 5. Converts the DocumentSnapshots to Event objects.
     *
     * If the user has not signed up for any events, the method returns a task that resolves to an empty list.
     *
     * @param userId The ID of the user.
     * @return A task that resolves to a list of Event objects.
     */
    public Task<List<Event>> getUserSignedUpEvents(String userId) {
        validateId(userId);
        // Reference to the user's signed up events document
        DocumentReference userSignUpsRef = db.collection("users")
                .document(userId)
                .collection("Attendance")
                .document("signedUpEvents");

        // Fetch the document and continue with the task
        return userSignUpsRef.get().continueWithTask(task -> {
            DocumentSnapshot document = task.getResult();

            // Get the list of event IDs from the document
            List<String> eventIds = (List<String>) document.get("eventIds");
            if (eventIds == null) {
                // If there are no event IDs, return an empty list
                return Tasks.forResult(new ArrayList<Object>());
            }

            // Convert the event IDs to a list of DocumentSnapshot tasks
            List<Task<DocumentSnapshot>> tasks = arrayToDocList(eventIds);

            // Wait for all tasks to complete
            return Tasks.whenAllSuccess(tasks);
        }).continueWith(task -> {
            // Convert the DocumentSnapshots to Event objects
            return convertToObject(task.getResult(), Event.class);
        });
    }

    /**
     * Fetches the events a user has checked into.
     *
     * This method performs the following operations:
     * 1. Fetches the user's checked-in events document.
     * 2. Retrieves the list of event IDs from the document.
     * 3. Converts the event IDs to a list of tasks that fetch the corresponding DocumentSnapshots.
     * 4. Waits for all tasks to complete.
     * 5. Converts the DocumentSnapshots to Event objects.
     *
     * If the user has not checked into any events, the method returns a task that resolves to an empty list.
     *
     * @param userId The ID of the user.
     * @return A task that resolves to a list of Event objects.
     */
    public Task<List<Event>> getUserCheckedInEvents(String userId) {
        validateId(userId);
        // Reference to the user's checked in events document
        DocumentReference userCheckInsRef = db.collection("users")
                .document(userId)
                .collection("Attendance")
                .document("checkedInEvents");

        // Fetch the document and continue with the task
        return userCheckInsRef.get().continueWithTask(task -> {
            DocumentSnapshot document = task.getResult();
            // Get the list of event IDs from the document
            List<String> eventIds = (List<String>) document.get("eventIds");
            if (eventIds == null) {
                // If there are no event IDs, return an empty list
                return Tasks.forResult(new ArrayList<Object>());
            }
            // Convert the event IDs to a list of DocumentSnapshot tasks
            List<Task<DocumentSnapshot>> tasks = arrayToDocList(eventIds);

            // Wait for all tasks to complete
            return Tasks.whenAllSuccess(tasks);
        }).continueWith(task -> {
            // Convert the DocumentSnapshots to Event objects
            return convertToObject(task.getResult(), Event.class);
        });
    }

    /**
     * Fetches the list of users who are signed up for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A Task that resolves to a list of User objects.
     */
    public Task<List<User>> getEventSignUps(String eventId) {
        // Validate the event ID
        validateId(eventId);

        // Reference to the event's sign-ups collection
        CollectionReference eventSignUpsRef = eventsRef.document(eventId).collection("signUps");

        // Fetch the documents in the collection and continue with the task
        return eventSignUpsRef.get().continueWithTask(task -> {
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                // Fetch the user data using the document ID
                Task<DocumentSnapshot> userTask = usersRef.document(document.getId()).get();
                tasks.add(userTask);
            }

            // Wait for all the user data to be fetched
            return Tasks.whenAllSuccess(tasks);
        }).continueWith(task -> {
            // Convert the DocumentSnapshots to User objects
            return convertToObject(task.getResult(), User.class);
        });
    }

    /**
     * Fetches the list of users who are checked in to a specific event.
     *
     * @param eventId The ID of the event.
     * @return A Task that resolves to a list of User objects.
     */
    public Task<List<User>> getEventCheckIns(String eventId) {
        // Validate the event ID
        validateId(eventId);

        // Reference to the event's check-ins collection
        CollectionReference eventCheckInsRef = eventsRef.document(eventId).collection("checkIns");

        // Fetch the documents in the collection and continue with the task
        return eventCheckInsRef.get().continueWithTask(task -> {
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                // Fetch the user data using the document ID
                Task<DocumentSnapshot> userTask = usersRef.document(document.getId()).get();
                tasks.add(userTask);
            }

            // Wait for all the user data to be fetched
            return Tasks.whenAllSuccess(tasks);
        }).continueWith(task -> {
            // Convert the DocumentSnapshots to User objects
            return convertToObject(task.getResult(), User.class);
        });
    }

    /**
     * Fetches the live count of attendees for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A Task that resolves to the current attendance count.
     */
    public Task<Long> getLiveCount(String eventId) {
        // Validate the event ID
        validateId(eventId);

        // Reference to the event's live count document
        DocumentReference liveCountRef = eventsRef.document(eventId).collection("liveCounts").document("currentAttendance");

        // Fetch the document and return the current attendance count
        return liveCountRef.get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            return document.getLong("attendanceCount");
        });
    }
    /**
     * Checks if a user is checked in to a specific event.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return A Task that resolves to true if the user is checked in, false otherwise.
     */
    public Task<Boolean> isUserCheckedIn(String eventId, String userId) {
        // Validate the event and user IDs
        validateId(eventId);
        validateId(userId);

        // Reference to the user's document in the check-ins subcollection of the event
        DocumentReference userCheckInRef = eventsRef.document(eventId).collection("checkIns").document(userId);

        // Fetch the document and return whether it exists
        return userCheckInRef.get().continueWith(task -> {
            // Get the document snapshot
            DocumentSnapshot document = task.getResult();

            // Return true if the document exists (i.e., the user is checked in), false otherwise
            return document.exists();
        });
    }

    /**
     * Checks if a user is signed up to a specific event.
     *
     * @param eventId The ID of the event.
     * @param userId The ID of the user.
     * @return A Task that resolves to true if the user is signed up, false otherwise.
     */
    public Task<Boolean> isUserSignedUp(String eventId, String userId) {
        // Validate the event and user IDs
        validateId(eventId);
        validateId(userId);

        // Reference to the user's document in the sign-ups subcollection of the event
        DocumentReference userSignUpRef = eventsRef.document(eventId).collection("signUps").document(userId);

        // Fetch the document and return whether it exists
        return userSignUpRef.get().continueWith(task -> {
            // Get the document snapshot
            DocumentSnapshot document = task.getResult();

            // Return true if the document exists (i.e., the user is signed up), false otherwise
            return document.exists();
        });
    }
    /**
     * Converts a list of DocumentSnapshots to a list of objects of a specified class.
     *
     * Each DocumentSnapshot in the list is converted to an instance of the specified class.
     * If a DocumentSnapshot cannot be converted to an instance of the specified class, it is ignored.
     * can convert any document snapshot list to any class
     *
     * @param objects The list of DocumentSnapshots.
     * @param objectClass The class to convert the DocumentSnapshots to.
     * @return A list of objects of the specified class.
     */
    public <ObjectClass> List<ObjectClass> convertToObject
    (List<?> objects, Class<ObjectClass> objectClass) {
        List<ObjectClass> result = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof DocumentSnapshot) {
                DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
                ObjectClass item = documentSnapshot.toObject(objectClass);
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Converts a list of event IDs to a list of tasks that retrieve the corresponding DocumentSnapshots.
     *
     * Each task in the returned list, when run, will fetch the DocumentSnapshot for the corresponding event ID.
     * currently only works for event ids, but could be converted to work with any id by taking
     * in the collection reference as well as changing variable names
     *
     * @param eventIds The list of event IDs.
     * @return A list of tasks, each of which retrieves the DocumentSnapshot for an event ID.
     */
    public List<Task<DocumentSnapshot>> arrayToDocList(List<String> eventIds) {
        List<Task<DocumentSnapshot>> docList = new ArrayList<>();
        for (String eventId : eventIds) {
            DocumentReference eventRef = eventsRef.document(eventId);
            docList.add(eventRef.get());
        }
        return docList;
    }
}
