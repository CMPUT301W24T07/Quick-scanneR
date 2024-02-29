package com.example.quickscanner.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    public String name;
    public String description;
    public String imagePath;

    public Event(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
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
}
