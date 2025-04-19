package com.lostfound.dao;

// Data Access Object for managing admin records in the database.
import com.lostfound.config.DBConnection;
import com.lostfound.model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDAO {
    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());

    public boolean addAdmin(Admin admin) throws SQLException {
        LOGGER.log(Level.INFO, "Adding admin for userId: {0}", admin.getUserId());
        String userCheckQuery = "SELECT User_ID FROM user WHERE User_ID = ?";
        String adminCheckQuery = "SELECT User_ID FROM admin WHERE User_ID = ?";
        String insertQuery = "INSERT INTO admin (User_ID, Admin_Role) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement userCheckStmt = connection.prepareStatement(userCheckQuery)) {
                userCheckStmt.setInt(1, admin.getUserId());
                try (ResultSet rs = userCheckStmt.executeQuery()) {
                    if (!rs.next()) {
                        LOGGER.log(Level.WARNING, "User_ID {0} does not exist in user table", admin.getUserId());
                        throw new SQLException("User_ID " + admin.getUserId() + " does not exist");
                    }
                }
            }
            try (PreparedStatement adminCheckStmt = connection.prepareStatement(adminCheckQuery)) {
                adminCheckStmt.setInt(1, admin.getUserId());
                try (ResultSet rs = adminCheckStmt.executeQuery()) {
                    if (rs.next()) {
                        LOGGER.log(Level.WARNING, "User_ID {0} already exists in admin table", admin.getUserId());
                        throw new SQLException("User_ID " + admin.getUserId() + " already exists in admin table");
                    }
                }
            }
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, admin.getUserId());
                insertStmt.setString(2, admin.getAdminRole() != null ? admin.getAdminRole() : "Moderator");
                int rowsAffected = insertStmt.executeUpdate();
                LOGGER.log(Level.INFO, "Added admin for userId: {0}, rows affected: {1}",
                        new Object[]{admin.getUserId(), rowsAffected});
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding admin for userId: {0}", admin.getUserId());
            throw e;
        }
    }

    public Admin getAdminById(int adminId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching admin with adminId: {0}", adminId);
        String query = "SELECT Admin_ID, User_ID, Admin_Role FROM admin WHERE Admin_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, adminId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Admin admin = new Admin(
                            resultSet.getInt("Admin_ID"),
                            resultSet.getInt("User_ID"),
                            resultSet.getString("Admin_Role")
                    );
                    LOGGER.log(Level.INFO, "Fetched admin with adminId: {0}", adminId);
                    return admin;
                }
            }
            LOGGER.log(Level.INFO, "No admin found for adminId: {0}", adminId);
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching admin with adminId: {0}", adminId);
            throw e;
        }
    }

    public List<Admin> getAllAdmins() throws SQLException {
        LOGGER.log(Level.INFO, "Fetching all admins");
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT Admin_ID, User_ID, Admin_Role FROM admin";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                admins.add(new Admin(
                        resultSet.getInt("Admin_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("Admin_Role")
                ));
            }
            LOGGER.log(Level.INFO, "Fetched {0} admins", admins.size());
            return admins;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all admins", e);
            throw e;
        }
    }

    public boolean updateAdmin(Admin admin) throws SQLException {
        LOGGER.log(Level.INFO, "Updating admin with adminId: {0}", admin.getAdminId());
        String query = "UPDATE admin SET User_ID = ?, Admin_Role = ? WHERE Admin_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, admin.getUserId());
            statement.setString(2, admin.getAdminRole() != null ? admin.getAdminRole() : "Moderator");
            statement.setInt(3, admin.getAdminId());
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Updated admin with adminId: {0}, rows affected: {1}",
                    new Object[]{admin.getAdminId(), rowsAffected});
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating admin with adminId: {0}", admin.getAdminId());
            throw e;
        }
    }

    public boolean deleteAdmin(int adminId) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting admin with adminId: {0}", adminId);
        String query = "DELETE FROM admin WHERE Admin_ID = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, adminId);
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Deleted admin with adminId: {0}, rows affected: {1}",
                    new Object[]{adminId, rowsAffected});
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting admin with adminId: {0}", adminId);
            throw e;
        }
    }
}