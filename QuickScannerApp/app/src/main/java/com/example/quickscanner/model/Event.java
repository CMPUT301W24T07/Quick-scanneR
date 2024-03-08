package com.example.quickscanner.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Parcelable {
    public String name;
    public String description;
    public String imagePath;
    public User organizer;

    public String time;
    public String location;
    public String eventID;
    public String organizerID;

    public ArrayList<String> signUps = new ArrayList<>();
    public ArrayList<String> checkIns = new ArrayList<>();



    public Event(String name, String description, User organizer) {
        this.name = name;
        this.description = description;
        this.organizer = organizer;
        imagePath = "default.jpeg";
    }

    public Event(String name, String description, String imagePath, User organizer, String time, String location) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.organizer = organizer;
        this.time = time;
        this.location = location;
    }

    public Event() {
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imagePath);
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

    public ArrayList<String> getSignUps() {return signUps;}

    public ArrayList<String> getCheckIns() {return checkIns;}

    // Setters
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}
    public void setOrganizer(User organizer) {this.organizer = organizer;}
    public void setTime(String time) {this.time = time;}
    public void setLocation(String location) {this.location = location;}
    public void setEventID(String eventID) {this.eventID = eventID;}
    public void setOrganizerID(String organizerID) {this.organizerID = organizerID;}

    public void setSignUps(ArrayList<String> signUps) {this.signUps = signUps;}

    public void setCheckIns(ArrayList<String> checkIns) {this.checkIns = checkIns;}

}
