package com.example.quickscanner.model;

import java.util.ArrayList;

public class Event {
    public String name;
    public String description;
    public String imagePath;
    public User organizer;
    public String time;
    public String location;



    public String geoLocation;
    public String eventID;
    public String organizerID;
    public ArrayList<String> signUps = new ArrayList<>();
    public ArrayList<String> checkIns = new ArrayList<>();
    private boolean isGeolocationEnabled;
    public int takenSpots ;
    public Integer maxSpots;
    public String checkInQrCode;
    public String promoQrCode;

    public Event(String name, String description, String organizerID, String time, String location) {
        this.name = name;
        this.description = description;
        imagePath = "default.jpeg";
        this.organizerID = organizerID;
        this.time = time;
        this.location = location;
        this.takenSpots  = 0;
    }

    public Event(String name, String description, String imagePath, String organizerID, String time, String location) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.organizerID = organizerID;
        this.time = time;
        this.location = location;
        this.takenSpots = 0;
    }

    public Event() {
        this.takenSpots = 0;
    }

    // Getters and Setters

    /**
     * toggles the isGeolocationEnabled value
     * false -> true, true -> false
     */
    public void toggleIsGeolocationEnabled() {
        this.isGeolocationEnabled = !this.isGeolocationEnabled;
    }

    // Getters
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getImagePath() {return imagePath;}
    public User getOrganizer() {return organizer;}
    public String getTime() {return time;}
    public String getLocation() {return location;}
    public String getEventID() {return eventID;}
    public String getOrganizerID() {return organizerID;}

    public boolean getIsGeolocationEnabled() {return isGeolocationEnabled; }

    public ArrayList<String> getSignUps() {return signUps;}

    public ArrayList<String> getCheckIns() {return checkIns;}

    public Integer getMaxSpots() {return maxSpots;}
    public int getTakenSpots() {return takenSpots;}

    public boolean isGeolocationEnabled()
    {
        return isGeolocationEnabled;
    }

    public String getCheckInQrCode()
    {
        return checkInQrCode;
    }

    public String getPromoQrCode()
    {
        return promoQrCode;
    }

    // Setters
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}
    public void setOrganizer(User organizer) {this.organizer = organizer;}
    public void setTime(String time) {this.time = time;}
    public void setLocation(String location) {this.location = location;}
    public void setEventID(String eventID) {this.eventID = eventID;}
    public void setOrganizerID(String organizerID) {this.organizerID = organizerID;}

    public void setGeolocationEnabled(boolean geolocationEnabled) {this.isGeolocationEnabled = geolocationEnabled;}

    public void setSignUps(ArrayList<String> signUps) {this.signUps = signUps;}
    public void setCheckIns(ArrayList<String> checkIns) {this.checkIns = checkIns;}
    public void setMaxSpots(Integer maxSpots) {this.maxSpots = maxSpots;}
    public void setTakenSpots(int takenSpots) {this.takenSpots = takenSpots;}

    public void setCheckInQrCode(String checkInQrCode)
    {
        this.checkInQrCode = checkInQrCode;
    }

    public void setPromoQrCode(String promoQrCode)
    {
        this.promoQrCode = promoQrCode;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }
}
