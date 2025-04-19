package com.lostfound.service;

// Service layer for managing admin-related operations.
import com.lostfound.config.DBConnection;
import com.lostfound.dao.AdminDAO;
import com.lostfound.model.Admin;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());
    private AdminDAO adminDAO;

    public AdminService() {
        adminDAO = new AdminDAO();
        LOGGER.log(Level.INFO, "AdminService initialized successfully");
    }

    public boolean addAdmin(Admin admin) throws SQLException {
        LOGGER.log(Level.INFO, "Adding admin for userId: {0}", admin.getUserId());
        return adminDAO.addAdmin(admin);
    }

    public boolean deleteAdmin(int adminId) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting admin with adminId: {0}", adminId);
        return adminDAO.deleteAdmin(adminId);
    }

    public List<Admin> getAllAdmins() throws SQLException {
        LOGGER.log(Level.INFO, "Fetching all admins");
        return adminDAO.getAllAdmins();
    }

    public void updateReportType(int reportId, String reportType) throws SQLException {
        try (var conn = DBConnection.getConnection()) {
            String sql = "UPDATE reports SET report_type = ? WHERE Report_ID = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, reportType);
                stmt.setInt(2, reportId);
                int rowsAffected = stmt.executeUpdate();
                LOGGER.log(Level.INFO, "Updated report ID: {0} to report_type: {1}, Rows affected: {2}",
                        new Object[]{reportId, reportType, rowsAffected});
            }
        }
    }
}