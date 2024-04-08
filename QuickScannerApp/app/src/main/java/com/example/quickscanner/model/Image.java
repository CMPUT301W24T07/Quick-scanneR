package com.example.quickscanner.model;

/**
 * Represents an image.
 */
public class Image {
    private String imageUrl;
    public boolean isSelected;
    private String source;

    /**
     * Default constructor for Image.
     */
    public Image() {
    }

    /**
     * Constructor for Image with imageUrl.
     * @param imageUrl The URL of the image.
     */
    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the URL of the image.
     * @return The URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image.
     * @param imageUrl The URL to set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Checks if the image is selected.
     * @return True if the image is selected, false otherwise.
     */
    public boolean isSelected() { return isSelected; }

    /**
     * Sets whether the image is selected.
     * @param selected The value to set.
     */
    public void setSelected(boolean selected) { isSelected = selected; }

    /**
     * Gets the source of the image.
     * @return The source of the image.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source of the image.
     * @param source The source to set.
     */
    public void setSource(String source) {
        this.source = source;
    }
}
