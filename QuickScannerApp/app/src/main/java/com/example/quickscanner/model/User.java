package com.example.quickscanner.model;

import java.util.ArrayList;

public class User {
    private Profile userProfile;
    private Boolean isAdmin;
    private ArrayList<Event> organizedEvents;
    private ArrayList<Event> attendingEvents;

    public User(String name, String email, String website, String imageUrl) {
        userProfile = new Profile(name, email, website, imageUrl);
        isAdmin = false;
        organizedEvents = new ArrayList<Event>();
        attendingEvents = new ArrayList<Event>();
    }

    public User() {
        userProfile = new Profile();
        isAdmin = false;
        organizedEvents = new ArrayList<Event>();
        attendingEvents = new ArrayList<Event>();
    }

    public void setAdmin() {
        isAdmin = true;
    }
}
