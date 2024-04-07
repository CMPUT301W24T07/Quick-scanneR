package com.example.quickscanner.model;

public class Image {
    private String imageUrl;

    public boolean isSelected;
    private String source;

    public Image() {
    }

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}