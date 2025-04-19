package com.lostfound.ui;

// User dashboard UI for accessing report forms, managing reports, and admin functions.
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

public class UserPanelUI extends JFrame {
    private UserService userService;
    private JButton btnFoundItemForm, btnLostItemForm, btnManageReports, btnAdminPanel, btnHome, btnLogout, btnClose;
    private static final Logger LOGGER = Logger.getLogger(UserPanelUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public UserPanelUI() throws SQLException {
        setTitle("Lost & Found - User Dashboard");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        userService = new UserService();

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
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
                int shadowSize = 15;
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(shadowSize, shadowSize,
                        getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 25, 25);
                g2d.setColor(BACKGROUND_DARK);
                g2d.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, 25, 25);
                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        add(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 40, 0));

        JLabel titleLabel = new JLabel("Welcome, " + currentUser.getName());
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Welcome Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Welcome message for user");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        btnLogout = createModernButton("Logout", new Color(255, 69, 0));
        btnLogout.getAccessibleContext().setAccessibleName("Logout Button");
        btnLogout.getAccessibleContext().setAccessibleDescription("Log out of the application");
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
                        Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                });
            }
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 25, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 40, 0));

        btnFoundItemForm = createDashboardButton("Report Found Item", new Color(98, 179, 119), "found.png");
        btnLostItemForm = createDashboardButton("Report Lost Item", new Color(70, 130, 180), "lost.png");
        btnManageReports = createDashboardButton("My Reports", new Color(143, 188, 143), "reports.png");
        btnHome = createDashboardButton("Home", new Color(255, 165, 0), "home.png");

        contentPanel.add(btnFoundItemForm);
        contentPanel.add(btnLostItemForm);
        contentPanel.add(btnManageReports);
        contentPanel.add(btnHome);

        if ("admin".equalsIgnoreCase(currentUser.getRole())) {
            btnAdminPanel = createDashboardButton("Admin Panel", new Color(169, 169, 169), "admin.png");
            contentPanel.add(btnAdminPanel);
        } else {
            contentPanel.add(new JPanel());
        }

        contentPanel.add(new JPanel());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("Lost & Found System • v2.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(SECONDARY_TEXT);
        footerPanel.add(footerLabel);

        btnClose = new JButton("Exit System");
        styleMinimalButton(btnClose);
        btnClose.getAccessibleContext().setAccessibleName("Exit Button");
        btnClose.getAccessibleContext().setAccessibleDescription("Exit the application");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Exiting application");
            System.exit(0);
        });
        footerPanel.add(Box.createHorizontalStrut(20));
        footerPanel.add(btnClose);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        btnFoundItemForm.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to CreateReportUI (Found)");
            SwingUtilities.invokeLater(() -> {
                CreateReportUI reportUI = new CreateReportUI();
                reportUI.setCategory("Found");
                reportUI.setVisible(true);
                dispose();
            });
        });

        btnLostItemForm.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to CreateReportUI (Lost)");
            SwingUtilities.invokeLater(() -> {
                CreateReportUI reportUI = new CreateReportUI();
                reportUI.setCategory("Lost");
                reportUI.setVisible(true);
                dispose();
            });
        });

        btnManageReports.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to ManageReportsUI (user)");
            SwingUtilities.invokeLater(() -> {
                try {
                    new ManageReportsUI("user").setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
        });

        btnHome.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to HomeUI");
            SwingUtilities.invokeLater(() -> {
                try {
                    new HomeUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
        });

        if ("admin".equalsIgnoreCase(currentUser.getRole())) {
            btnAdminPanel.addActionListener(e -> {
                LOGGER.log(Level.INFO, "Navigating to AdminPanelUI");
                SwingUtilities.invokeLater(() -> {
                    try {
                        new AdminPanelUI().setVisible(true);
                    } catch (IOException ex) {
                        Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(UserPanelUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                });
            });
        }
    }

    private JButton createDashboardButton(String text, Color baseColor, String iconName) {
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

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            if (icon.getImage() != null) {
                button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
                button.setHorizontalTextPosition(SwingConstants.CENTER);
                button.setVerticalTextPosition(SwingConstants.BOTTOM);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Icon not found: {0}", iconName);
        }

        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        button.setForeground(Color.BLACK);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(5, 0.3f),
                new EmptyBorder(25, 10, 25, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.getAccessibleContext().setAccessibleName(text);
        button.getAccessibleContext().setAccessibleDescription("Navigate to " + text.toLowerCase());

        return button;
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
                new UserPanelUI().setVisible(true);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error starting UserPanelUI", e);
            }
        });
    }
}