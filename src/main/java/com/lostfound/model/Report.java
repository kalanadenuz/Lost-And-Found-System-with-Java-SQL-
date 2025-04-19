package com.lostfound.model;

// Model class for report entities.
import java.sql.Timestamp;

public class Report {
    private int reportId;
    private int userId;
    private int itemId;
    private String reportType;
    private Timestamp reportDate;

    public Report(int reportId, int userId, int itemId, String reportType, Timestamp reportDate) {
        this.reportId = reportId;
        this.userId = userId;
        this.itemId = itemId;
        this.reportType = reportType;
        this.reportDate = reportDate;
    }

    public int getReportId() {
        return reportId;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getReportType() {
        return reportType;
    }

    public Timestamp getReportDate() {
        return reportDate;
    }
}