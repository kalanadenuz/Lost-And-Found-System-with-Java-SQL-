package com.lostfound.ui;

// Home UI for displaying and searching reports.
import com.lostfound.model.User;
import com.lostfound.service.ReportService;
import com.lostfound.service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class HomeUI extends JFrame {
    private UserService userService;
    private ReportService reportService;
    private JTable reportsTable;
    private JTextField searchField;
    private JLabel statusLabel;
    private static final Logger LOGGER = Logger.getLogger(HomeUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public HomeUI() throws SQLException {
        userService = new UserService();
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
            return;
        }

        try {
            reportService = new ReportService();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing ReportService", e);
            JOptionPane.showMessageDialog(this, "Failed to load reports: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dispose();
            });
            return;
        }

        setTitle("Home - Lost & Found");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 15;
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 25, 25);
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

        JPanel titleSearchPanel = new JPanel(new BorderLayout());
        titleSearchPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Latest Reports");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Latest Reports Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for the latest reports table");
        titleSearchPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(CARD_COLOR);
        searchField.setForeground(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.getAccessibleContext().setAccessibleName("Search Field");
        searchField.getAccessibleContext().setAccessibleDescription("Enter item name, location, or lost/found status to search reports");
        searchPanel.add(searchField);

        JButton btnSearch = new JButton("Search");
        styleMinimalButton(btnSearch);
        btnSearch.getAccessibleContext().setAccessibleName("Search Button");
        btnSearch.getAccessibleContext().setAccessibleDescription("Search reports by item name, location, or status");
        btnSearch.addActionListener(e -> searchReports());
        searchPanel.add(btnSearch);

        JButton btnClearSearch = new JButton("Clear");
        styleMinimalButton(btnClearSearch);
        btnClearSearch.getAccessibleContext().setAccessibleName("Clear Search Button");
        btnClearSearch.getAccessibleContext().setAccessibleDescription("Clear the search query and show all reports");
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            statusLabel.setText("");
            try {
                showLatestReports("");
            } catch (SQLException ex) {
                handleReportLoadError(ex);
            }
        });
        searchPanel.add(btnClearSearch);

        titleSearchPanel.add(searchPanel, BorderLayout.EAST);
        headerPanel.add(titleSearchPanel, BorderLayout.NORTH);

        JPanel logoutStatusPanel = new JPanel(new BorderLayout());
        logoutStatusPanel.setOpaque(false);

        JButton btnLogout = new JButton("Logout");
        styleMinimalButton(btnLogout);
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
                        Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                });
            }
        });
        logoutStatusPanel.add(btnLogout, BorderLayout.EAST);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(SECONDARY_TEXT);
        logoutStatusPanel.add(statusLabel, BorderLayout.CENTER);

        headerPanel.add(logoutStatusPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        reportsTable = new JTable();
        reportsTable.setRowHeight(30);
        reportsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportsTable.setForeground(TEXT_COLOR);
        reportsTable.setBackground(CARD_COLOR);
        reportsTable.setGridColor(new Color(70, 70, 70));
        reportsTable.setSelectionBackground(PRIMARY_COLOR);
        reportsTable.getAccessibleContext().setAccessibleName("Reports Table");
        reportsTable.getAccessibleContext().setAccessibleDescription("Table displaying the latest lost and found reports");

        JTableHeader header = reportsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(TEXT_COLOR);
        header.setBackground(new Color(50, 54, 59));
        header.setBorder(new LineBorder(new Color(70, 70, 70)));

        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton btnBack = new JButton("Back to Dashboard");
        styleMinimalButton(btnBack);
        btnBack.getAccessibleContext().setAccessibleName("Back to Dashboard");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to the user dashboard");
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
        buttonPanel.add(btnBack);

        JButton btnClose = new JButton("Close");
        styleMinimalButton(btnClose);
        btnClose.getAccessibleContext().setAccessibleName("Close Button");
        btnClose.getAccessibleContext().setAccessibleDescription("Exit the application");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Closing application");
            reportService.close();
            System.exit(0);
        });
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        reportsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = reportsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Object reportIdObj = reportsTable.getValueAt(selectedRow, 0);
                        if (reportIdObj == null || reportIdObj.toString().isEmpty()) {
                            LOGGER.log(Level.WARNING, "Invalid report ID in selected row");
                            JOptionPane.showMessageDialog(HomeUI.this, "Error: Invalid report ID",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        try {
                            int reportId = (reportIdObj instanceof Integer)
                                    ? (Integer) reportIdObj
                                    : Integer.parseInt(reportIdObj.toString());
                            LOGGER.log(Level.INFO, "Opening ReportUI for reportId: {0}", reportId);
                            new ReportUI(reportId).setVisible(true);
                            dispose();
                        } catch (NumberFormatException ex) {
                            LOGGER.log(Level.SEVERE, "Invalid reportId format: {0}", reportIdObj);
                            JOptionPane.showMessageDialog(HomeUI.this, "Error: Invalid report ID format",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (SQLException ex) {
                            LOGGER.log(Level.SEVERE, "Error loading report", ex);
                            JOptionPane.showMessageDialog(HomeUI.this, "Error loading report: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        try {
            showLatestReports("");
        } catch (SQLException e) {
            handleReportLoadError(e);
        }

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchReports();
                } else {
                    searchReports();
                }
            }
        });
    }

    private void searchReports() {
        String query = searchField.getText().trim();
        LOGGER.log(Level.INFO, "Searching reports with query: {0}", query);
        try {
            showLatestReports(query);
            statusLabel.setText(query.isEmpty() ? "" : "Showing results for: " + query);
            statusLabel.setForeground(PRIMARY_COLOR);
        } catch (SQLException e) {
            handleReportLoadError(e);
        }
    }

    private void showLatestReports(String searchQuery) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Report ID", "Item Name", "User Name", "User Contact", "Date"}, 0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        var reports = reportService.getAllReportsWithDetails();
        if (reports == null) {
            LOGGER.log(Level.WARNING, "ReportService returned null reports");
            model.addRow(new Object[]{"", "Error: No reports available", "", "", ""});
            statusLabel.setText("Error: Failed to load reports");
            statusLabel.setForeground(Color.RED);
            reportsTable.setModel(model);
            updateTableUI();
            return;
        }

        String query = searchQuery.toLowerCase();
        int matchCount = 0;

        for (var report : reports) {
            boolean matches = query.isEmpty() ||
                    (report.getItemName() != null && report.getItemName().toLowerCase().contains(query)) ||
                    (report.getStatus() != null && report.getStatus().toLowerCase().contains(query)) ||
                    (report.getLocation() != null && report.getLocation().toLowerCase().contains(query));
            if (matches) {
                model.addRow(new Object[]{
                        report.getReportId(),
                        report.getItemName() != null ? report.getItemName() : "",
                        report.getUserName() != null ? report.getUserName() : "",
                        report.getUserContact() != null ? report.getUserContact() : "",
                        report.getReportDate() != null ? report.getReportDate().toString() : ""
                });
                matchCount++;
            }
        }

        if (matchCount == 0 && !query.isEmpty()) {
            model.addRow(new Object[]{"", "No reports found", "", "", ""});
            statusLabel.setText("No results for: " + searchQuery);
            statusLabel.setForeground(Color.YELLOW);
        } else if (matchCount == 0 && query.isEmpty()) {
            model.addRow(new Object[]{"", "No reports available", "", "", ""});
            statusLabel.setText("No reports available");
            statusLabel.setForeground(Color.YELLOW);
        }

        reportsTable.setModel(model);
        reportsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        reportsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reportsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        reportsTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < reportsTable.getColumnCount(); i++) {
            reportsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        updateTableUI();
    }

    private void updateTableUI() {
        SwingUtilities.invokeLater(() -> {
            reportsTable.revalidate();
            reportsTable.repaint();
            LOGGER.log(Level.INFO, "Table updated, visible: {0}, rows: {1}, size: {2}",
                    new Object[]{reportsTable.isVisible(), reportsTable.getRowCount(), reportsTable.getSize()});
        });
    }

    private void handleReportLoadError(SQLException e) {
        LOGGER.log(Level.SEVERE, "Error loading reports: {0}", e.getMessage());
        JOptionPane.showMessageDialog(this, "Error loading reports: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        });
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HomeUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}