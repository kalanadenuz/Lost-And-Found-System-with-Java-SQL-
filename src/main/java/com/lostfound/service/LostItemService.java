package com.lostfound.service;

// Service layer for managing lost item operations.
import com.lostfound.dao.LostItemDAO;
import com.lostfound.model.LostItem;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LostItemService {
    private LostItemDAO lostItemDAO;
    private static final Logger LOGGER = Logger.getLogger(LostItemService.class.getName());

    public LostItemService() throws SQLException {
        LOGGER.log(Level.INFO, "Initializing LostItemService");
        try {
            lostItemDAO = new LostItemDAO();
            LOGGER.log(Level.INFO, "LostItemDAO initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize LostItemDAO", e);
            throw new SQLException("LostItemService initialization failed: " + e.getMessage(), e);
        }
    }

    public boolean createLostItem(LostItem lostItem) throws SQLException {
        LOGGER.log(Level.INFO, "Creating lost item for itemId: {0}", lostItem.getItemId());
        try {
            boolean success = lostItemDAO.createLostItem(lostItem);
            if (success) {
                LOGGER.log(Level.INFO, "Successfully created lost item for itemId: {0}", lostItem.getItemId());
            } else {
                LOGGER.log(Level.WARNING, "Failed to create lost item for itemId: {0}", lostItem.getItemId());
            }
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating lost item for itemId: {0}", new Object[]{lostItem.getItemId(), e});
            throw new SQLException("Failed to create lost item: " + e.getMessage(), e);
        }
    }

    public LostItem getLostItemDetails(int itemId) throws SQLException {
        LOGGER.log(Level.INFO, "Fetching lost item details for itemId: {0}", itemId);
        return lostItemDAO.getLostItemById(itemId);
    }
}