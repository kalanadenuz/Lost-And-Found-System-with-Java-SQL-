package com.lostfound.service;

// Service layer for managing found item operations.
import com.lostfound.config.DBConnection;
import com.lostfound.model.FoundItem;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FoundItemService {
    private static final Logger LOGGER = Logger.getLogger(FoundItemService.class.getName());

    public FoundItemService() throws SQLException {
        LOGGER.log(Level.INFO, "Initializing FoundItemService");
    }

    public boolean createFoundItem(FoundItem foundItem) throws SQLException {
        LOGGER.log(Level.INFO, "Creating found item for itemId: {0}", foundItem.getItemId());
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO found_item (Item_ID, Found_Location, Found_Date, Storage_Location, Additional_Details, Image_Path) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, foundItem.getItemId());
                stmt.setString(2, foundItem.getFoundLocation());
                stmt.setDate(3, foundItem.getFoundDate());
                stmt.setString(4, foundItem.getStorageLocation());
                stmt.setString(5, foundItem.getAdditionalDetails());
                stmt.setString(6, foundItem.getImagePath());
                int rows = stmt.executeUpdate();
                LOGGER.log(Level.INFO, "Inserted found item, rows affected: {0}", rows);
                return rows > 0;
            }
        }
    }

    public FoundItem getFoundItemDetails(int itemId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching found item details for itemId: {0}", itemId);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM found_item WHERE Item_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new FoundItem(
                                rs.getInt("Item_ID"),
                                rs.getString("Found_Location"),
                                rs.getDate("Found_Date"),
                                rs.getString("Storage_Location"),
                                rs.getString("Additional_Details"),
                                rs.getString("Image_Path")
                        );
                    }
                }
            }
        }
        LOGGER.log(Level.WARNING, "Found item not found for itemId: {0}", itemId);
        return null;
    }
}