package com.example.quickscanner.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.model.ConferenceConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ConferenceConfigSingleton
{
    private static ConferenceConfigSingleton instance;
    private FirebaseEventController fbEventController;

    private int minHour;
    private int minMinute;
    private int minYear;
    private int minMonth;
    private int minDay;
    private int maxHour;
    private int maxMinute;
    private int maxYear;
    private int maxMonth;
    private int maxDay;
    private String timeZone;
    private OnConfigFetchedListener onConfigFetchedListener;


    public interface OnConfigFetchedListener {
        void onConfigFetched();
    }
    public void setOnConfigFetchedListener(OnConfigFetchedListener onConfigFetchedListener) {
        this.onConfigFetchedListener = onConfigFetchedListener;
    }


    private ConferenceConfigSingleton() {
        fbEventController = new FirebaseEventController();
        fbEventController.getConferenceConfig().addOnSuccessListener(config -> {
            // Fill the ConferenceConfigSingleton instance with data
            setMinHour(config.getMinHour());
            setMinMinute(config.getMinMinute());
            setMinYear(config.getMinYear());
            setMinMonth(config.getMinMonth());
            setMinDay(config.getMinDay());
            setMaxHour(config.getMaxHour());
            setMaxMinute(config.getMaxMinute());
            setMaxYear(config.getMaxYear());
            setMaxMonth(config.getMaxMonth());
            setMaxDay(config.getMaxDay());
            setTimeZone(config.getTimeZone());
            if (onConfigFetchedListener != null)
            {
                onConfigFetchedListener.onConfigFetched();
            }

        }).addOnFailureListener(e -> Log.e("ConferenceConfigSingleton", "Error getting ConferenceConfig", e));
    }
    public static void initInstance() {
        if (instance == null) {
            instance = new ConferenceConfigSingleton();
        }
    }
    public static ConferenceConfigSingleton getInstance() {return instance;}
    public int getMinHour()
    {
        return minHour;
    }

    public void setMinHour(int minHour)
    {
        this.minHour = minHour;
    }

    public int getMinMinute()
    {
        return minMinute;
    }

    public void setMinMinute(int minMinute)
    {
        this.minMinute = minMinute;
    }

    public int getMinYear()
    {
        return minYear;
    }

    public void setMinYear(int minYear)
    {
        this.minYear = minYear;
    }

    public int getMinMonth()
    {
        return minMonth;
    }

    public void setMinMonth(int minMonth)
    {
        this.minMonth = minMonth;
    }

    public int getMinDay()
    {
        return minDay;
    }

    public void setMinDay(int minDay)
    {
        this.minDay = minDay;
    }

    public int getMaxHour()
    {
        return maxHour;
    }

    public void setMaxHour(int maxHour)
    {
        this.maxHour = maxHour;
    }

    public int getMaxMinute()
    {
        return maxMinute;
    }

    public void setMaxMinute(int maxMinute)
    {
        this.maxMinute = maxMinute;
    }

    public int getMaxYear()
    {
        return maxYear;
    }

    public void setMaxYear(int maxYear)
    {
        this.maxYear = maxYear;
    }

    public int getMaxMonth()
    {
        return maxMonth;
    }

    public void setMaxMonth(int maxMonth)
    {
        this.maxMonth = maxMonth;
    }

    public int getMaxDay()
    {
        return maxDay;
    }

    public void setMaxDay(int maxDay)
    {
        this.maxDay = maxDay;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }
}
