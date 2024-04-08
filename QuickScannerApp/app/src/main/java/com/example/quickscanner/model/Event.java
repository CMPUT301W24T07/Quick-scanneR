package com.example.quickscanner.model;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import com.example.quickscanner.singletons.ConferenceConfigSingleton;

/**
 * Represents an event.
 */
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

    /**
     * Constructor for Event with specified parameters.
     * @param name The name of the event.
     * @param description The description of the event.
     * @param organizerID The organizer ID of the event.
     * @param time The time of the event.
     * @param location The location of the event.
     */
    public Event(String name, String description, String organizerID, Timestamp time, String location) {
        this.name = name;
        this.description = description;
        imagePath = "default.jpeg";
        this.organizerID = organizerID;
        this.time = time;
        this.location = location;
        this.takenSpots  = 0;
    }

    /**
     * Constructor for Event with specified parameters including imagePath.
     * @param name The name of the event.
     * @param description The description of the event.
     * @param imagePath The image path of the event.
     * @param organizerID The organizer ID of the event.
     * @param time The time of the event.
     * @param location The location of the event.
     */
    public Event(String name, String description, String imagePath, String organizerID, Timestamp time, String location) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.organizerID = organizerID;
        this.time = time;
        this.location = location;
        this.takenSpots = 0;
    }

    /**
     * Default constructor for Event.
     */
    public Event() {
        this.takenSpots = 0;
    }

    // Getters and Setters

    /**
     * Toggles the isGeolocationEnabled value.
     * false -> true, true -> false.
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
    public boolean isGeolocationEnabled() {return isGeolocationEnabled;}
    public String getCheckInQrCode() {return checkInQrCode;}
    public String getPromoQrCode() {return promoQrCode;}
    public String getGeoLocation() {return geoLocation;}
    public boolean isSelected() {return selected;}

    /**
     * Sets the name of the event.
     * @param name The name to set.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Sets the description of the event.
     * @param description The description to set.
     */
    public void setDescription(String description) {this.description = description;}

    /**
     * Sets the image path of the event.
     * @param imagePath The image path to set.
     */
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}

    /**
     * Sets the organizer of the event.
     * @param organizer The organizer to set.
     */
    public void setOrganizer(User organizer) {this.organizer = organizer;}

    /**
     * Sets the time of the event.
     * @param time The time to set.
     */
    public void setTime(Timestamp time) {this.time = time;}

    /**
     * Sets the location of the event.
     * @param location The location to set.
     */
    public void setLocation(String location) {this.location = location;}

    /**
     * Sets the event ID.
     * @param eventID The event ID to set.
     */
    public void setEventID(String eventID) {this.eventID = eventID;}

    /**
     * Sets the organizer ID.
     * @param organizerID The organizer ID to set.
     */
    public void setOrganizerID(String organizerID) {this.organizerID = organizerID;}

    /**
     * Sets whether geolocation is enabled.
     * @param geolocationEnabled The value to set.
     */
    public void setGeolocationEnabled(boolean geolocationEnabled) {this.isGeolocationEnabled = geolocationEnabled;}

    /**
     * Sets the sign-ups list.
     * @param signUps The sign-ups list to set.
     */
    public void setSignUps(ArrayList<String> signUps) {this.signUps = signUps;}

    /**
     * Sets the check-ins list.
     * @param checkIns The check-ins list to set.
     */
    public void setCheckIns(ArrayList<String> checkIns) {this.checkIns = checkIns;}

    /**
     * Sets the maximum spots.
     * @param maxSpots The maximum spots to set.
     */
    public void setMaxSpots(Integer maxSpots) {this.maxSpots = maxSpots;}

    /**
     * Sets the taken spots.
     * @param takenSpots The taken spots to set.
     */
    public void setTakenSpots(int takenSpots) {this.takenSpots = takenSpots;}

    /**
     * Sets the check-in QR code.
     * @param checkInQrCode The check-in QR code to set.
     */
    public void setCheckInQrCode(String checkInQrCode) {this.checkInQrCode = checkInQrCode;}

    /**
     * Sets the promo QR code.
     * @param promoQrCode The promo QR code to set.
     */
    public void setPromoQrCode(String promoQrCode) {this.promoQrCode = promoQrCode;}

    /**
     * Sets the geolocation.
     * @param geoLocation The geolocation to set.
     */
    public void setGeoLocation(String geoLocation) {this.geoLocation = geoLocation;}

    /**
     * Sets whether the event is selected.
     * @param selected The value to set.
     */
    public void setSelected(boolean selected) {this.selected = selected;}

    /**
     * Gets the time as a formatted string.
     * @return The formatted time string.
     */
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

    /**
     * Checks if two events are equal based on their IDs.
     * @param compare The object to compare.
     * @return True if the events are equal, false otherwise.
     */
    @Override
    public boolean equals(Object compare) {
        if (compare == null || getClass() != compare.getClass()) return false;
        Event event = (Event) compare;
        return Objects.equals(eventID, event.eventID);
    }
}
