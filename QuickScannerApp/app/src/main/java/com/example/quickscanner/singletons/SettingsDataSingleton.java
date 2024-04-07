package com.example.quickscanner.singletons;


/*
* Description:
*   A Singleton class representing our App Settings.
*   These settings can be adjusted within the app under the
*   'Settings' menu option.
*
*  Usage : String hashedGeolocation = SettingsDataSingleton.getInstance().getHashedGeoLocation();
*         - This usage grabs the device's hashed geolocation as a String
*         - Note: This usage returns NULL if device permissions are disabled
*                 If permissions are enabled, returns hashed geolocation as a String
*
 * Credits: Hindle Design Pattern notes and https://stackoverflow.com/questions/16517702/singleton-in-android
 */
public class SettingsDataSingleton {
    private static SettingsDataSingleton instance;
    private static String hashedGeoLocation;


    private SettingsDataSingleton(){
        // empty constructor
    }

    /*
    * Only use once in MainActivity to initialize this Singleton
    * Using it again does nothing
    * Usage: SettingsDataSingleton.initInstance();
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
     *   This functions returns a hashed (String) version of geolocation
     *   If User or Phone has permissions disabled, returns NULL.
     *   If Both Permissions are enabled, returns a hashed String.
     */
    public static String getHashedGeoLocation() {
        return hashedGeoLocation;
    }

    /*
     * Sets the device's hashed geolocation
     */
    public static void setHashedGeoLocation(String hashedGeoLocation) {
        SettingsDataSingleton.hashedGeoLocation = hashedGeoLocation;
    }

}
