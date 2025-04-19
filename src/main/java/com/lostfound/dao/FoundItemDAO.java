package com.lostfound.dao;

// Data Access Object for managing found item records in the database.
import com.lostfound.model.FoundItem;
import com.lostfound.config.DBConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoundItemDAO {
    private static final Logger LOGGER = Logger.getLogger(FoundItemDAO.class.getName());

    public FoundItemDAO() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            LOGGER.log(Level.INFO, "FoundItemDAO initialized successfully");
        }
    }

    public FoundItem getFoundItemById(int itemId) throws SQLException {
        String sql = "SELECT Item_ID, Found_Location, Found_Date, Storage_Location, Additional_Details, Image_Path FROM found_item WHERE Item_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FoundItem foundItem = new FoundItem(
                            rs.getInt("Item_ID"),
                            rs.getString("Found_Location"),
                            rs.getDate("Found_Date"),
                            rs.getString("Storage_Location"),
                            rs.getString("Additional_Details"),
                            rs.getString("Image_Path")
                    );
                    LOGGER.log(Level.INFO, "Retrieved found item ID: {0}", itemId);
                    return foundItem;
                }
                LOGGER.log(Level.INFO, "No found item found with ID: {0}", itemId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving found item ID: {0}", itemId);
            throw e;
        }
    }
}