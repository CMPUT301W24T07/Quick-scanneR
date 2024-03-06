package com.example.quickscanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Event implements Parcelable
{
    public String name;
    public String description;
    public String imagePath;
    private String organizerUid;
    private String eventId;
    private ArrayList<Event> checkedInEvents;
    private ArrayList<Event> SignedUpEvents;

    public Event(String name, String description, String imagePath, String organizerUid, String eventId, ArrayList<Event> checkedInEvents, ArrayList<Event> SignedUpEvents) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.organizerUid = organizerUid;
        this.eventId = eventId;
        this.checkedInEvents = checkedInEvents;
        this.SignedUpEvents = SignedUpEvents;

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

    public Event()
    {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imagePath);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getOrganizerUid()
    {
        return organizerUid;
    }

    public void setOrganizerUid(String organizerUid)
    {
        this.organizerUid = organizerUid;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public ArrayList<Event> getCheckedInEvents()
    {
        return checkedInEvents;
    }

    public void setCheckedInEvents(ArrayList<Event> checkedInEvents)
    {
        this.checkedInEvents = checkedInEvents;
    }

    public ArrayList<Event> getSignedUpEvents()
    {
        return SignedUpEvents;
    }

    public void setSignedUpEvents(ArrayList<Event> signedUpEvents)
    {
        SignedUpEvents = signedUpEvents;
    }
}
