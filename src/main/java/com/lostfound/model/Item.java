package com.lostfound.model;

// Model class for item entities.
import java.sql.Timestamp;

public class Item {
    private int itemId;
    private String name;
    private String description;
    private String category;
    private int userId;
    private String status;
    private Timestamp date;

    public Item(int itemId, String name, String description, String category, int userId, String status, Timestamp date) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.userId = userId;
        this.status = status;
        this.date = date;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
}