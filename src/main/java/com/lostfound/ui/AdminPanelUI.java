package com.lostfound.ui;

// Admin Panel UI for managing users and reports.
import com.lostfound.model.User;
import com.lostfound.service.UserService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminPanelUI extends JFrame {
    private JButton btnManageUsers, btnManageReports, btnViewReports, btnBack, btnLogout, btnClose;
    private UserService userService;
    private static final Logger LOGGER = Logger.getLogger(AdminPanelUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public AdminPanelUI() throws IOException, SQLException {
        setTitle("Admin Panel - Lost & Found");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        userService = new UserService();
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(AdminPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
            return;
        }
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.log(Level.WARNING, "Non-admin user attempted to access AdminPanelUI: {0}", currentUser.getEmail());
            JOptionPane.showMessageDialog(this, "Access denied: Admin privileges required",
                    "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(AdminPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
            return;
        }

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 10;
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(shadowSize, shadowSize,
                        getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 20, 20);
                g2d.setColor(BACKGROUND_DARK);
                g2d.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, 20, 20);
                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(30, 40, 40, 40));
        add(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Admin Panel Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Administrative control panel for managing users and reports");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setOpaque(false);

        btnBack = new JButton("← Back to Dashboard");
        styleMinimalButton(btnBack);
        btnBack.getAccessibleContext().setAccessibleName("Back to Dashboard");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to the user dashboard");
        headerButtons.add(btnBack);

        btnLogout = new JButton("Logout");
        styleMinimalButton(btnLogout);
        btnLogout.getAccessibleContext().setAccessibleName("Logout");
        btnLogout.getAccessibleContext().setAccessibleDescription("Log out of the application");
        headerButtons.add(btnLogout);

        headerPanel.add(headerButtons, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        btnManageUsers = createModernButton("Manage Users", PRIMARY_COLOR);
        btnManageUsers.getAccessibleContext().setAccessibleName("Manage Users");
        btnManageUsers.getAccessibleContext().setAccessibleDescription("Open interface to manage user accounts");

        btnManageReports = createModernButton("Manage Reports", new Color(120, 111, 253));
        btnManageReports.getAccessibleContext().setAccessibleName("Manage Reports");
        btnManageReports.getAccessibleContext().setAccessibleDescription("Open interface to manage all reports");

        btnViewReports = createModernButton("View Reports", new Color(70, 200, 150));
        btnViewReports.getAccessibleContext().setAccessibleName("View Reports");
        btnViewReports.getAccessibleContext().setAccessibleDescription("View all reports in read-only mode");

        contentPanel.add(btnManageUsers);
        contentPanel.add(btnManageReports);
        contentPanel.add(btnViewReports);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnClose = new JButton("Close");
        styleMinimalButton(btnClose);
        btnClose.getAccessibleContext().setAccessibleName("Close");
        btnClose.getAccessibleContext().setAccessibleDescription("Exit the application");
        bottomPanel.add(btnClose);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        btnManageUsers.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Opening ManageUsersUI");
            SwingUtilities.invokeLater(() -> {
                try {
                    new ManageUsersUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening ManageUsersUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        btnManageReports.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Opening ManageReportsUI (manage mode)");
            SwingUtilities.invokeLater(() -> {
                try {
                    new ManageReportsUI("manage").setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening ManageReportsUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        btnViewReports.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Opening ManageReportsUI (view mode)");
            SwingUtilities.invokeLater(() -> {
                try {
                    new ManageReportsUI("view").setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening ManageReportsUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        btnBack.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to UserPanelUI");
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        btnLogout.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Logging out user: {0}", currentUser.getEmail());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                userService.logout();
                SwingUtilities.invokeLater(() -> {
                    try {
                        new LoginUI().setVisible(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                });
            }
        });

        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Closing application");
            System.exit(0);
        });
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(baseColor.brighter());
                } else {
                    g2.setColor(baseColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(5, 0.3f),
                new EmptyBorder(15, 30, 15, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void styleMinimalButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setForeground(SECONDARY_TEXT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setForeground(PRIMARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setForeground(SECONDARY_TEXT);
            }
        });
    }

    private static class ShadowBorder extends AbstractBorder {
        private final int shadowSize;
        private final float shadowOpacity;

        public ShadowBorder(int shadowSize, float shadowOpacity) {
            this.shadowSize = shadowSize;
            this.shadowOpacity = shadowOpacity;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color shadow = new Color(0, 0, 0, shadowOpacity);
            for (int i = 0; i < shadowSize; i++) {
                float ratio = (float) i / shadowSize;
                g2d.setColor(new Color(
                        shadow.getRed(),
                        shadow.getGreen(),
                        shadow.getBlue(),
                        (int) (shadow.getAlpha() * (1 - ratio))
                ));
                g2d.drawRoundRect(
                        x + i, y + i,
                        width - 1 - i * 2, height - 1 - i * 2,
                        15, 15
                );
            }
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminPanelUI().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(AdminPanelUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(AdminPanelUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}