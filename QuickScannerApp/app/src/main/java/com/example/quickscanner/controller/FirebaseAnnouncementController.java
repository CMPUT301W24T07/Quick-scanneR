package com.example.quickscanner.controller;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quickscanner.AnnouncementArrayAdapter;
import com.example.quickscanner.model.Announcement;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class FirebaseAnnouncementController
{
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    private final CollectionReference usersRef;

    /**
     * Constructor for FirebaseAnnouncementController.
     * Initializes Firestore database instance and references to "Events" and "users" collections.
     */
    public FirebaseAnnouncementController()
    {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");
        usersRef = db.collection("users");
    }
    /**
     * Validates the provided ID.
     * @param id The ID to be validated.
     * @throws IllegalArgumentException if the ID is null or empty.
     */
    private void validateId(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("ID cannot be null or empty in Attendance controller");
        }
    }
    /**
     * Adds an announcement to both the event's and user's "Announcements" sub-collections.
     * The same ID is used for the announcement in both collections.
     * @param eventID The ID of the event.
     * @param userID The ID of the user.
     * @param announcement The announcement to be added.
     * @return A Task representing the operation of adding the announcement to both collections.
     */
    public Task<Void> addAnnouncement(String eventID, String userID, Announcement announcement) {
        validateId(eventID);
        validateId(userID);
        return db.runTransaction(transaction -> {
            // Create a new document in the event's "Announcements" collection
            DocumentReference newEventAnnouncementRef = eventsRef.document(eventID).collection("Announcements").document();
            transaction.set(newEventAnnouncementRef, announcement);

            // Create a new document with the same ID in the user's "Announcements" collection
            DocumentReference newUserAnnouncementRef = usersRef.document(userID).collection("Announcements").document(newEventAnnouncementRef.getId());
            transaction.set(newUserAnnouncementRef, announcement);

            return null;
        });
    }
    /**
     * Deletes an announcement from both the event's and user's "Announcements" sub-collections.
     * @param eventID The ID of the event.
     * @param userID The ID of the user.
     * @param announcementID The ID of the announcement to be deleted.
     * @return A Task representing the operation of deleting the announcement from both collections.
     */
    public Task<Void> deleteAnnouncement(String eventID, String userID, String announcementID) {
        validateId(eventID);
        validateId(userID);
        validateId(announcementID);

        return db.runTransaction(transaction -> {
            // Delete the document from the event's "Announcements" collection
            DocumentReference eventAnnouncementRef = eventsRef.document(eventID).collection("Announcements").document(announcementID);
            transaction.delete(eventAnnouncementRef);

            // Delete the document from the user's "Announcements" collection
            DocumentReference userAnnouncementRef = usersRef.document(userID).collection("Announcements").document(announcementID);
            transaction.delete(userAnnouncementRef);

            return null;
        });
    }
    public ListenerRegistration setupAnnouncementListListener(String eventID, ArrayList<Announcement> announcementDataList, AnnouncementArrayAdapter adapter, TextView emptyAnnouncement, ListView listView) {
        validateId(eventID);

        return eventsRef.document(eventID).collection("Announcements")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirebaseAnnouncementController", "Error getting announcements", error);
                        return;
                    }

                    if (value == null) {
                        Log.e("FirebaseAnnouncementController", "No announcements found");
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Announcement newAnnouncement = dc.getDocument().toObject(Announcement.class);
                                announcementDataList.add(newAnnouncement);
                                break;
                            case MODIFIED:
                                Announcement modifiedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                int index = announcementDataList.indexOf(modifiedAnnouncement);
                                if (index != -1) {
                                    announcementDataList.set(index, modifiedAnnouncement);
                                }
                                break;
                            case REMOVED:
                                Announcement removedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                announcementDataList.remove(removedAnnouncement);
                                break;
                        }
                    }

                    // Sort the list based on the 'time' field
                    announcementDataList.sort((a1, a2) -> a2.getTime().compareTo(a1.getTime()));

                    //this is how we check if the list is empty or not
                    //if it is empty, we show the no annoucements to view text
                    //if it is not empty, we show the list of announcements
                    //depends on if you want to implement this it
                    //should just need to set the emptyAnnouncement text to the "no annoucements!"
                    //or something
                    //if you dont want to just remove the text view and the listview form parameters
                    //and delete this.
                    if (announcementDataList.isEmpty()) {
                        emptyAnnouncement.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        emptyAnnouncement.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}

