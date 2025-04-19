package com.lostfound.model;

// Model class for found item entities.
import java.sql.Date;

public class FoundItem {
    private int itemId;
    private String foundLocation;
    private Date foundDate;
    private String storageLocation;
    private String additionalDetails;
    private String imagePath;

    public FoundItem(int itemId, String foundLocation, Date foundDate, String storageLocation, String additionalDetails, String imagePath) {
        this.itemId = itemId;
        this.foundLocation = foundLocation;
        this.foundDate = foundDate;
        this.storageLocation = storageLocation;
        this.additionalDetails = additionalDetails;
        this.imagePath = imagePath;
    }

    public int getItemId() {
        return itemId;
    }

    public String getFoundLocation() {
        return foundLocation;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public String getImagePath() {
        return imagePath;
    }
}