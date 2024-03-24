package com.example.quickscanner.singletons;


/*
* Description:
*   A Singleton class representing our App Settings.
*   These settings can be adjusted within the app under the
*   'Settings' menu option.
*
*  Usage : String hashedGeolocation = SettingsDataSingleton.getInstance().getHashedGeoLocation();
*         - This usage grabs the device's hashed geolocation
*
 * Credits: Hindle Design Pattern notes and https://stackoverflow.com/questions/16517702/singleton-in-android
 */
public class SettingsDataSingleton {
    private static SettingsDataSingleton instance;
    private static String hashedGeoLocation = "hello";


    private SettingsDataSingleton(){
        // empty constructor
    }

    /*
    * Only use once in MainActivity to initialize this Singleton
    * Using it again does nothing
     */
    public static void initInstance(){
        if (instance == null)
            instance = new SettingsDataSingleton();
    }

    /*
    * Returns our singleton class
     */
    public static SettingsDataSingleton getInstance(){
        return instance;
    }

    /*
     * Returns the device's hashed geolocation
     */
    public String getHashedGeoLocation() {
        return hashedGeoLocation;
    }

    /*
     * Sets the device's hashed geolocation
     */
    public void setHashedGeoLocation(String hashedGeoLocation) {
        SettingsDataSingleton.hashedGeoLocation = hashedGeoLocation;
    }

}
