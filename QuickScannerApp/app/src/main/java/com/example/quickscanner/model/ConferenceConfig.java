package com.example.quickscanner.model;

/**
 * Represents the configuration for a conference.
 */
public class ConferenceConfig {

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

    /**
     * Gets the minimum hour of the conference.
     * @return The minimum hour of the conference.
     */
    public int getMinHour() {
        return minHour;
    }

    /**
     * Sets the minimum hour of the conference.
     * @param minHour The minimum hour to set.
     */
    public void setMinHour(int minHour) {
        this.minHour = minHour;
    }

    /**
     * Gets the minimum minute of the conference.
     * @return The minimum minute of the conference.
     */
    public int getMinMinute() {
        return minMinute;
    }

    /**
     * Sets the minimum minute of the conference.
     * @param minMinute The minimum minute to set.
     */
    public void setMinMinute(int minMinute) {
        this.minMinute = minMinute;
    }

    /**
     * Gets the minimum year of the conference.
     * @return The minimum year of the conference.
     */
    public int getMinYear() {
        return minYear;
    }

    /**
     * Sets the minimum year of the conference.
     * @param minYear The minimum year to set.
     */
    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    /**
     * Gets the minimum month of the conference.
     * @return The minimum month of the conference.
     */
    public int getMinMonth() {
        return minMonth;
    }

    /**
     * Sets the minimum month of the conference.
     * @param minMonth The minimum month to set.
     */
    public void setMinMonth(int minMonth) {
        this.minMonth = minMonth;
    }

    /**
     * Gets the minimum day of the conference.
     * @return The minimum day of the conference.
     */
    public int getMinDay() {
        return minDay;
    }

    /**
     * Sets the minimum day of the conference.
     * @param minDay The minimum day to set.
     */
    public void setMinDay(int minDay) {
        this.minDay = minDay;
    }

    /**
     * Gets the maximum hour of the conference.
     * @return The maximum hour of the conference.
     */
    public int getMaxHour() {
        return maxHour;
    }

    /**
     * Sets the maximum hour of the conference.
     * @param maxHour The maximum hour to set.
     */
    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    /**
     * Gets the maximum minute of the conference.
     * @return The maximum minute of the conference.
     */
    public int getMaxMinute() {
        return maxMinute;
    }

    /**
     * Sets the maximum minute of the conference.
     * @param maxMinute The maximum minute to set.
     */
    public void setMaxMinute(int maxMinute) {
        this.maxMinute = maxMinute;
    }

    /**
     * Gets the maximum year of the conference.
     * @return The maximum year of the conference.
     */
    public int getMaxYear() {
        return maxYear;
    }

    /**
     * Sets the maximum year of the conference.
     * @param maxYear The maximum year to set.
     */
    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    /**
     * Gets the maximum month of the conference.
     * @return The maximum month of the conference.
     */
    public int getMaxMonth() {
        return maxMonth;
    }

    /**
     * Sets the maximum month of the conference.
     * @param maxMonth The maximum month to set.
     */
    public void setMaxMonth(int maxMonth) {
        this.maxMonth = maxMonth;
    }

    /**
     * Gets the maximum day of the conference.
     * @return The maximum day of the conference.
     */
    public int getMaxDay() {
        return maxDay;
    }

    /**
     * Sets the maximum day of the conference.
     * @param maxDay The maximum day to set.
     */
    public void setMaxDay(int maxDay) {
        this.maxDay = maxDay;
    }

    /**
     * Gets the time zone of the conference.
     * @return The time zone of the conference.
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone of the conference.
     * @param timeZone The time zone to set.
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
