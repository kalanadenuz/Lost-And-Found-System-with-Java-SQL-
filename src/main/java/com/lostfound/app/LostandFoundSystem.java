package com.lostfound.app;

// Main entry point for the Lost and Found System application.
import com.lostfound.ui.LoginUI;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LostandFoundSystem {
    // Logger for capturing application errors.
    private static final Logger LOGGER = Logger.getLogger(LostandFoundSystem.class.getName());

    // Launches the application by displaying the LoginUI.
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new LoginUI().setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to start application", e);
            }
        });
    }
}