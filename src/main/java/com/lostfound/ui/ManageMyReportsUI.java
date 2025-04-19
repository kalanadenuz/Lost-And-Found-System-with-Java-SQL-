package com.lostfound.ui;

// UI for managing user's lost and found reports.
import com.lostfound.model.*;
import com.lostfound.service.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ManageMyReportsUI extends JFrame {
    private ReportService reportService;
    private ItemService itemService;
    private LostItemService lostItemService;
    private FoundItemService foundItemService;
    private UserService userService;
    private static final Logger LOGGER = Logger.getLogger(ManageMyReportsUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    private JTable foundTable, lostTable;
    private DefaultTableModel foundTableModel, lostTableModel;

    public ManageMyReportsUI() throws IOException {
        setTitle("Manage My Reports");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        try {
            userService = new UserService();
            reportService = new ReportService();
            itemService = new ItemService();
            lostItemService = new LostItemService();
            foundItemService = new FoundItemService();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing services", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Failed to initialize report management: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            });
            return;
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    new LoginUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening LoginUI", ex);
                    JOptionPane.showMessageDialog(this, "Error opening login page: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
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

        JLabel titleLabel = new JLabel("Manage My Reports");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Manage My Reports Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for managing personal reports");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnClose = createModernButton("Close", new Color(255, 99, 71));
        btnClose.getAccessibleContext().setAccessibleName("Close Button");
        btnClose.getAccessibleContext().setAccessibleDescription("Close the report management window");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Closing ManageMyReportsUI");
            reportService.close();
            dispose();
        });
        headerPanel.add(btnClose, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        centerPanel.setOpaque(false);

        foundTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        foundTableModel.addColumn("Report ID");
        foundTableModel.addColumn("Item ID");
        foundTableModel.addColumn("Item Name");
        foundTableModel.addColumn("Details");
        foundTableModel.addColumn("Report Date");
        foundTableModel.addColumn("Action");

        foundTable = createStyledTable(foundTableModel);
        foundTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Found"));
        foundTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Found"));
        foundTable.removeColumn(foundTable.getColumnModel().getColumn(0));
        foundTable.getAccessibleContext().setAccessibleName("Found Reports Table");
        foundTable.getAccessibleContext().setAccessibleDescription("Table listing found reports");

        JPanel foundPanel = createTablePanel("Found Reports", foundTable);

        lostTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        lostTableModel.addColumn("Report ID");
        lostTableModel.addColumn("Item ID");
        lostTableModel.addColumn("Item Name");
        lostTableModel.addColumn("Details");
        lostTableModel.addColumn("Report Date");
        lostTableModel.addColumn("Action");

        lostTable = createStyledTable(lostTableModel);
        lostTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Lost"));
        lostTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Lost"));
        lostTable.removeColumn(lostTable.getColumnModel().getColumn(0));
        lostTable.getAccessibleContext().setAccessibleName("Lost Reports Table");
        lostTable.getAccessibleContext().setAccessibleDescription("Table listing lost reports");

        JPanel lostPanel = createTablePanel("Lost Reports", lostTable);

        centerPanel.add(foundPanel);
        centerPanel.add(lostPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnRefresh = createModernButton("Refresh", PRIMARY_COLOR);
        btnRefresh.getAccessibleContext().setAccessibleName("Refresh Button");
        btnRefresh.getAccessibleContext().setAccessibleDescription("Reload the report tables");
        btnRefresh.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Refreshing report tables");
            try {
                populateTables();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error refreshing data", ex);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error refreshing data: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            } catch (IOException ex) {
                Logger.getLogger(ManageMyReportsUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JButton btnBack = createModernButton("Back to Dashboard", new Color(70, 200, 150));
        btnBack.getAccessibleContext().setAccessibleName("Back to Dashboard Button");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to the user dashboard");
        btnBack.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to UserPanelUI");
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                    reportService.close();
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Error opening dashboard: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE));
                }
            });
        });

        JButton btnLogout = createModernButton("Logout", ERROR_COLOR);
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
                        reportService.close();
                        dispose();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error opening LoginUI", ex);
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Error opening login page: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE));
                    }
                }
            });
        });

        footerPanel.add(btnRefresh);
        footerPanel.add(btnBack);
        footerPanel.add(btnLogout);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        try {
            populateTables();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading reports", e);
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error loading reports: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
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
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        return table;
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titleLabel.getAccessibleContext().setAccessibleName(title + " Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for " + title.toLowerCase() + " table");
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.getAccessibleContext().setAccessibleName(title + " Table");
        scrollPane.getAccessibleContext().setAccessibleDescription("Table listing " + title.toLowerCase());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void populateTables() throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Populating report tables");
        foundTableModel.setRowCount(0);
        lostTableModel.setRowCount(0);

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in during populateTables");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    new LoginUI().setVisible(true);
                    reportService.close();
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening LoginUI", ex);
                    JOptionPane.showMessageDialog(this, "Error opening login page: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            return;
        }

        List<Report> reports = reportService.getCurrentUserReports();
        if (reports == null || reports.isEmpty()) {
            LOGGER.log(Level.INFO, "No reports found for user: {0}", currentUser.getEmail());
            foundTableModel.addRow(new Object[]{0, 0, "No found reports", "", "", ""});
            lostTableModel.addRow(new Object[]{0, 0, "No lost reports", "", "", ""});
            return;
        }

        for (Report report : reports) {
            Item item = itemService.getItemById(report.getItemId());
            if (item == null) {
                LOGGER.log(Level.WARNING, "Item not found for report ID: {0}", report.getReportId());
                continue;
            }

            String details = "";
            if ("found".equalsIgnoreCase(report.getReportType())) {
                FoundItem foundItem = foundItemService.getFoundItemDetails(item.getItemId());
                details = foundItem != null ? "Location: " + foundItem.getFoundLocation() : "N/A";
                if (foundItem == null) {
                    LOGGER.log(Level.WARNING, "FoundItem details missing for item ID: {0}", item.getItemId());
                }
                Object[] row = {
                        report.getReportId(),
                        item.getItemId(),
                        item.getName(),
                        details,
                        report.getReportDate(),
                        "Delete"
                };
                foundTableModel.addRow(row);
            } else if ("lost".equalsIgnoreCase(report.getReportType())) {
                LostItem lostItem = lostItemService.getLostItemDetails(item.getItemId());
                details = lostItem != null ? "Last seen: " + lostItem.getLastSeenLocation() : "N/A";
                if (lostItem == null) {
                    LOGGER.log(Level.WARNING, "LostItem details missing for item ID: {0}", item.getItemId());
                }
                Object[] row = {
                        report.getReportId(),
                        item.getItemId(),
                        item.getName(),
                        details,
                        report.getReportDate(),
                        "Delete"
                };
                lostTableModel.addRow(row);
            }
        }
        LOGGER.log(Level.INFO, "Loaded {0} reports for user: {1}",
                new Object[]{reports.size(), currentUser.getEmail()});
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String type) {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBackground(ERROR_COLOR);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR.darker(), 1),
                    new EmptyBorder(5, 15, 5, 15)
            ));
            setText("Delete");
            getAccessibleContext().setAccessibleName("Delete " + type + " Report Button");
            getAccessibleContext().setAccessibleDescription("Delete the selected " + type.toLowerCase() + " report");
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String type;
        private JTable tableRef;

        public ButtonEditor(JCheckBox checkBox, String type) {
            this.type = type;
            button = new JButton("Delete");
            button.setBackground(ERROR_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR.darker(), 1),
                    new EmptyBorder(5, 15, 5, 15)
            ));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.getAccessibleContext().setAccessibleName("Delete " + type + " Report Button");
            button.getAccessibleContext().setAccessibleDescription("Delete the selected " + type.toLowerCase() + " report");

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    button.setBackground(ERROR_COLOR.darker());
                }
                public void mouseExited(MouseEvent evt) {
                    button.setBackground(ERROR_COLOR);
                }
            });

            button.addActionListener(e -> {
                int row = tableRef.getSelectedRow();
                if (row < 0) return;
                int modelRow = tableRef.convertRowIndexToModel(row);
                int reportId = (int) ((DefaultTableModel) tableRef.getModel()).getValueAt(modelRow, 0);

                SwingUtilities.invokeLater(() -> {
                    int confirm = JOptionPane.showConfirmDialog(ManageMyReportsUI.this,
                            "Are you sure you want to delete this " + type.toLowerCase() + " report?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            boolean success = reportService.deleteReport(reportId);
                            if (success) {
                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(ManageMyReportsUI.this,
                                                "Report deleted successfully",
                                                "Success", JOptionPane.INFORMATION_MESSAGE));
                                populateTables();
                                LOGGER.log(Level.INFO, "Deleted {0} report ID: {1}",
                                        new Object[]{type.toLowerCase(), reportId});
                            } else {
                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(ManageMyReportsUI.this,
                                                "Failed to delete report",
                                                "Error", JOptionPane.ERROR_MESSAGE));
                                LOGGER.log(Level.WARNING, "Failed to delete {0} report ID: {1}",
                                        new Object[]{type.toLowerCase(), reportId});
                            }
                        } catch (SQLException ex) {
                            LOGGER.log(Level.SEVERE, "Error deleting {0} report ID: {1}",
                                    new Object[]{type.toLowerCase(), reportId});
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(ManageMyReportsUI.this,
                                            "Failed to delete report: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE));
                        } catch (IOException ex) {
                            Logger.getLogger(ManageMyReportsUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    fireEditingStopped();
                });
            });
        }

        public Object getCellEditorValue() {
            return "Delete";
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.tableRef = table;
            return button;
        }
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
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                new ManageMyReportsUI().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(ManageMyReportsUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}