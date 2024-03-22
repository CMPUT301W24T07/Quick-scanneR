package com.example.quickscanner.controller;

import androidx.annotation.NonNull;

import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
/**
 * Controller for managing events in Firestore.
 */
public class FirebaseEventController
{
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    /**
     * Initializes Firestore and a reference to the "Events" collection.
     */
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
     * Updates existing event in Firestore. AND merges new Fields.
     * NOTE: this does not update checked in or signed up users.
     *  you must use signUp and checkIn methods for that.
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


    public Task<List<Event>> getEvents() {
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
    public Task<List<Event>> continueGetEvents(String lastEventId) {
        Task<DocumentSnapshot> lastEventTask = eventsRef.document(lastEventId).get();

        return lastEventTask.continueWithTask(new Continuation<DocumentSnapshot, Task<List<Event>>>() {
            @Override
            public Task<List<Event>> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot lastEvent = task.getResult();
                Query query = eventsRef.orderBy("time").startAfter(lastEvent).limit(30);

                Task<QuerySnapshot> queryTask = query.get();

                return queryTask.continueWith(new Continuation<QuerySnapshot, List<Event>>() {
                    @Override
                    public List<Event> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            List<Event> events = new ArrayList<>();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Event event = document.toObject(Event.class);
                                    events.add(event);
                                }
                            }
                            return events;
                        } else {
                            throw task.getException();
                        }
                    }
                });
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


    /**
     * Retrieves specific event *Task* from Firestore.
     * This is different from getEvents, as it does not return the object.
     * Instead, it returns a task of pulling a snapshot of the document.
     *
     * @param eventId the ID of the event to retrieve
     * @return a Task that will be completed once the event is fetched
     */
    public Task<DocumentSnapshot> getEventTask(String eventId)
    {
        return eventsRef.document(eventId).get();

    }
}
