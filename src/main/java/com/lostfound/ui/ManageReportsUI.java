package com.lostfound.ui;

// UI for managing reports based on mode (manage, view, user).
import com.lostfound.model.Item;
import com.lostfound.model.Report;
import com.lostfound.model.User;
import com.lostfound.service.ItemService;
import com.lostfound.service.ReportService;
import com.lostfound.service.UserService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageReportsUI extends JFrame {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private ReportService reportService;
    private ItemService itemService;
    private UserService userService;
    private String mode;
    private static final Logger LOGGER = Logger.getLogger(ManageReportsUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    public ManageReportsUI(String mode) throws SQLException {
        this.mode = mode;
        setTitle(getTitleForMode());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        userService = new UserService();
        try {
            reportService = new ReportService();
            itemService = new ItemService();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing services", e);
            SwingUtilities.invokeLater(() ->
                    showErrorDialog("Failed to initialize report management: " + e.getMessage()));
            navigateToFallback();
            return;
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            SwingUtilities.invokeLater(() -> {
                showErrorDialog("Please login first");
                try {
                    new LoginUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(ManageReportsUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
            return;
        }
        if ("manage".equals(mode) && !"admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.log(Level.WARNING, "Non-admin user attempted to access ManageReportsUI in manage mode: {0}",
                    currentUser.getEmail());
            SwingUtilities.invokeLater(() -> {
                showErrorDialog("Access denied: Admin privileges required");
                try {
                    new UserPanelUI().setVisible(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    showErrorDialog("Error opening dashboard: " + ex.getMessage());
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
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        add(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(getTitleForMode());
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Report Management Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for the report management interface");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnClose = styleMinimalButton("Close");
        btnClose.getAccessibleContext().setAccessibleName("Close Button");
        btnClose.getAccessibleContext().setAccessibleDescription("Close the report management window");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Closing ManageReportsUI");
            System.exit(0);
        });
        headerPanel.add(btnClose, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Report ID", "User ID", "Item Name", "Item Category", "Report Type", "Report Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        styleTable(reportTable);
        reportTable.getAccessibleContext().setAccessibleName("Reports Table");
        reportTable.getAccessibleContext().setAccessibleDescription("Table listing reports based on mode");

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnDelete = createModernButton("Delete Selected", ERROR_COLOR);
        btnDelete.getAccessibleContext().setAccessibleName("Delete Selected Button");
        btnDelete.getAccessibleContext().setAccessibleDescription("Delete the selected report");
        btnDelete.setEnabled("manage".equals(mode) || "user".equals(mode));
        btnDelete.addActionListener(e -> deleteSelectedReport());

        JButton btnRefresh = createModernButton("Refresh", PRIMARY_COLOR);
        btnRefresh.getAccessibleContext().setAccessibleName("Refresh Button");
        btnRefresh.getAccessibleContext().setAccessibleDescription("Reload the report data table");
        btnRefresh.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Refreshing reports in {0} mode", mode);
            loadReports();
        });

        JButton btnBack = createModernButton(getBackButtonText(), new Color(255, 99, 71));
        btnBack.getAccessibleContext().setAccessibleName("Back Button");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to the previous panel");
        btnBack.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to {0}",
                    "user".equals(mode) ? "UserPanelUI" : "AdminPanelUI");
            SwingUtilities.invokeLater(() -> {
                try {
                    if ("user".equals(mode)) {
                        new UserPanelUI().setVisible(true);
                    } else {
                        new AdminPanelUI().setVisible(true);
                    }
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error navigating to {0}",
                            "user".equals(mode) ? "UserPanelUI" : "AdminPanelUI");
                    SwingUtilities.invokeLater(() ->
                            showErrorDialog("Error opening panel: " + ex.getMessage()));
                }
            });
        });

        JButton btnLogout = createModernButton("Logout", new Color(255, 69, 0));
        btnLogout.getAccessibleContext().setAccessibleName("Logout Button");
        btnLogout.getAccessibleContext().setAccessibleDescription("Log out of the application");
        btnLogout.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Logging out user: {0}", currentUser.getEmail());
            SwingUtilities.invokeLater(() -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to logout?", "Confirm Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    userService.logout();
                    try {
                        new LoginUI().setVisible(true);
                        dispose();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error opening LoginUI", ex);
                        SwingUtilities.invokeLater(() ->
                                showErrorDialog("Error opening login page: " + ex.getMessage()));
                    }
                }
            });
        });

        buttonPanel.add(btnRefresh);
        if ("manage".equals(mode) || "user".equals(mode)) {
            buttonPanel.add(btnDelete);
        }
        buttonPanel.add(btnBack);
        buttonPanel.add(btnLogout);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadReports();
    }

    private String getTitleForMode() {
        switch (mode) {
            case "manage": return "Manage All Reports";
            case "view": return "View All Reports";
            case "user": return "Manage My Reports";
            default: return "Manage Reports";
        }
    }

    private String getBackButtonText() {
        return "user".equals(mode) ? "Back to Dashboard" : "Back to Admin Panel";
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_COLOR);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        header.setBackground(new Color(50, 53, 57));
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(row % 2 == 0 ? CARD_COLOR : new Color(50, 53, 57));
                setForeground(TEXT_COLOR);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setBackground(PRIMARY_COLOR.darker());
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
    }

    private void loadReports() {
        try {
            List<Report> reports;
            if ("user".equals(mode)) {
                User currentUser = userService.getCurrentUser();
                if (currentUser == null) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("No user logged in");
                        navigateToFallback();
                    });
                    return;
                }
                int userId = currentUser.getUserId();
                reports = reportService.getReportsByUserId(userId);
            } else {
                reports = reportService.getAllReports();
            }
            tableModel.setRowCount(0);

            for (Report report : reports) {
                Item item = itemService.getItemById(report.getItemId());
                tableModel.addRow(new Object[]{
                        report.getReportId(),
                        report.getUserId(),
                        item != null ? item.getName() : "N/A",
                        item != null ? item.getCategory() : "N/A",
                        report.getReportType(),
                        report.getReportDate()
                });
            }
            LOGGER.log(Level.INFO, "Loaded {0} reports in {1} mode",
                    new Object[]{reports.size(), mode});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading reports in {0} mode", mode);
            SwingUtilities.invokeLater(() ->
                    showErrorDialog("Error loading reports: " + e.getMessage()));
        }
    }

    private void deleteSelectedReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow >= 0) {
            int reportId = (int) tableModel.getValueAt(selectedRow, 0);
            String itemName = (String) tableModel.getValueAt(selectedRow, 2);
            SwingUtilities.invokeLater(() -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete report for item: " + itemName + " (ID: " + reportId + ")?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = reportService.deleteReport(reportId);
                        if (success) {
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(this,
                                            "Report deleted successfully",
                                            "Success", JOptionPane.INFORMATION_MESSAGE));
                            loadReports();
                            LOGGER.log(Level.INFO, "Deleted report ID: {0}, Item Name: {1}",
                                    new Object[]{reportId, itemName});
                        } else {
                            SwingUtilities.invokeLater(() ->
                                    showErrorDialog("Failed to delete report"));
                            LOGGER.log(Level.WARNING, "Failed to delete report ID: {0}", reportId);
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error deleting report ID: {0}", reportId);
                        SwingUtilities.invokeLater(() ->
                                showErrorDialog("Error deleting report: " + e.getMessage()));
                    }
                }
            });
        } else {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                            "Please select a report to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE));
            LOGGER.log(Level.WARNING, "No report selected for deletion");
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void navigateToFallback() {
        LOGGER.log(Level.INFO, "Navigating to fallback UI");
        SwingUtilities.invokeLater(() -> {
            try {
                if ("user".equals(mode)) {
                    new UserPanelUI().setVisible(true);
                } else {
                    new AdminPanelUI().setVisible(true);
                }
                dispose();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error navigating to fallback UI", ex);
                SwingUtilities.invokeLater(() ->
                        showErrorDialog("Error opening panel: " + ex.getMessage()));
            }
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

    private JButton styleMinimalButton(String text) {
        JButton button = new JButton(text);
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
        return button;
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
                new ManageReportsUI("manage").setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(ManageReportsUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}