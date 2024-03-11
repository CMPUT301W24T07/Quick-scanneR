package com.example.quickscanner.controller;

import com.example.quickscanner.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseEventController
{
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
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
}
