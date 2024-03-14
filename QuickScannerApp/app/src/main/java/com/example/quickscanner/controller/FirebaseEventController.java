package com.example.quickscanner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseEventController
{
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;
    public FirebaseEventController()
    {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");
    }
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
     * Retrieves a page of events from Firestore, sorted by the "time" field.
     * it creates a list of 30, so we don't load more then needed at once.
     * the idea is that we will load more as the user scrolls down.
     *
     * @param lastListId The last document from the previous page, or null for the first page.
     * grab this from the last event in the current list and feed it in.otherwise, it'll do it
     * from the top
     * @return A task that will complete with a list of Event objects.
     */
    public Task<List<Event>> getEvents(DocumentSnapshot lastListId) {
        // Create a query that retrieves documents from eventsRef, ordered by the "time" field.
        Query query = eventsRef.orderBy("time");

        // If lastListId is not null, start the query after this document.
        if (lastListId != null) {
            query = query.startAfter(lastListId);
        }

        // Limit the query to 30 documents and execute it.
        Task<QuerySnapshot> task = query.limit(30).get();

        // Transform the QuerySnapshot into a list of Event objects.
        return task.continueWithTask(new Continuation<QuerySnapshot, Task<List<Event>>>() {
            @Override
            public Task<List<Event>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Event> events = new ArrayList<>();
                    if (querySnapshot != null) {
                        // Convert each document in the QuerySnapshot to an Event object and
                        // add it to the list.
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                    }
                    // Return a successful task with the list of Event objects.
                    return Tasks.forResult(events);
                } else {
                    // If the task failed, return a failed task with the exception.
                    return Tasks.forException(task.getException());

                }
            }
        });
        // this can be used in the caller by using a listener to get the result. then something like
        //    public void onSuccess(List<Event> events)
        //  would work
        //then while in onsuccess you can use the list.
    }

    public Task<Event> getEvent(String eventId) {
        Task<DocumentSnapshot> task = eventsRef.document(eventId).get();
        return task.continueWithTask(new Continuation<DocumentSnapshot, Task<Event>>() {
            @Override
            public Task<Event> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        return Tasks.forResult(event);
                    } else {
                        return Tasks.forException(new Exception("No such document"));
                    }
                } else {
                    return Tasks.forException(task.getException());
                }
            }
        });
    }
    /**
     * Checks a user into an event. This method performs the following steps in a transaction:
     * 1. Reads the Event and signedUp documents.
     * 2. Checks if the user is signed up and if the event is full.
     * 3. If the user is not signed up and the event is not full, increments the
     *    currentAttendees field of the Event document and adds a document to the
     *    checkIns subcollection.
     *
     * If any step fails, the transaction is aborted and no changes are made to the database.
     *
     * @param userId  The ID of the user to check in.
     * @param eventId The ID of the event to check the user into.
     * @return A Task that will be completed when the transaction is finished.
     */
    public Task<Void> checkIn(final String userId, final String eventId) {
        final DocumentReference eventRef = eventsRef.document(eventId);
        final DocumentReference signUpRef = eventRef.collection("signUps").document(userId);
        final DocumentReference checkInRef = eventRef.collection("checkIns").document(userId);

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                DocumentSnapshot signUpSnapshot = transaction.get(signUpRef);

                Event event = eventSnapshot.toObject(Event.class);
                boolean isSignedUp = signUpSnapshot.exists();

                if (!isSignedUp && event.currentAttendees >= event.maxAttendees) {
                    throw new FirebaseFirestoreException(
                            "Event is full",
                            FirebaseFirestoreException.Code.ABORTED
                    );
                }

                if (!isSignedUp) {
                    // Increment the currentAttendees field
                    transaction.update(eventRef, "currentAttendees", FieldValue.increment(1));

                    // Add a document to the checkIns subcollection
                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("timesCheckedIn", 1); // Set the initial value of timesCheckedIn to 1
                    // Add other check-in data here
                    transaction.set(checkInRef, checkInData);
                }

                return null;
            }
        });
    }
}
