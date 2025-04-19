package com.lostfound.model;

// Model class for admin entities.
public class Admin {
    private int adminId;
    private int userId;
    private String adminRole;

    public Admin(int adminId, int userId, String adminRole) {
        this.adminId = adminId;
        this.userId = userId;
        this.adminRole = adminRole;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }
}