package com.example.quickscanner.model;


import com.google.firebase.Timestamp;

import java.util.Objects;

public class Announcement {

    private String message;
    private String eventName;
    private String eventID;
    private String id;
    private Timestamp time;


    public Announcement() {
    }

    public Announcement(String message, String eventName) {
        this.message = message;
        this.eventName = eventName;
    }


    public String getMessage() {
        return message;
    }
    public String getEventName() {
        return eventName;
    }
    public Timestamp getTime() {
        return time;
    }

    public String getEventID()
    {
        return eventID;
    }
    public String getId() {
        return id;
    }



    public void setMessage(String message) {
        this.message = message;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setEventID(String eventID)
    {
        this.eventID = eventID;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public boolean equals(Object compare) {
        if (compare == null || getClass() != compare.getClass()) return false;
        Announcement otherAnnouncement = (Announcement) compare;
        return Objects.equals(id, otherAnnouncement.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
