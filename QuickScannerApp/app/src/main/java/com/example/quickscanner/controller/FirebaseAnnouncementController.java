package com.example.quickscanner.controller;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quickscanner.AnnouncementArrayAdapter;
import com.example.quickscanner.model.Announcement;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

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
     * @param eventId The ID of the event.
     * @param announcement The announcement to be added.
     * @return A Task representing the operation of adding the announcement to both collections.
     */
    public Task<List<String>> addAnnouncement(String eventId, Announcement announcement, List<String> userIds) {
        validateId(eventId);
        for (String userId : userIds) {
            validateId(userId);
        }
        TaskCompletionSource<List<String>> taskCompletionSource = new TaskCompletionSource<>();

        // Attempt to add the announcement to the event
        DocumentReference eventAnnouncementRef = eventsRef.document(eventId)
                .collection("Announcements").document();

        eventAnnouncementRef.set(announcement).addOnSuccessListener(aVoid -> {
            // If adding to the event succeeds, proceed with users
            processUserAnnouncements(userIds, announcement, taskCompletionSource);
        }).addOnFailureListener(e -> {
            // If adding to the event fails, resolve the task with the entire list of user IDs
            taskCompletionSource.setResult(userIds);
        });

        return taskCompletionSource.getTask();
    }

    private void processUserAnnouncements(List<String> userIds, Announcement announcement, TaskCompletionSource<List<String>> taskCompletionSource) {
        final int MAX_BATCH_SIZE = 500;
        List<Task<Void>> taskList = new ArrayList<>();
        List<String> failedUserIds = new ArrayList<>();
        if (userIds.isEmpty()) {
            // If there are no users to process, resolve the task with an empty list
            taskCompletionSource.setResult(new ArrayList<>());
            return;
        }
        // Process each user in different parts to avoid exceeding the batch limit
        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            int end = Math.min(userIds.size(), i + MAX_BATCH_SIZE);
            List<String> subList = userIds.subList(i, end);



            //write each batch to the database in the users collection.
            WriteBatch batch = db.batch();
            for (String userId : subList) {
                DocumentReference userRef = usersRef.document(userId)
                        .collection("Announcements").document();
                batch.set(userRef, announcement);
            }
            //if writing fails, add the user to the failedUserIds list
            Task<Void> batchTask = batch.commit().addOnFailureListener(e -> {
                // Collect user IDs from failed batches
                failedUserIds.addAll(subList);
            });
            //otherwise add the task to the taskList
            taskList.add(batchTask);
        }

        // Wait for all batches to complete
        Tasks.whenAllComplete(taskList).addOnCompleteListener(task -> {
            if (failedUserIds.isEmpty()) {
                // If there are no failures, resolve with an empty list indicating success
                taskCompletionSource.setResult(new ArrayList<>());
            } else {
                // If some user operations failed, resolve with the list of failed user IDs
                taskCompletionSource.setResult(failedUserIds);
            }
        });
    }
    public ListenerRegistration setupAnnouncementListListener(String userid, ArrayList<Announcement> announcementDataList, AnnouncementArrayAdapter adapter, TextView emptyAnnouncement, ListView listView) {
        validateId(userid);

        return usersRef.document(userid).collection("Announcements")
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
                                announcementDataList.add(0, newAnnouncement);
                                break;
                            case MODIFIED:
                                Announcement modifiedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                for (int i = 0; i < announcementDataList.size(); i++)
                                {
                                    if (announcementDataList.get(i).getId().equals(modifiedAnnouncement.getId()))
                                    {
                                        announcementDataList.set(i, modifiedAnnouncement);
                                        break;
                                    }
                                }
                            case REMOVED:
                                Announcement removedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                announcementDataList.remove(removedAnnouncement);
                                break;
                        }
                    }


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

