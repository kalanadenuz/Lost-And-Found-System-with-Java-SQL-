package com.lostfound.dao;

// Data Access Object for managing lost item records in the database.
import com.lostfound.model.LostItem;
import com.lostfound.config.DBConnection;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LostItemDAO {
    private static final Logger LOGGER = Logger.getLogger(LostItemDAO.class.getName());

    public LostItemDAO() throws SQLException {
        LOGGER.log(Level.INFO, "Initializing LostItemDAO");
        try (Connection conn = DBConnection.getConnection()) {
            LOGGER.log(Level.INFO, "Database connection established successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw e;
        }
    }

    public boolean createLostItem(LostItem lostItem) throws SQLException {
        LOGGER.log(Level.INFO, "Inserting lost item for itemId: {0}", lostItem.getItemId());
        String sql = "INSERT INTO lost_item (Item_ID, Last_Seen_Location, Last_Seen_Date, Additional_Details, Image_Path) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lostItem.getItemId());
            stmt.setString(2, lostItem.getLastSeenLocation());
            stmt.setDate(3, lostItem.getLastSeenDate());
            stmt.setString(4, lostItem.getAdditionalDetails());
            stmt.setString(5, lostItem.getImagePath());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Rows affected: {0} for itemId: {1}", new Object[]{rowsAffected, lostItem.getItemId()});
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting lost item for itemId: " + lostItem.getItemId(), e);
            throw e;
        }
    }

    public LostItem getLostItemById(int itemId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching lost item for itemId: {0}", itemId);
        String sql = "SELECT * FROM lost_item WHERE Item_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new LostItem(
                            rs.getInt("Item_ID"),
                            rs.getString("Last_Seen_Location"),
                            rs.getDate("Last_Seen_Date"),
                            rs.getString("Additional_Details"),
                            rs.getString("Image_Path")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lost item for itemId: " + itemId, e);
            throw e;
        }
        LOGGER.log(Level.WARNING, "No lost item found for itemId: {0}", itemId);
        return null;
    }
}