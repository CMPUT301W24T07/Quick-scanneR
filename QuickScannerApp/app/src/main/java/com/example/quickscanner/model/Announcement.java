/**
 * Represents an announcement for an event.
 */
package com.example.quickscanner.model;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Announcement {

    private String message;
    private String eventName;
    private String eventID;
    private String id;
    private Timestamp time;
    private String organizerID;
    private boolean isMilestone = false;

    /**
     * Default constructor for Announcement.
     */
    public Announcement() {
    }

    /**
     * Constructor for Announcement with message and event name.
     * @param message The message of the announcement.
     * @param eventName The name of the event.
     */
    public Announcement(String message, String eventName) {
        this.message = message;
        this.eventName = eventName;
    }

    /**
     * Gets the message of the announcement.
     * @return The message of the announcement.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the name of the event.
     * @return The name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the time of the announcement.
     * @return The time of the announcement.
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Gets the organizer ID of the event.
     * @return The organizer ID of the event.
     */
    public String getOrganizerID() {
        return organizerID;
    }

    /**
     * Gets the ID of the event.
     * @return The ID of the event.
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Gets the ID of the announcement.
     * @return The ID of the announcement.
     */
    public String getId() {
        return id;
    }

    /**
     * Checks if the announcement is a milestone.
     * @return True if the announcement is a milestone, false otherwise.
     */
    public boolean getIsMilestone() {
        return isMilestone;
    }

    /**
     * Sets the message of the announcement.
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the name of the event.
     * @param eventName The event name to set.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Sets the time of the announcement.
     * @param time The time to set.
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Sets the organizer ID of the event.
     * @param organizerID The organizer ID to set.
     */
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    /**
     * Sets whether the announcement is a milestone.
     * @param isMilestone True if the announcement is a milestone, false otherwise.
     */
    public void setIsMilestone(boolean isMilestone) {
        this.isMilestone = isMilestone;
    }

    /**
     * Sets the ID of the event.
     * @param eventID The event ID to set.
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Sets the ID of the announcement.
     * @param id The ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Checks if two announcements are equal based on their IDs.
     * @param compare The object to compare.
     * @return True if the announcements are equal, false otherwise.
     */
    @Override
    public boolean equals(Object compare) {
        if (compare == null || getClass() != compare.getClass()) return false;
        Announcement otherAnnouncement = (Announcement) compare;
        return Objects.equals(id, otherAnnouncement.id);
    }

    /**
     * Generates a hash code for the announcement based on its ID.
     * @return The hash code of the announcement.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
