package com.example.quickscanner.model;

public class Announcement {

    private String message;
    private String eventName;

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

    public void setMessage(String message) {
        this.message = message;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
