package com.lostfound.ui;

// UI for creating lost or found item reports.
import com.lostfound.model.Item;
import com.lostfound.model.LostItem;
import com.lostfound.model.FoundItem;
import com.lostfound.model.Report;
import com.lostfound.model.User;
import com.lostfound.service.ItemService;
import com.lostfound.service.LostItemService;
import com.lostfound.service.FoundItemService;
import com.lostfound.service.ReportService;
import com.lostfound.service.UserService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CreateReportUI extends JFrame {
    private JTextField txtTitle, txtLastSeenLocation, txtReward, txtFoundLocation, txtStorageLocation;
    private JTextArea txtDescription;
    private JComboBox<String> cmbCategory;
    private JButton btnSubmit, btnCancel, btnUploadImage;
    private JLabel lblImagePath;
    private File selectedImageFile;
    private JPanel specificFieldsPanel;
    private UserService userService;
    private ItemService itemService;
    private LostItemService lostItemService;
    private FoundItemService foundItemService;
    private ReportService reportService;
    private static final Logger LOGGER = Logger.getLogger(CreateReportUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public CreateReportUI() {
        try {
            userService = new UserService();
            itemService = new ItemService();
            lostItemService = new LostItemService();
            foundItemService = new FoundItemService();
            reportService = new ReportService();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing services", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Failed to initialize: " + e.getMessage(),
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

        setTitle("Create Report - Lost & Found");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

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

        JLabel titleLabel = new JLabel("Create New Report");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Create New Report");
        titleLabel.getAccessibleContext().setAccessibleDescription("Form to create a new report");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        formPanel.add(createFormField("Title", txtTitle = new JTextField()));
        formPanel.add(Box.createVerticalStrut(20));

        JPanel categoryPanel = new JPanel(new BorderLayout(10, 0));
        categoryPanel.setOpaque(false);
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryLabel.setForeground(SECONDARY_TEXT);
        categoryLabel.getAccessibleContext().setAccessibleName("Category");
        categoryLabel.getAccessibleContext().setAccessibleDescription("Select report category");
        categoryPanel.add(categoryLabel, BorderLayout.WEST);

        String[] categories = {"Lost", "Found"};
        cmbCategory = new JComboBox<>(categories);
        styleComboBox(cmbCategory);
        cmbCategory.getAccessibleContext().setAccessibleName("Category Selector");
        cmbCategory.getAccessibleContext().setAccessibleDescription("Choose report category");
        categoryPanel.add(cmbCategory, BorderLayout.CENTER);
        formPanel.add(categoryPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(SECONDARY_TEXT);
        descLabel.getAccessibleContext().setAccessibleName("Description");
        descLabel.getAccessibleContext().setAccessibleDescription("Enter report description");
        formPanel.add(descLabel);

        txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setForeground(TEXT_COLOR);
        txtDescription.setBackground(CARD_COLOR);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.getAccessibleContext().setAccessibleName("Description Input");
        txtDescription.getAccessibleContext().setAccessibleDescription("Enter report description");
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(20));

        specificFieldsPanel = new JPanel();
        specificFieldsPanel.setOpaque(false);
        specificFieldsPanel.setLayout(new BoxLayout(specificFieldsPanel, BoxLayout.Y_AXIS));
        formPanel.add(specificFieldsPanel);
        updateSpecificFields("Lost");

        cmbCategory.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateSpecificFields((String) cmbCategory.getSelectedItem());
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        btnSubmit = new JButton("Submit Report");
        stylePrimaryButton(btnSubmit);
        btnSubmit.getAccessibleContext().setAccessibleName("Submit Report");
        btnSubmit.getAccessibleContext().setAccessibleDescription("Submit the report");

        btnCancel = new JButton("Cancel");
        styleMinimalButton(btnCancel);
        btnCancel.getAccessibleContext().setAccessibleName("Cancel");
        btnCancel.getAccessibleContext().setAccessibleDescription("Cancel and return to user panel");

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        btnSubmit.addActionListener(e -> submitReport());
        btnCancel.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Cancelling report creation");
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    JOptionPane.showMessageDialog(this, "Error opening dashboard: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    public void setCategory(String category) {
        if ("Lost".equalsIgnoreCase(category) || "Found".equalsIgnoreCase(category)) {
            cmbCategory.setSelectedItem(category);
            updateSpecificFields(category);
            LOGGER.log(Level.INFO, "Set report category to: {0}", category);
        } else {
            LOGGER.log(Level.WARNING, "Invalid category provided: {0}", category);
        }
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(SECONDARY_TEXT);
        label.getAccessibleContext().setAccessibleName(labelText);
        label.getAccessibleContext().setAccessibleDescription("Label for " + labelText.toLowerCase() + " input");
        panel.add(label, BorderLayout.WEST);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(CARD_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.getAccessibleContext().setAccessibleName(labelText + " Input");
        textField.getAccessibleContext().setAccessibleDescription("Enter " + labelText.toLowerCase());
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70)),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR.brighter(), 1),
                new EmptyBorder(15, 30, 15, 30)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
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

    private void updateSpecificFields(String category) {
        specificFieldsPanel.removeAll();
        selectedImageFile = null;

        if ("Lost".equals(category)) {
            specificFieldsPanel.add(createFormField("Last Seen Location", txtLastSeenLocation = new JTextField()));
            specificFieldsPanel.add(Box.createVerticalStrut(20));
            specificFieldsPanel.add(createFormField("Reward Offered ($)", txtReward = new JTextField()));
            specificFieldsPanel.add(Box.createVerticalStrut(20));
        } else if ("Found".equals(category)) {
            specificFieldsPanel.add(createFormField("Found Location", txtFoundLocation = new JTextField()));
            specificFieldsPanel.add(Box.createVerticalStrut(20));
            specificFieldsPanel.add(createFormField("Storage Location", txtStorageLocation = new JTextField()));
            specificFieldsPanel.add(Box.createVerticalStrut(20));
        }

        JPanel uploadPanel = new JPanel(new BorderLayout(10, 5));
        uploadPanel.setOpaque(false);
        JLabel uploadLabel = new JLabel("Item Image");
        uploadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        uploadLabel.setForeground(SECONDARY_TEXT);
        uploadLabel.getAccessibleContext().setAccessibleName("Item Image");
        uploadLabel.getAccessibleContext().setAccessibleDescription("Upload an image of the item");
        uploadPanel.add(uploadLabel, BorderLayout.WEST);

        btnUploadImage = new JButton("Upload Image");
        styleSecondaryButton(btnUploadImage);
        btnUploadImage.getAccessibleContext().setAccessibleName("Upload Image Button");
        btnUploadImage.getAccessibleContext().setAccessibleDescription("Upload an image of the item");
        uploadPanel.add(btnUploadImage, BorderLayout.CENTER);

        lblImagePath = new JLabel("No image selected");
        lblImagePath.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblImagePath.setForeground(SECONDARY_TEXT);
        lblImagePath.getAccessibleContext().setAccessibleName("Image Path Label");
        lblImagePath.getAccessibleContext().setAccessibleDescription("Displays the selected image file name");
        uploadPanel.add(lblImagePath, BorderLayout.SOUTH);

        specificFieldsPanel.add(uploadPanel);
        specificFieldsPanel.add(Box.createVerticalStrut(20));

        btnUploadImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Image files", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                lblImagePath.setText("Selected: " + selectedImageFile.getName());
                lblImagePath.setForeground(new Color(144, 238, 144));
                LOGGER.log(Level.INFO, "Image selected: {0}", selectedImageFile.getName());
            }
        });

        specificFieldsPanel.revalidate();
        specificFieldsPanel.repaint();
    }

    private void submitReport() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                LOGGER.log(Level.WARNING, "No user logged in during submitReport");
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

            String title = txtTitle.getText().trim();
            String description = txtDescription.getText().trim();
            String category = ((String) cmbCategory.getSelectedItem()).toLowerCase();

            if (title.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Title and Description are required",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String imagePath = processImage(category);

            Item item = new Item(
                    0,
                    title,
                    description,
                    category,
                    currentUser.getUserId(),
                    category,
                    new java.sql.Timestamp(System.currentTimeMillis())
            );
            int itemId = itemService.createItem(item);
            if (itemId <= 0) {
                throw new SQLException("Failed to create item");
            }
            LOGGER.log(Level.INFO, "Created item with ID: {0}", itemId);

            if ("lost".equals(category)) {
                String lastSeenLocation = txtLastSeenLocation != null ? txtLastSeenLocation.getText().trim() : "";
                String rewardStr = txtReward != null ? txtReward.getText().trim() : "";
                if (lastSeenLocation.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Last Seen Location is required for lost items",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double reward = 0.0;
                if (!rewardStr.isEmpty()) {
                    try {
                        reward = Double.parseDouble(rewardStr);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Please enter a valid reward amount",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                String additionalDetails = reward > 0 ? "Reward Offered: $" + reward : null;
                LostItem lostItem = new LostItem(
                        itemId,
                        lastSeenLocation,
                        new java.sql.Date(System.currentTimeMillis()),
                        additionalDetails,
                        imagePath
                );
                if (!lostItemService.createLostItem(lostItem)) {
                    throw new SQLException("Failed to create lost item");
                }
                LOGGER.log(Level.INFO, "Created lost item for itemId: {0}", itemId);
            } else if ("found".equals(category)) {
                String foundLocation = txtFoundLocation != null ? txtFoundLocation.getText().trim() : "";
                String storageLocation = txtStorageLocation != null ? txtStorageLocation.getText().trim() : "";
                if (foundLocation.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Found Location is required for found items",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                FoundItem foundItem = new FoundItem(
                        itemId,
                        foundLocation,
                        new java.sql.Date(System.currentTimeMillis()),
                        storageLocation,
                        null,
                        imagePath
                );
                if (!foundItemService.createFoundItem(foundItem)) {
                    throw new SQLException("Failed to create found item");
                }
                LOGGER.log(Level.INFO, "Created found item for itemId: {0}", itemId);
            }

            Report report = new Report(
                    0,
                    currentUser.getUserId(),
                    itemId,
                    category,
                    new Timestamp(System.currentTimeMillis())
            );
            if (!reportService.createReport(report)) {
                throw new SQLException("Failed to create report");
            }
            LOGGER.log(Level.INFO, "Created report for itemId: {0}", itemId);

            JOptionPane.showMessageDialog(this,
                    "Report submitted successfully!\nReference ID: " + itemId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    JOptionPane.showMessageDialog(this, "Error opening dashboard: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error in submitReport: {0}", ex.getMessage());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            });
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error processing image in submitReport", ex);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Error processing image: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private String processImage(String type) throws IOException {
        if (selectedImageFile == null) {
            return null;
        }
        String targetDir = "images/" + type;
        File dir = new File(targetDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create image directory: " + targetDir);
        }
        String newFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
        File targetFile = new File(dir, newFileName);
        Files.copy(selectedImageFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        LOGGER.log(Level.INFO, "Image copied to: {0}", targetFile.getPath());
        return targetFile.getPath();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateReportUI frame = new CreateReportUI();
            frame.setVisible(true);
        });
    }
}