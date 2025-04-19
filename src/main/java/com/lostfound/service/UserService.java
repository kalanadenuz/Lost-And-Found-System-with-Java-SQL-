package com.lostfound.service;

// Service layer for managing user-related operations.
import com.lostfound.dao.UserDAO;
import com.lostfound.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
        LOGGER.log(Level.INFO, "UserService initialized");
    }

    public User login(String email, String password) throws SQLException {
        LOGGER.log(Level.INFO, "Attempting login for email: {0}", email);
        User user = userDAO.authenticate(email, password);
        if (user != null) {
            User.setLoggedInUser(user);
            LOGGER.log(Level.INFO, "Login successful for userId: {0}", user.getUserId());
            return user;
        }
        LOGGER.log(Level.WARNING, "Login failed for email: {0}", email);
        return null;
    }

    public void logout() {
        LOGGER.log(Level.INFO, "Logging out user");
        User.logout();
    }

    public User getCurrentUser() {
        User user = User.getLoggedInUser();
        LOGGER.log(Level.INFO, "Fetching current user: {0}", user != null ? user.getUserId() : "none");
        return user;
    }

    public User getUserById(int userId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching user by userId: {0}", userId);
        return userDAO.getUserById(userId);
    }

    public List<User> getAllUsers() throws SQLException {
        LOGGER.log(Level.INFO, "Fetching all users");
        List<User> users = userDAO.getAllUsers();
        LOGGER.log(Level.INFO, "Fetched {0} users", users.size());
        return users;
    }

    public boolean updateUser(User user) throws SQLException {
        LOGGER.log(Level.INFO, "Updating user with userId: {0}", user.getUserId());
        boolean success = userDAO.updateUser(user);
        LOGGER.log(Level.INFO, "Update {0} for userId: {1}",
                new Object[]{success ? "successful" : "failed", user.getUserId()});
        return success;
    }

    public boolean deleteUser(int userId) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting user with userId: {0}", userId);
        boolean success = userDAO.deleteUser(userId);
        LOGGER.log(Level.INFO, "Deletion {0} for userId: {1}",
                new Object[]{success ? "successful" : "failed", userId});
        return success;
    }

    public boolean registerUserWithRole(String name, String email, String password, String role, String contact) throws SQLException {
        LOGGER.log(Level.INFO, "Registering user with email: {0}", email);
        boolean success = userDAO.createUserWithRole(name, email, password, role, contact);
        LOGGER.log(Level.INFO, "Registration {0} for email: {1}",
                new Object[]{success ? "successful" : "failed", email});
        return success;
    }

    public void updateUserRole(int userId, String newRole) throws SQLException {
        if (!User.isLoggedIn() || !"Admin".equalsIgnoreCase(User.getLoggedInUser().getRole())) {
            LOGGER.log(Level.WARNING, "Unauthorized attempt to update role for userId: {0}", userId);
            throw new SecurityException("Admin privileges required");
        }
        LOGGER.log(Level.INFO, "Updating role for userId: {0} to {1}", new Object[]{userId, newRole});
        userDAO.updateUserRole(userId, newRole);
    }

    public boolean isAdmin() {
        User current = User.getLoggedInUser();
        boolean isAdmin = current != null && "Admin".equalsIgnoreCase(current.getRole());
        LOGGER.log(Level.INFO, "Checking if current user is admin: {0}", isAdmin);
        return isAdmin;
    }

    public User findUserByEmail(String email) throws SQLException {
        LOGGER.log(Level.INFO, "Finding user by email: {0}", email);
        return userDAO.findByEmail(email);
    }
}