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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAnnouncementController {
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    private final CollectionReference usersRef;
    private final FirebaseAttendanceController fbAttendanceController;

    /**
     * Constructor for FirebaseAnnouncementController.
     * Initializes Firestore database instance and references to "Events" and "users" collections.
     */
    public FirebaseAnnouncementController() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");
        usersRef = db.collection("users");
        fbAttendanceController = new FirebaseAttendanceController();
    }

    /**
     * Validates the provided ID.
     *
     * @param id The ID to be validated.
     * @throws IllegalArgumentException if the ID is null or empty.
     */
    private void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty in Attendance controller");
        }
    }

    /**
     * Attempts to add an announcement to a specific event and then notifies the event's attendees.
     * First, it fetches the event attendee IDs. If successful, it proceeds to add the announcement
     * to the event's "Announcements" collection. Regardless of the outcome, it attempts to notify
     * attendees by processing the announcements for each user.
     *
     * @param eventId      The unique identifier of the event to add the announcement to.
     * @param announcement The {@link Announcement} object containing the announcement details.
     * @return A {@link Task} that resolves to a list of strings. On success, this list is empty,
     * indicating that the announcement was added and processed successfully. If fetching attendees fails,
     * the list contains just the eventId to indicate failure in fetching attendees. If adding the
     * announcement fails but fetching attendee IDs succeeds, the list returns the user IDs to indicate
     * the intended recipients of the failed announcement.
     */
    public Task<List<String>> addAnnouncement(String eventId, Announcement announcement) {
        validateId(eventId);
        TaskCompletionSource<List<String>> taskCompletionSource = new TaskCompletionSource<>();
        fbAttendanceController.getEventAttendeeIds(eventId).addOnSuccessListener(userIds -> {
            DocumentReference eventAnnouncementRef = eventsRef.document(eventId)
                    .collection("Announcements").document();

            // attempts to add the announcement to the event
            announcement.setId(eventAnnouncementRef.getId());

            eventAnnouncementRef.set(announcement).addOnSuccessListener(aVoid -> {

                if (!userIds.contains(announcement.getOrganizerID())) {
                    userIds.add(announcement.getOrganizerID());
                }

                // If adding the announcement succeeds, proceeds as normal
                processUserAnnouncements(userIds, announcement, taskCompletionSource);
            }).addOnFailureListener(e -> {
                Log.e("FirebaseAnnouncementController", "Error getting event attendees while adding ", e);
                // If adding the announcement fails, returns the list of user IDs as the result
                taskCompletionSource.setResult(userIds);
            });
        }).addOnFailureListener(e -> {
            //if it fails to get event, it returns the eventId in the list to signify that
            Log.e("FirebaseAnnouncementController", "Error getting user ids while adding, returning event. ", e);
            List<String> failureIndicator = new ArrayList<>();
            failureIndicator.add(eventId);
            taskCompletionSource.setResult(failureIndicator);
        });

        return taskCompletionSource.getTask();
    }

    /**
     * Processes announcements for a batch of users. This method handles the distribution of
     * the announcement to each user's "Announcements" collection in Firestore. It manages
     * batch processing to avoid exceeding Firestore's batch size limits.
     * <p>
     * The method divides the user IDs into manageable batches and attempts to write the announcement
     * to each user's document. If any batch fails, the IDs from that batch are collected for reporting.
     *
     * @param userIds              A list of user IDs representing the recipients of the announcement.
     * @param announcement         The {@link Announcement} object to be distributed.
     * @param taskCompletionSource A {@link TaskCompletionSource} used to signal the completion
     *                             of the announcement processing, either successfully or with a list
     *                             of user IDs that failed to be notified.
     */

    private void processUserAnnouncements(List<String> userIds, Announcement announcement, TaskCompletionSource<List<String>> taskCompletionSource) {
        final int MAX_BATCH_SIZE = 500;
        List<Task<Void>> taskList = new ArrayList<>();
        List<String> failedUserIds = new ArrayList<>();
        if (userIds.isEmpty()) {
            // If there are no users to process, resolves the task with an empty list
            taskCompletionSource.setResult(new ArrayList<>());
            return;
        }
        // Process's each user in different parts to avoid exceeding the batch limit
        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            int end = Math.min(userIds.size(), i + MAX_BATCH_SIZE);
            List<String> subList = userIds.subList(i, end);


            //writes each batch to the database in the users collection.
            WriteBatch batch = db.batch();
            for (String userId : subList) {
                DocumentReference userRef = usersRef.document(userId)
                        .collection("Announcements").document();
                batch.set(userRef, announcement);
            }
            //if writing fails, adds the user to the failedUserIds list
            Task<Void> batchTask = batch.commit().addOnFailureListener(e -> {
                // Collects user IDs from failed batches
                failedUserIds.addAll(subList);
            });
            //otherwise adds the task to the taskList
            taskList.add(batchTask);
        }

        // Wait for all batches to complete
        Tasks.whenAllComplete(taskList).addOnCompleteListener(task -> {
            if (failedUserIds.isEmpty()) {
                // If there are no failures, resolves with an empty list indicating success
                taskCompletionSource.setResult(new ArrayList<>());
            } else {
                // If some user operations failed, resolves with the list of failed user IDs
                taskCompletionSource.setResult(failedUserIds);
            }
        });
    }


    public ListenerRegistration setupAnnouncementListListener(String userid, ArrayList<Announcement> announcementDataList, AnnouncementArrayAdapter adapter, TextView emptyAnnouncement, ListView listView) {
        validateId(userid);

        CollectionReference userAnnouncementsRef = usersRef.document(userid).collection("Announcements");
//
        // Query query = isOrganizer ? userAnnouncementsRef : userAnnouncementsRef.whereEqualTo("isMilestone", false);
//        Query query = userid== ? userAnnouncementsRef.whereEqualTo("isMilestone", true) : userAnnouncementsRef.whereEqualTo("isMilestone", false);


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
                    if (value.isEmpty()) {
                        listView.setVisibility(View.GONE);
                        emptyAnnouncement.setVisibility(View.VISIBLE);
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Announcement ann = dc.getDocument().toObject(Announcement.class);
                        if (userid.equals(ann.getOrganizerID())) {
                            //current user is the organiser.
                            //show only milestone announcements
                            Log.d("weird", "organiser id equals user id");
                            if (ann.getIsMilestone()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Announcement newAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        announcementDataList.add(0, newAnnouncement);
                                        break;
                                    case MODIFIED:
                                        Announcement modifiedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        for (int i = 0; i < announcementDataList.size(); i++) {
                                            if (announcementDataList.get(i).getId().equals(modifiedAnnouncement.getId())) {
                                                announcementDataList.set(i, modifiedAnnouncement);
                                                break;
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        //TODO make this method inside all other listeners.
                                        Announcement removedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        announcementDataList.removeIf(announcement -> announcement.getId().equals(removedAnnouncement.getId()));
                                        break;
                                }
                            }
                        } else {
                            //current user is not the organiser.
                            //show only non milestone announcements
                            if (!ann.getIsMilestone()) {
                                Log.d("weird", "organiser id does not                             Log.d(\"weird\",\"organiser id equals user id\");\nequals user id");

                                switch (dc.getType()) {
                                    case ADDED:
                                        Announcement newAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        announcementDataList.add(0, newAnnouncement);
                                        break;
                                    case MODIFIED:
                                        Announcement modifiedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        for (int i = 0; i < announcementDataList.size(); i++) {
                                            if (announcementDataList.get(i).getId().equals(modifiedAnnouncement.getId())) {
                                                announcementDataList.set(i, modifiedAnnouncement);
                                                break;
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        //TODO make this method inside all other listeners.
                                        Announcement removedAnnouncement = dc.getDocument().toObject(Announcement.class);
                                        announcementDataList.removeIf(announcement -> announcement.getId().equals(removedAnnouncement.getId()));
                                        break;
                                }
                            }
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
                    updateVisibility(announcementDataList, listView, emptyAnnouncement);
                    Log.d("Halpp", "announcement changes made");
                    adapter.notifyDataSetChanged();
                });
    }

    private void updateVisibility(ArrayList<Announcement> announcementDataList, ListView listView, TextView emptyAnnouncement) {
        if (announcementDataList.isEmpty()) {
            emptyAnnouncement.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyAnnouncement.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void addMilestoneAnnouncement(String eventId, long count, String milestoneType) {
        validateId(eventId);

        if (count == 10 || count == 20) {
            Announcement milestoneAnnouncement = new Announcement();
            milestoneAnnouncement.setEventName(milestoneType + " Milestone Reached");
            milestoneAnnouncement.setMessage("Your event has reached " + count + " " + milestoneType.toLowerCase() + "!");
            milestoneAnnouncement.setIsMilestone(true);
            milestoneAnnouncement.setOrganizerID("system announcement");

            addAnnouncement(eventId, milestoneAnnouncement);
        }
    }



}

