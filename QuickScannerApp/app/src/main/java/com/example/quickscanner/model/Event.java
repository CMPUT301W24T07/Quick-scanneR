package com.example.quickscanner.model;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import com.example.quickscanner.singletons.ConferenceConfigSingleton;

public class Event {

    public String name;
    public String description;
    public String imagePath;
    public User organizer;
    public Timestamp time;
    public String location;
    private boolean selected;
    ConferenceConfigSingleton configSingleton;



    private String geoLocation;
    private String eventID;
    private String organizerID;
    private ArrayList<String> signUps = new ArrayList<>();
    private ArrayList<String> checkIns = new ArrayList<>();
    private boolean isGeolocationEnabled;
    private int takenSpots ;
    private Integer maxSpots;
    private String checkInQrCode;
    private String promoQrCode;

    public Event(String name, String description, String organizerID, Timestamp time, String location) {
        this.name = name;
        this.description = description;
        imagePath = "default.jpeg";
        this.organizerID = organizerID;
        this.time = time;
        this.location = location;
        this.takenSpots  = 0;
    }

    public Event(String name, String description, String imagePath, String organizerID, Timestamp time, String location) {
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
    public Timestamp getTime() {return time;}
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
    public void setTime(Timestamp time) {this.time = time;}
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

    public boolean isSelected() {return selected;}

    public void setSelected(boolean selected) {this.selected = selected;}
    public String getTimeAsString() {
        configSingleton = ConferenceConfigSingleton.getInstance();
        if (configSingleton == null) {
            Log.e("Event", "ConferenceConfigSingleton is null");
            return "";
        }
        String timeZone = ConferenceConfigSingleton.getInstance().getTimeZone();
        SimpleDateFormat date = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault());

        date.setTimeZone(TimeZone.getTimeZone(timeZone));
        return date.format(this.time.toDate());
    }
}
