package com.lostfound.model;

// Model class for report details entities.
import java.sql.Timestamp;

public class ReportDetails {
    private int reportId;
    private String itemName;
    private String userName;
    private String userContact;
    private Timestamp reportDate;
    private String status;
    private String location;

    public ReportDetails(int reportId, String itemName, String userName, String userContact,
                         Timestamp reportDate, String status, String location) {
        this.reportId = reportId;
        this.itemName = itemName;
        this.userName = userName;
        this.userContact = userContact;
        this.reportDate = reportDate;
        this.status = status;
        this.location = location;
    }

    public int getReportId() {
        return reportId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public Timestamp getReportDate() {
        return reportDate;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }
}