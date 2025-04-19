package com.lostfound.dao;

// Data Access Object for managing item records in the database.
import com.lostfound.model.Item;
import com.lostfound.config.DBConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDAO {
    private static final Logger LOGGER = Logger.getLogger(ItemDAO.class.getName());

    public ItemDAO() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            LOGGER.log(Level.INFO, "ItemDAO initialized successfully");
        }
    }

    public Item getItemById(int itemId) throws SQLException {
        String sql = "SELECT Item_ID, Name, Description, Category, User_ID, Status, Date FROM item WHERE Item_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item(
                            rs.getInt("Item_ID"),
                            rs.getString("Name"),
                            rs.getString("Description"),
                            rs.getString("Category"),
                            rs.getInt("User_ID"),
                            rs.getString("Status"),
                            rs.getTimestamp("Date")
                    );
                    LOGGER.log(Level.INFO, "Retrieved item ID: {0}", itemId);
                    return item;
                }
                LOGGER.log(Level.INFO, "No item found with ID: {0}", itemId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving item ID: {0}", itemId);
            throw e;
        }
    }
}