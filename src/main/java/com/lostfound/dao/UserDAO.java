package com.lostfound.dao;

// Data Access Object for managing user records in the database.
import com.lostfound.config.DBConnection;
import com.lostfound.model.Admin;
import com.lostfound.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private AdminDAO adminDAO = new AdminDAO();

    public User authenticate(String email, String password) throws SQLException {
        String query = "SELECT User_ID, Name, Email, Password, Role, Contact FROM user WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    if (password.equals(storedPassword)) {
                        User user = new User(
                                rs.getInt("User_ID"),
                                rs.getString("Name"),
                                rs.getString("Email"),
                                storedPassword,
                                rs.getString("Role"),
                                rs.getString("Contact")
                        );
                        LOGGER.log(Level.INFO, "Authenticated user with email: {0}", email);
                        return user;
                    }
                }
                LOGGER.log(Level.INFO, "Authentication failed for email: {0}", email);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user with email: {0}", email);
            throw e;
        }
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT User_ID, Name, Email, Password, Role, Contact FROM user WHERE User_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("User_ID"),
                            rs.getString("Name"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            rs.getString("Role"),
                            rs.getString("Contact")
                    );
                    LOGGER.log(Level.INFO, "Fetched user with userId: {0}", userId);
                    return user;
                }
            }
            LOGGER.log(Level.INFO, "No user found for userId: {0}", userId);
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user with userId: {0}", userId);
            throw e;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT User_ID, Name, Email, Password, Role, Contact FROM user";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("User_ID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("Contact")
                ));
            }
            LOGGER.log(Level.INFO, "Fetched {0} users", users.size());
            return users;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all users", e);
            throw e;
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE user SET Name = ?, Email = ?, Password = ?, Role = ?, Contact = ? WHERE User_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getContact());
            stmt.setInt(6, user.getUserId());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Updated user with userId: {0}, rows affected: {1}",
                    new Object[]{user.getUserId(), rowsAffected});
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user with userId: {0}", user.getUserId());
            throw e;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM user WHERE User_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Deleted user with userId: {0}, rows affected: {1}",
                    new Object[]{userId, rowsAffected});
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with userId: {0}", userId);
            throw e;
        }
    }

    public boolean createUserWithRole(String name, String email, String password, String role, String contact) throws SQLException {
        String userQuery = "INSERT INTO user (Name, Email, Password, Role, Contact) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, name);
                userStmt.setString(2, email);
                userStmt.setString(3, password);
                userStmt.setString(4, role != null && role.equalsIgnoreCase("Admin") ? "Admin" : "User");
                userStmt.setString(5, contact);
                int rowsAffected = userStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("User creation failed, no rows affected");
                }
                try (ResultSet rs = userStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        if ("Admin".equalsIgnoreCase(role)) {
                            Admin admin = new Admin(0, userId, "Moderator");
                            adminDAO.addAdmin(admin);
                        }
                        LOGGER.log(Level.INFO, "Created user with userId: {0}", userId);
                        conn.commit();
                        return true;
                    }
                }
                throw new SQLException("Failed to retrieve generated user ID");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
                }
            }
            LOGGER.log(Level.SEVERE, "Error creating user with email: {0}", email);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", closeEx);
                }
            }
        }
    }

    public void updateUserRole(int userId, String newRole) throws SQLException {
        String userQuery = "UPDATE user SET Role = ? WHERE User_ID = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, newRole != null && newRole.equalsIgnoreCase("Admin") ? "Admin" : "User");
                userStmt.setInt(2, userId);
                userStmt.executeUpdate();
            }
            if ("Admin".equalsIgnoreCase(newRole)) {
                Admin admin = new Admin(0, userId, "Moderator");
                adminDAO.addAdmin(admin);
            } else {
                String deleteQuery = "DELETE FROM admin WHERE User_ID = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.executeUpdate();
                }
            }
            conn.commit();
            LOGGER.log(Level.INFO, "Updated role for userId: {0} to {1}", new Object[]{userId, newRole});
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
                }
            }
            LOGGER.log(Level.SEVERE, "Error updating role for userId: {0}", userId);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", closeEx);
                }
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String query = "SELECT User_ID, Name, Email, Password, Role, Contact FROM user WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("User_ID"),
                            rs.getString("Name"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            rs.getString("Role"),
                            rs.getString("Contact")
                    );
                    LOGGER.log(Level.INFO, "Found user with email: {0}", email);
                    return user;
                }
            }
            LOGGER.log(Level.INFO, "No user found for email: {0}", email);
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user with email: {0}", email);
            throw e;
        }
    }
}