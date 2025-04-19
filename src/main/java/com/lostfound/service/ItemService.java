package com.lostfound.service;

// Service layer for managing item operations.
import com.lostfound.config.DBConnection;
import com.lostfound.model.Item;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());

    public ItemService() throws SQLException {
        LOGGER.log(Level.INFO, "Initializing ItemService");
    }

    public int createItem(Item item) throws SQLException {
        LOGGER.log(Level.INFO, "Creating item: {0}", item.getName());
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO item (Name, Description, Category, User_ID, Status, Date) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getDescription());
                stmt.setString(3, item.getCategory());
                stmt.setInt(4, item.getUserId());
                stmt.setString(5, item.getStatus());
                stmt.setTimestamp(6, item.getDate());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int itemId = rs.getInt(1);
                            LOGGER.log(Level.INFO, "Created item with ID: {0}", itemId);
                            return itemId;
                        }
                    }
                }
                LOGGER.log(Level.WARNING, "Failed to retrieve item ID");
                return -1;
            }
        }
    }

    public Item getItemById(int itemId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching item with ID: {0}", itemId);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT Item_ID, Name, Description, Category, User_ID, Status, Date FROM item WHERE Item_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Item(
                                rs.getInt("Item_ID"),
                                rs.getString("Name"),
                                rs.getString("Description"),
                                rs.getString("Category"),
                                rs.getInt("User_ID"),
                                rs.getString("Status"),
                                rs.getTimestamp("Date")
                        );
                    }
                }
            }
        }
        LOGGER.log(Level.WARNING, "Item not found for ID: {0}", itemId);
        return null;
    }
}