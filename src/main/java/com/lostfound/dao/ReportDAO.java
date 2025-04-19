package com.lostfound.dao;

// Data Access Object for managing report records in the database.
import com.lostfound.config.DBConnection;
import com.lostfound.model.Report;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDAO {
    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    public ReportDAO() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            LOGGER.log(Level.INFO, "ReportDAO initialized successfully");
        }
    }

    public boolean createReport(Report report) throws SQLException {
        String sql = "INSERT INTO report (User_ID, Item_ID, Report_Type, Report_Date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, report.getUserId());
            stmt.setInt(2, report.getItemId());
            stmt.setString(3, report.getReportType());
            stmt.setTimestamp(4, report.getReportDate() != null ? report.getReportDate() : new Timestamp(System.currentTimeMillis()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        LOGGER.log(Level.INFO, "Created report with ID: {0}, Item_ID: {1}",
                                new Object[]{rs.getInt(1), report.getItemId()});
                    }
                }
                return true;
            }
            LOGGER.log(Level.WARNING, "Failed to create report with Item_ID: {0}", report.getItemId());
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating report with Item_ID: {0}", report.getItemId());
            throw e;
        }
    }

    public Report getReportById(int reportId) throws SQLException {
        String sql = "SELECT Report_ID, User_ID, Item_ID, Report_Type, Report_Date FROM report WHERE Report_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Report report = new Report(
                            rs.getInt("Report_ID"),
                            rs.getInt("User_ID"),
                            rs.getInt("Item_ID"),
                            rs.getString("Report_Type"),
                            rs.getTimestamp("Report_Date")
                    );
                    LOGGER.log(Level.INFO, "Retrieved report ID: {0}", reportId);
                    return report;
                }
                LOGGER.log(Level.INFO, "No report found with ID: {0}", reportId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving report ID: {0}", reportId);
            throw e;
        }
    }

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT Report_ID, User_ID, Item_ID, Report_Type, Report_Date FROM report";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(new Report(
                        rs.getInt("Report_ID"),
                        rs.getInt("User_ID"),
                        rs.getInt("Item_ID"),
                        rs.getString("Report_Type"),
                        rs.getTimestamp("Report_Date")
                ));
            }
            LOGGER.log(Level.INFO, "Retrieved {0} reports", reports.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reports", e);
            throw e;
        }
        return reports;
    }

    public List<Report> getReportsByUserId(int userId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT Report_ID, User_ID, Item_ID, Report_Type, Report_Date FROM report WHERE User_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(new Report(
                            rs.getInt("Report_ID"),
                            rs.getInt("User_ID"),
                            rs.getInt("Item_ID"),
                            rs.getString("Report_Type"),
                            rs.getTimestamp("Report_Date")
                    ));
                }
                LOGGER.log(Level.INFO, "Retrieved {0} reports for user ID: {1}",
                        new Object[]{reports.size(), userId});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reports for user ID: {0}", userId);
            throw e;
        }
        return reports;
    }

    public boolean updateReport(Report report) throws SQLException {
        String sql = "UPDATE report SET User_ID = ?, Item_ID = ?, Report_Type = ?, Report_Date = ? WHERE Report_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, report.getUserId());
            stmt.setInt(2, report.getItemId());
            stmt.setString(3, report.getReportType());
            stmt.setTimestamp(4, report.getReportDate() != null ? report.getReportDate() : new Timestamp(System.currentTimeMillis()));
            stmt.setInt(5, report.getReportId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Updated report ID: {0}, Item_ID: {1}",
                        new Object[]{report.getReportId(), report.getItemId()});
                return true;
            }
            LOGGER.log(Level.WARNING, "No report found with ID: {0}", report.getReportId());
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating report ID: {0}", report.getReportId());
            throw e;
        }
    }

    public boolean deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM report WHERE Report_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reportId);
            int rowsAffected = stmt.executeUpdate();
            boolean success = rowsAffected > 0;
            if (success) {
                LOGGER.log(Level.INFO, "Deleted report ID: {0}", reportId);
            } else {
                LOGGER.log(Level.WARNING, "No report found with ID: {0}", reportId);
            }
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting report ID: {0}", reportId);
            throw e;
        }
    }
}