package com.example.quickscanner.model;

import java.util.ArrayList;

public class User {
    private Profile userProfile;
    private Boolean isAdmin;
    private ArrayList<String> organizedEvents;
    private ArrayList<String> attendingEvents;
    private String Uid;
    public User(String name, String email, String website, String imageUrl) {
        userProfile = new Profile(name, email, website, imageUrl);
        isAdmin = false;
        organizedEvents = new ArrayList<String>();
        attendingEvents = new ArrayList<String>();
    }

    public User() {
        userProfile = new Profile();
        isAdmin = false;
        organizedEvents = new ArrayList<String>();
        attendingEvents = new ArrayList<String>();
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

    public ArrayList<String> getOrganizedEvents()
    {
        return organizedEvents;
    }

    public void setOrganizedEvents(ArrayList<String> organizedEvents)
    {
        this.organizedEvents = organizedEvents;
    }

    public ArrayList<String> getAttendingEvents()
    {
        return attendingEvents;
    }

    public void setAttendingEvents(ArrayList<String> attendingEvents)
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

    public int getOrganizedEventsSize() {
        return organizedEvents.size();
    }

    public int getAttendingEventsSize() {
        return attendingEvents.size();
    }

}
