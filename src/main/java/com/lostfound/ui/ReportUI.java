package com.lostfound.ui;

// UI for displaying detailed report information for a specific report ID.
import com.lostfound.model.FoundItem;
import com.lostfound.model.Item;
import com.lostfound.model.LostItem;
import com.lostfound.model.Report;
import com.lostfound.service.FoundItemService;
import com.lostfound.service.ItemService;
import com.lostfound.service.LostItemService;
import com.lostfound.service.ReportService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;

public class ReportUI extends JFrame {
    private ReportService reportService;
    private ItemService itemService;
    private LostItemService lostItemService;
    private FoundItemService foundItemService;
    private static final Logger LOGGER = Logger.getLogger(ReportUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public ReportUI(int reportId) throws SQLException {
        LOGGER.log(Level.INFO, "Initializing ReportUI for reportId: {0}", reportId);

        try {
            reportService = new ReportService();
            itemService = new ItemService();
            lostItemService = new LostItemService();
            foundItemService = new FoundItemService();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing services", e);
            JOptionPane.showMessageDialog(this, "Failed to initialize services: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Report Details - Lost & Found");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

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

        JLabel titleLabel = new JLabel("Report Details");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Report Details Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for report details interface");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnClose = new JButton("× Close");
        styleMinimalButton(btnClose);
        btnClose.getAccessibleContext().setAccessibleName("Close");
        btnClose.getAccessibleContext().setAccessibleDescription("Return to home screen");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to HomeUI from ReportUI for reportId: {0}", reportId);
            try {
                new HomeUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        });
        headerPanel.add(btnClose, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);

        JPanel imagePanel = new JPanel();
        imagePanel.setOpaque(false);
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.getAccessibleContext().setAccessibleName("Item Image");
        imageLabel.getAccessibleContext().setAccessibleDescription("Image of the lost or found item");
        imagePanel.add(imageLabel);

        JPanel textDetailsPanel = new JPanel();
        textDetailsPanel.setOpaque(false);
        textDetailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Report report;
        try {
            report = reportService.getReportById(reportId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching report for reportId: {0}", reportId);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Error fetching report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            });
            return;
        }

        if (report == null) {
            LOGGER.log(Level.WARNING, "No report found for reportId: {0}", reportId);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Report not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            });
            return;
        }

        Item item;
        LostItem lostItem = null;
        FoundItem foundItem = null;
        try {
            item = itemService.getItemById(report.getItemId());
            if ("Lost".equals(report.getReportType())) {
                lostItem = lostItemService.getLostItemDetails(report.getItemId());
            } else if ("Found".equals(report.getReportType())) {
                foundItem = foundItemService.getFoundItemDetails(report.getItemId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching item or item details for itemId: {0}", report.getItemId());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Error fetching item details: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            });
            return;
        }

        String imagePath = null;
        if (lostItem != null && lostItem.getImagePath() != null) {
            imagePath = lostItem.getImagePath();
        } else if (foundItem != null && foundItem.getImagePath() != null) {
            imagePath = foundItem.getImagePath();
        }

        if (imagePath != null) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    imageLabel.setText("Image Not Found");
                    imageLabel.setForeground(TEXT_COLOR);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error loading image from path: {0}", imagePath);
                imageLabel.setText("Error Loading Image");
                imageLabel.setForeground(TEXT_COLOR);
            }
        } else {
            imageLabel.setText("No Image Available");
            imageLabel.setForeground(TEXT_COLOR);
        }

        int row = 0;
        addLabel(textDetailsPanel, gbc, "Report ID:", String.valueOf(report.getReportId()), row++);
        addLabel(textDetailsPanel, gbc, "User ID:", String.valueOf(report.getUserId()), row++);
        addLabel(textDetailsPanel, gbc, "Item ID:", String.valueOf(report.getItemId()), row++);
        addLabel(textDetailsPanel, gbc, "Report Type:", report.getReportType(), row++);
        addLabel(textDetailsPanel, gbc, "Report Date:", report.getReportDate() != null ? report.getReportDate().toString() : "N/A", row++);

        if (item != null) {
            addLabel(textDetailsPanel, gbc, "Item Name:", item.getName(), row++);
            addLabel(textDetailsPanel, gbc, "Description:", item.getDescription(), row++);
            addLabel(textDetailsPanel, gbc, "Category:", item.getCategory(), row++);
            addLabel(textDetailsPanel, gbc, "Status:", item.getStatus(), row++);
            addLabel(textDetailsPanel, gbc, "Date:", item.getDate() != null ? item.getDate().toString() : "N/A", row++);
        }

        if (lostItem != null) {
            addLabel(textDetailsPanel, gbc, "Last Seen Location:", lostItem.getLastSeenLocation(), row++);
            addLabel(textDetailsPanel, gbc, "Last Seen Date:", lostItem.getLastSeenDate() != null ? lostItem.getLastSeenDate().toString() : "N/A", row++);
            addLabel(textDetailsPanel, gbc, "Additional Details:", lostItem.getAdditionalDetails(), row++);
        } else if (foundItem != null) {
            addLabel(textDetailsPanel, gbc, "Found Location:", foundItem.getFoundLocation(), row++);
            addLabel(textDetailsPanel, gbc, "Found Date:", foundItem.getFoundDate() != null ? foundItem.getFoundDate().toString() : "N/A", row++);
            addLabel(textDetailsPanel, gbc, "Storage Location:", foundItem.getStorageLocation(), row++);
            addLabel(textDetailsPanel, gbc, "Additional Details:", foundItem.getAdditionalDetails(), row++);
        }

        detailsPanel.add(imagePanel, BorderLayout.NORTH);
        detailsPanel.add(textDetailsPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton btnBack = new JButton("Back");
        styleMinimalButton(btnBack);
        btnBack.getAccessibleContext().setAccessibleName("Back");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to home screen");
        btnBack.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating to HomeUI from ReportUI for reportId: {0}", reportId);
            try {
                new HomeUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        });
        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String labelText, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        label.getAccessibleContext().setAccessibleName(labelText);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueLabel = new JLabel(value != null ? value : "N/A");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.getAccessibleContext().setAccessibleName(labelText + " Value");
        panel.add(valueLabel, gbc);
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
                new ReportUI(1).setVisible(true);
            } catch (SQLException e) {
                Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, "Error starting ReportUI", e);
            }
        });
    }
}