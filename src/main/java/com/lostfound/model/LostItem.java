package com.lostfound.model;

// Model class for lost item entities.
import java.sql.Date;

public class LostItem {
    private int itemId;
    private String lastSeenLocation;
    private Date lastSeenDate;
    private String additionalDetails;
    private String imagePath;

    public LostItem(int itemId, String lastSeenLocation, Date lastSeenDate, String additionalDetails, String imagePath) {
        this.itemId = itemId;
        this.lastSeenLocation = lastSeenLocation;
        this.lastSeenDate = lastSeenDate;
        this.additionalDetails = additionalDetails;
        this.imagePath = imagePath;
    }

    public int getItemId() {
        return itemId;
    }

    public String getLastSeenLocation() {
        return lastSeenLocation;
    }

    public Date getLastSeenDate() {
        return lastSeenDate;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public String getImagePath() {
        return imagePath;
    }
}