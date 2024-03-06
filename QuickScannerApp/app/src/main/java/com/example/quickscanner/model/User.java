package com.example.quickscanner.model;

import java.util.ArrayList;

public class User {
    private Profile userProfile;
    private Boolean isAdmin;
    private ArrayList<Event> organizedEvents;
    private ArrayList<Event> attendingEvents;
    private String Uid;

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

    public Profile getUserProfile()
    {
        return userProfile;
    }

    public void setUserProfile(Profile userProfile)
    {
        this.userProfile = userProfile;
    }

    public Boolean getAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(Boolean admin)
    {
        isAdmin = admin;
    }

    public ArrayList<Event> getOrganizedEvents()
    {
        return organizedEvents;
    }

    public void setOrganizedEvents(ArrayList<Event> organizedEvents)
    {
        this.organizedEvents = organizedEvents;
    }

    public ArrayList<Event> getAttendingEvents()
    {
        return attendingEvents;
    }

    public void setAttendingEvents(ArrayList<Event> attendingEvents)
    {
        this.attendingEvents = attendingEvents;
    }

    public String getUid()
    {
        return Uid;
    }

    public void setUid(String uid)
    {
        Uid = uid;
    }
}
