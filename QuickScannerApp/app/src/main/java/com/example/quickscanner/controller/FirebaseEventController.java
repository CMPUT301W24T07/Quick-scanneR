package com.example.quickscanner.controller;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.model.ConferenceConfig;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Controller for managing events in Firestore.
 */
public class FirebaseEventController
{
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;
    private final CollectionReference configRef;

    /**
     * Initializes Firestore and a reference to the "Events" collection.
     */
    public FirebaseEventController()
    {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");
        configRef = db.collection("config");
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
            throw new IllegalArgumentException("ID cannot be null or empty in Event controller");
        }
    }

    /**
     * Adds new event to Firestore.
     *
     * @param event the event to be added
     * @return a Task that will be once the event is added
     */
    public Task<DocumentReference> addEvent(final Event event) {
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();

        // Create a new document with an auto-generated ID in the events collection
        final DocumentReference newEventRef = eventsRef.document();

        // Get the generated document ID
        String eventId = newEventRef.getId();

        // Set the eventID field of the event object
        event.setEventID(eventId);

        // Now set the document with the event data, including the eventID
        newEventRef.set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AddEventActivity", "Event added successfully with ID: " + eventId);
                        // Here, signal success and provide the DocumentReference
                        taskCompletionSource.setResult(newEventRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AddEventActivity", "Error adding event", e);
                        // Signal failure
                        taskCompletionSource.setException(e);
                    }
                });

        // Return the Task that will eventually provide the DocumentReference
        return taskCompletionSource.getTask();
    }



    /**
     * Updates existing event in Firestore. AND merges new Fields.
     * NOTE: this does not update checked in or signed up users.
     * you must use signUp and checkIn methods for that.
     * use signUp and checkIn methods for that.
     *
     * @param event the event to be updated
     * @return a Task that will be completed once the event is updated
     */
    public Task<Void> updateAndMergeEvent(Event event)
    {
        return eventsRef.document(event.getEventID()).set(event, SetOptions.merge());
    }

    /**
     * Updates existing event in Firestore.
     * NOTE: this does not update checked in or signed up users.
     * you must use signUp and checkIn methods for that.
     * use signUp and checkIn methods for that.
     *
     * @param event the event to be updated
     * @return a Task that will be completed once the event is updated
     */
    public Task<Void> updateEvent(Event event)
    {
        Log.d("AddEventActivity", "updateEvent: updating event with ID: " + event.getEventID());
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
        validateId(eventId);
        deleteFromAttendance(eventId);
        return eventsRef.document(eventId).delete();
    }

    public void deleteFromAttendance(String eventId) {

    }


    public Task<List<Event>> getEvents()
    {
        Query query = eventsRef.orderBy("time").limit(30);

        Task<QuerySnapshot> task = query.get();

        return task.continueWith(new Continuation<QuerySnapshot, List<Event>>()
        {
            @Override
            public List<Event> then(@NonNull Task<QuerySnapshot> task) throws Exception
            {
                if (task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Event> events = new ArrayList<>();
                    if (querySnapshot != null)
                    {
                        for (QueryDocumentSnapshot document : querySnapshot)
                        {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                    }
                    return events;
                }
                else
                {
                    throw task.getException();
                }
            }
        });
    }
    /**
     * Retrieves a specific event from Firestore.
     *
     * @param eventId The ID of the event to retrieve.
     * @return A task that completes with the Event object.
     */
    public Task<Event> getEvent(String eventId)
    {
        validateId(eventId);
        Task<DocumentSnapshot> task = eventsRef.document(eventId).get();
        return task.continueWithTask(new Continuation<DocumentSnapshot, Task<Event>>()
        {
            @Override
            public Task<Event> then(@NonNull Task<DocumentSnapshot> task) throws Exception
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        Event event = document.toObject(Event.class);
                        return Tasks.forResult(event);
                    }
                    else
                    {
                        return Tasks.forException(new Exception("No such Event"));
                    }
                }
                else
                {
                    return Tasks.forException(task.getException());
                }
            }
        });
    }

    public ListenerRegistration setUpOrganizedEventsListener(String orgId, ArrayList<Event> eventsDataList, ArrayAdapter<Event> eventAdapter, TextView emptyEvents, ListView listView)
    {
        //log saying that the orgId is being passed
        return eventsRef.whereEqualTo("organizerID", orgId)
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        //log stating in on event
                        if (e != null)
                        {
                            Log.e("SetUpOrganizedEventsListener", "Error getting events", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null)
                        {
                            if(queryDocumentSnapshots.isEmpty())
                            {
                                listView.setVisibility(View.GONE);
                                emptyEvents.setVisibility(View.VISIBLE);
                            }


                        for (DocumentChange docChange : queryDocumentSnapshots.getDocumentChanges())
                        {
                            switch (docChange.getType())
                            {
                                case ADDED:
                                    // A new document has been added, add it to the list
                                    Event newEvent = docChange.getDocument().toObject(Event.class);
                                    eventsDataList.add(newEvent);
                                    updateVisibility();
                                    break;
                                case MODIFIED:
                                    // An existing document has been modified, update it in the list
                                    Event modifiedEvent = docChange.getDocument().toObject(Event.class);
                                    int index = eventsDataList.indexOf(modifiedEvent);
                                    if (index != -1)
                                    {
                                        eventsDataList.set(index, modifiedEvent);
                                    }
                                    updateVisibility();
                                    break;
                                case REMOVED:
                                    // A document has been removed, remove it from the list
                                    Event removedEvent = docChange.getDocument().toObject(Event.class);
                                    eventsDataList.remove(removedEvent);
                                    updateVisibility();
                                    break;
                            }
                        }
                    }else {
                            Log.e("SetUpOrganizedEventsListener", "queryDocumentSnapshots is null");
                        }

                        // Notify the adapter that the data has changed
                        eventAdapter.notifyDataSetChanged();
                    }
                    private void updateVisibility()
                    {
                        if (eventsDataList.isEmpty())
                        {
                            listView.setVisibility(View.GONE);
                            emptyEvents.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            listView.setVisibility(View.VISIBLE);
                            emptyEvents.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public ListenerRegistration setupAttendListListener(String userId, ArrayList<Event> signedUpEventsList, ArrayAdapter<Event> adapter, TextView emptyList, ListView listView) {
        validateId(userId);
        DocumentReference userSignUpsRef = db.collection("users").document(userId).collection("Attendance").document("signedUpEvents");

        return userSignUpsRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("UserSignedUpEvents", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    List<String> eventIds = (List<String>) snapshot.get("eventIds");
                    if (eventIds == null || eventIds.isEmpty()) {
                        listView.setVisibility(View.GONE);
                        emptyList.setVisibility(View.VISIBLE);
                    } else {
                        // Handle added events
                        for (String eventId : eventIds) {
                            getEvent(eventId)
                                    .addOnSuccessListener(event -> {
                                        if (!signedUpEventsList.contains(event)) {
                                            signedUpEventsList.add(event);
                                            adapter.notifyDataSetChanged();
                                            updateVisibility();
                                        }
                                    });
                        }

                        // Handle removed events
                        signedUpEventsList.removeIf(event -> !eventIds.contains(event.getEventID()));
                        adapter.notifyDataSetChanged();
                        updateVisibility();
                    }
                } else {
                    Log.d("UserSignedUpEvents", "Current data: null");
                }
            }

            private void updateVisibility() {
                if (signedUpEventsList.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    emptyList.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * Sets up a Firestore listener for real-time updates to the events list.
     *
     * 1. This method sets up a listener on the Firestore "Events" collection.
     * The listener will be triggered every time there's a change in the collection.
     * 2. Inside the listener, it first checks if there was an error. If there was,
     * it logs the error and returns.
     * 3. If there was no error, it checks if the QuerySnapshot is not null and not empty. If it is,
     * it iterates over the document changes in the QuerySnapshot.
     * 4. For each DocumentChange, it checks the type of change:
     *    - If the change type is ADDED,
     *      it converts the document to an Event object and adds it to the eventsDataList.
     *    - If the change type is MODIFIED,
     *      it converts the document to an Event object and finds its position in the eventsDataList.
     *    If the event is found in the list, it checks if the image, name, or time has changed.
     *      If any of these have changed, it replaces the old event with the new one in the
     *      eventsDataList.
     *    - If the change type is REMOVED,
     *    it converts the document to an Event object and removes it from the eventsDataList.
     * 5. After processing all the document changes,
     * it sorts the eventsDataList in descending order based on the timestamp.
     * 6. It then iterates over the eventsDataList, and if it finds an event whose time is before
     * the current time, it removes that event from the list.
     * 7. Finally, it notifies the eventAdapter that the data has changed.
     * This will cause the UI to update and reflect the changes in the data.
     *
     * @param eventsDataList The list of events to update. This list should be modifiable.
     * @param eventAdapter The ArrayAdapter to notify of changes. This adapter should be connected to the UI.
     */

    public ListenerRegistration setupEventListListener(final ArrayList<Event> eventsDataList, final ArrayAdapter<Event> eventAdapter) {
        Date currentTime = new Date(); // Get the current time

        return eventsRef.whereGreaterThan("time", currentTime)
                .addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("event fragment listener", "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange changes : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (changes.getType()) {
                                    case ADDED:
                                        Event newEvent = changes.getDocument().toObject(Event.class);
                                        eventsDataList.add(newEvent);
                                        break;
                                    case MODIFIED:
                                        Event modifiedEvent = changes.getDocument().toObject(Event.class);
                                        Log.d("AddEventActivity", "onEvent: modified event: " + modifiedEvent.getName() + "ID: " + modifiedEvent.getEventID());

                                        int modifiedPosition = eventsDataList.indexOf(modifiedEvent);

                                        if (modifiedPosition != -1) {
                                             Event oldEvent = eventsDataList.get(modifiedPosition);
                                            // Check if the image, name, or time has changed
                                            Log.d("AddEventActivity", "onEvent: modified event: " + modifiedEvent.getName() + "ID: " + modifiedEvent.getEventID()
                                            + "old wevent: " + oldEvent.getName() + "ID: " + oldEvent.getEventID());
                                            if (!oldEvent.getImagePath().equals(modifiedEvent.getImagePath()) ||
                                                    !oldEvent.getName().equals(modifiedEvent.getName()) ||
                                                    !oldEvent.getTime().equals(modifiedEvent.getTime())
                                            || !oldEvent.getEventID().equals(modifiedEvent.getEventID()))
                                            {
                                                // Replace the old event with the new one
                                                eventsDataList.set(modifiedPosition, modifiedEvent);
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        Event removedEvent = changes.getDocument().toObject(Event.class);
                                        eventsDataList.remove(removedEvent);
                                        break;
                                }
                            }
                            // Sort the eventsDataList in ascending order based on the timestamp
                            eventsDataList.sort(new Comparator<Event>() {
                                @Override
                                public int compare(Event e1, Event e2) {
                                    return e1.getTime().compareTo(e2.getTime());
                                }
                            });
                            // Notify the adapter of dataset changes
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("eventfragment listener", "Current data: null");
                        }
                    }
                });
    }
    /**
     * Retrieves the conference configuration from Firestore.
     *
     * @return a Task that completes with the ConferenceConfig object.
     */
    public Task<ConferenceConfig> getConferenceConfig()
    {
        return configRef.document("ConferenceConfig")
                .get()
                .continueWithTask(new Continuation<DocumentSnapshot, Task<ConferenceConfig>>()
                {
                    @Override
                    public Task<ConferenceConfig> then(@NonNull Task<DocumentSnapshot> task) throws Exception
                    {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                return Tasks.forResult(document.toObject(ConferenceConfig.class));
                            }
                            else
                            {
                                return Tasks.forException(new Exception("No such document"));
                            }
                        }
                        else
                        {
                            return Tasks.forException(task.getException());
                        }
                    }
                });
    }

    public Task<Event> getEventByImageURL(String imageURL) {
        Query query = eventsRef.whereEqualTo("imagePath", imageURL);

        Task<QuerySnapshot> task = query.get();

        return task.continueWith(new Continuation<QuerySnapshot, Event>() {
            @Override
            public Event then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        return document.toObject(Event.class);
                    } else {
                        throw new Exception("No event found with the given image URL");
                    }
                } else {
                    throw task.getException();
                }
            }
        });
    }
}
