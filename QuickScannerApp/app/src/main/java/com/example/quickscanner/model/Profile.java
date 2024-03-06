package com.example.quickscanner.model;

public class Profile {
    private String name;
    private String email;
    private String website;
    private String imageUrl;

    public Profile(String name, String email, String website, String imageUrl) {
        this.name = name;
        this.email = email;
        this.website = website;
        this.imageUrl = imageUrl;
    }

    public Profile() {
        this.name = "";
        this.email = "";
        this.website = "";
        this.imageUrl = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
