/**
 * The JavaDoc comments in this code were generated with the assistance of GitHub Copilot.
 */

package com.example.quickscanner.model;

import com.example.quickscanner.controller.FirebaseImageController;

/**
 * Represents a User's profile with name, email, website, and image URL.
 */
public class Profile {
    private String name;
    private String email;
    private String website;
    private String imageUrl;
    private FirebaseImageController fbImageController;

    /**
     * Creates a Profile with specified name, email, website, and image URL.
     *
     * @param name     The user's name.
     * @param email    The user's email.
     * @param website  The user's website.
     * @param imageUrl The user's profile image URL.
     */
    public Profile(String name, String email, String website, String imageUrl) {
        this.name = name;
        this.email = email;
        this.website = website;
        this.imageUrl = imageUrl;
    }

    /**
     * Creates a Profile with default values.
     */
    public Profile() {
        this.name = "";
        this.email = "";
        this.website = "";
        this.imageUrl = "default.jpeg";
    }

    public String genereteProfilePicture(String userID) {
        int sum = 0;
        int pfpCount = 10;
        for(int i = 0; i < userID.length(); i++) {
            sum += (int)userID.charAt(i);
        }
        return String.valueOf(sum%pfpCount) + ".png";
    }

    /**
     * Returns the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's email.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The new email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's website.
     *
     * @return The user's website.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the user's website.
     *
     * @param website The new website.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Returns the user's profile image URL.
     *
     * @return The user's profile image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the user's profile image URL.
     *
     * @param imageUrl The new profile image URL.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}