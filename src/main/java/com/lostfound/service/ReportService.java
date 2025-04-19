package com.lostfound.service;

// Service layer for managing report operations.
import com.lostfound.config.DBConnection;
import com.lostfound.model.Report;
import com.lostfound.model.ReportDetails;
import com.lostfound.model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportService {
    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    public ReportService() throws SQLException {
        LOGGER.log(Level.INFO, "ReportService initialized");
    }

    public List<ReportDetails> getAllReportsWithDetails() throws SQLException {
        List<ReportDetails> reports = new ArrayList<>();
        String query = """
            SELECT 
                r.report_id,
                i.Name AS item_name,
                u.name AS user_name,
                u.contact AS user_contact,
                r.report_date,
                r.report_type AS status,
                COALESCE(l.last_seen_location, f.found_location) AS location
            FROM lostfounddb.reports r
            JOIN item i ON r.item_id = i.item_id
            JOIN user u ON r.user_id = u.user_id
            LEFT JOIN lost_item l ON r.item_id = l.item_id AND r.report_type = 'lost'
            LEFT JOIN found_item f ON r.item_id = f.item_id AND r.report_type = 'found'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ReportDetails report = new ReportDetails(
                        rs.getInt("report_id"),
                        rs.getString("item_name"),
                        rs.getString("user_name"),
                        rs.getString("user_contact"),
                        rs.getTimestamp("report_date"),
                        rs.getString("status"),
                        rs.getString("location")
                );
                reports.add(report);
                LOGGER.log(Level.FINE, "Created ReportDetails: ID={0}, ItemName={1}, Status={2}, Location={3}, UserName={4}, UserContact={5}, Date={6}",
                        new Object[]{
                                report.getReportId(),
                                report.getItemName(),
                                report.getStatus(),
                                report.getLocation(),
                                report.getUserName(),
                                report.getUserContact(),
                                report.getReportDate()
                        });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching reports with details: {0}", e.getMessage());
            throw e;
        }

        LOGGER.log(Level.INFO, "Fetched {0} reports with details", reports.size());
        return reports;
    }

    public boolean createReport(Report report) throws SQLException {
        String sql = "INSERT INTO lostfounddb.reports (user_id, item_id, report_type, report_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, report.getUserId());
            stmt.setInt(2, report.getItemId());
            stmt.setString(3, report.getReportType());
            stmt.setTimestamp(4, report.getReportDate());
            int rows = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Created report for item_id: {0}, rows affected: {1}",
                    new Object[]{report.getItemId(), rows});
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating report for item_id: {0}: {1}",
                    new Object[]{report.getItemId(), e.getMessage()});
            throw e;
        }
    }

    public List<Report> getCurrentUserReports() throws SQLException, IOException {
        UserService userService = new UserService();
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No current user found for getCurrentUserReports");
            return new ArrayList<>();
        }
        return getReportsByUserId(currentUser.getUserId());
    }

    public List<Report> getReportsByUserId(int userId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT report_id, user_id, item_id, report_type, report_date FROM lostfounddb.reports WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Report report = new Report(
                            rs.getInt("report_id"),
                            rs.getInt("user_id"),
                            rs.getInt("item_id"),
                            rs.getString("report_type"),
                            rs.getTimestamp("report_date")
                    );
                    reports.add(report);
                    LOGGER.log(Level.FINE, "Fetched report ID: {0} for user_id: {1}",
                            new Object[]{report.getReportId(), userId});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching reports for user_id: {0}: {1}",
                    new Object[]{userId, e.getMessage()});
            throw e;
        }
        LOGGER.log(Level.INFO, "Fetched {0} reports for user_id: {1}",
                new Object[]{reports.size(), userId});
        return reports;
    }

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT report_id, user_id, item_id, report_type, report_date FROM lostfounddb.reports";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Report report = new Report(
                        rs.getInt("report_id"),
                        rs.getInt("user_id"),
                        rs.getInt("item_id"),
                        rs.getString("report_type"),
                        rs.getTimestamp("report_date")
                );
                reports.add(report);
                LOGGER.log(Level.FINE, "Fetched report ID: {0}", report.getReportId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all reports: {0}", e.getMessage());
            throw e;
        }
        LOGGER.log(Level.INFO, "Fetched {0} reports", reports.size());
        return reports;
    }

    public boolean deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM lostfounddb.reports WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            int rows = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Deleted report ID: {0}, rows affected: {1}",
                    new Object[]{reportId, rows});
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting report ID: {0}: {1}",
                    new Object[]{reportId, e.getMessage()});
            throw e;
        }
    }

    public Report getReportById(int reportId) throws SQLException {
        String sql = "SELECT report_id, user_id, item_id, report_type, report_date FROM lostfounddb.reports WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Report report = new Report(
                            rs.getInt("report_id"),
                            rs.getInt("user_id"),
                            rs.getInt("item_id"),
                            rs.getString("report_type"),
                            rs.getTimestamp("report_date")
                    );
                    LOGGER.log(Level.INFO, "Fetched report ID: {0}", reportId);
                    return report;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching report ID: {0}: {1}",
                    new Object[]{reportId, e.getMessage()});
            throw e;
        }
        LOGGER.log(Level.WARNING, "No report found for ID: {0}", reportId);
        return null;
    }

    public void close() {
        LOGGER.log(Level.INFO, "ReportService closed");
    }
}