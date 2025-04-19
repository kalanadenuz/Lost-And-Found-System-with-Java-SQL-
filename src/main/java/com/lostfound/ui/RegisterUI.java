package com.lostfound.ui;

// UI for user registration.
import com.lostfound.service.UserService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.event.DocumentListener;

public class RegisterUI extends JFrame {
    private JTextField txtFullName, txtEmail, txtContact;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnClear, btnBack;
    private UserService userService;
    private JProgressBar progressBar;
    private static final Logger LOGGER = Logger.getLogger(RegisterUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);
    private static final Color ERROR_COLOR = new Color(255, 100, 100);

    public RegisterUI() throws SQLException {
        setTitle("User Registration");
        setSize(700, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        userService = new UserService();

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

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Create New Account");
        titleLabel.getAccessibleContext().setAccessibleDescription("Form to register a new user account");
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        formPanel.add(createFormField("Full Name", txtFullName = new JTextField()));
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Email Address", txtEmail = new JTextField()));
        txtEmail.getDocument().addDocumentListener(new ValidationListener(txtEmail, this::isValidEmail));
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Contact Number", txtContact = new JTextField()));
        txtContact.getDocument().addDocumentListener(new ValidationListener(txtContact, this::isValidContact));
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Password", txtPassword = new JPasswordField()));
        txtPassword.getDocument().addDocumentListener(new ValidationListener(txtPassword, s -> s.length() >= 8));
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createFormField("Confirm Password", txtConfirmPassword = new JPasswordField()));
        txtConfirmPassword.getDocument().addDocumentListener(new ValidationListener(txtConfirmPassword,
                s -> s.equals(new String(txtPassword.getPassword()))));
        formPanel.add(Box.createVerticalStrut(20));

        JPanel rolePanel = new JPanel(new BorderLayout(10, 0));
        rolePanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Account Type");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(SECONDARY_TEXT);
        roleLabel.getAccessibleContext().setAccessibleName("Account Type");
        roleLabel.getAccessibleContext().setAccessibleDescription("Select user or admin account type");
        rolePanel.add(roleLabel, BorderLayout.WEST);

        String[] roles = userService.isAdmin() ? new String[]{"User", "Admin"} : new String[]{"User"};
        cmbRole = new JComboBox<>(roles);
        styleComboBox(cmbRole);
        cmbRole.getAccessibleContext().setAccessibleName("Account Type Selector");
        cmbRole.getAccessibleContext().setAccessibleDescription("Choose between User or Admin role");
        cmbRole.setToolTipText(userService.isAdmin() ? "Select account type" : "Only User role available for non-administrators");
        rolePanel.add(cmbRole, BorderLayout.CENTER);

        formPanel.add(rolePanel);
        formPanel.add(Box.createVerticalStrut(30));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.getAccessibleContext().setAccessibleName("Registration Progress");
        formPanel.add(progressBar);
        formPanel.add(Box.createVerticalStrut(15));

        btnRegister = createModernButton("Create Account", PRIMARY_COLOR);
        btnRegister.getAccessibleContext().setAccessibleName("Create Account Button");
        btnRegister.getAccessibleContext().setAccessibleDescription("Initiate account registration");
        formPanel.add(btnRegister);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setOpaque(false);

        btnClear = createModernButton("Clear Form", new Color(120, 111, 253));
        btnClear.getAccessibleContext().setAccessibleName("Clear Form Button");
        btnClear.getAccessibleContext().setAccessibleDescription("Clear all form fields");

        btnBack = new JButton("Back to Login");
        styleMinimalButton(btnBack);
        btnBack.getAccessibleContext().setAccessibleName("Back to Login Button");
        btnBack.getAccessibleContext().setAccessibleDescription("Return to login screen");

        bottomPanel.add(btnClear);
        bottomPanel.add(btnBack);
        formPanel.add(bottomPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> handleRegistration());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Navigating back to LoginUI");
            dispose();
            try {
                new LoginUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(RegisterUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(SECONDARY_TEXT);
        label.getAccessibleContext().setAccessibleName(labelText);
        label.getAccessibleContext().setAccessibleDescription("Label for " + labelText + " input field");
        panel.add(label, BorderLayout.NORTH);

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

    private void handleRegistration() {
        LOGGER.log(Level.INFO, "Attempting user registration");
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("All fields are required");
            LOGGER.log(Level.WARNING, "Registration failed: Missing required fields");
            return;
        }

        if (!isValidEmail(email)) {
            showErrorDialog("Please enter a valid email address");
            LOGGER.log(Level.WARNING, "Registration failed: Invalid email format");
            return;
        }

        if (!isValidContact(contact)) {
            showErrorDialog("Contact number must be 10 digits");
            LOGGER.log(Level.WARNING, "Registration failed: Invalid contact number");
            return;
        }

        if (password.length() < 8) {
            showErrorDialog("Password must be at least 8 characters");
            LOGGER.log(Level.WARNING, "Registration failed: Password too short");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Passwords do not match");
            LOGGER.log(Level.WARNING, "Registration failed: Passwords do not match");
            return;
        }

        try {
            if (userService.findUserByEmail(email) != null) {
                showErrorDialog("Email already registered");
                LOGGER.log(Level.WARNING, "Registration failed: Email {0} already exists", email);
                return;
            }

            setFormEnabled(false);
            progressBar.setVisible(true);

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws SQLException {
                    return userService.registerUserWithRole(name, email, password, role, contact);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        setFormEnabled(true);
                        progressBar.setVisible(false);
                        if (success) {
                            JOptionPane.showMessageDialog(RegisterUI.this,
                                    "Registration successful!\nYou can now login.",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            LOGGER.log(Level.INFO, "Registration successful for email: {0}", email);
                            dispose();
                            new LoginUI().setVisible(true);
                        } else {
                            showErrorDialog("Registration failed. Please try again.");
                            LOGGER.log(Level.WARNING, "Registration failed for email: {0}", email);
                        }
                    } catch (Exception e) {
                        setFormEnabled(true);
                        progressBar.setVisible(false);
                        String errorMsg = e.getCause() instanceof SQLException ?
                                parseSQLException((SQLException) e.getCause()) : "Registration failed: " + e.getMessage();
                        showErrorDialog(errorMsg);
                        LOGGER.log(Level.SEVERE, "Registration error for email: {0}", new Object[]{email, e});
                    }
                }
            };
            worker.execute();
        } catch (SQLException e) {
            showErrorDialog(parseSQLException(e));
            LOGGER.log(Level.SEVERE, "Database error during email check for: {0}", new Object[]{email, e});
        }
    }

    private String parseSQLException(SQLException e) {
        String message = e.getMessage().toLowerCase();
        if (message.contains("duplicate entry") && message.contains("email")) {
            return "Email already registered";
        } else if (message.contains("foreign key constraint")) {
            return "Invalid user data: related record not found";
        } else {
            return "Database error: " + e.getMessage();
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email);
    }

    private boolean isValidContact(String contact) {
        return contact.matches("\\d{10}");
    }

    private void clearForm() {
        txtFullName.setText("");
        txtEmail.setText("");
        txtContact.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        cmbRole.setSelectedIndex(0);
        resetFieldBorders();
        LOGGER.log(Level.INFO, "Form cleared");
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setFormEnabled(boolean enabled) {
        txtFullName.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtContact.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        txtConfirmPassword.setEnabled(enabled);
        cmbRole.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        btnClear.setEnabled(enabled);
        btnBack.setEnabled(enabled);
    }

    private void resetFieldBorders() {
        Border defaultBorder = BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70), 1), new EmptyBorder(10, 15, 10, 15));
        txtFullName.setBorder(defaultBorder);
        txtEmail.setBorder(defaultBorder);
        txtContact.setBorder(defaultBorder);
        txtPassword.setBorder(defaultBorder);
        txtConfirmPassword.setBorder(defaultBorder);
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
            Color transparent = new Color(0, 0, 0, 0);
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

    private class ValidationListener implements DocumentListener {
        private final JTextField field;
        private final java.util.function.Predicate<String> validator;

        public ValidationListener(JTextField field, java.util.function.Predicate<String> validator) {
            this.field = field;
            this.validator = validator;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            validate();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            validate();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            validate();
        }

        private void validate() {
            String text = field instanceof JPasswordField ?
                    new String(((JPasswordField) field).getPassword()) : field.getText().trim();
            boolean valid = text.isEmpty() || validator.test(text);
            field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(valid ? new Color(70, 70, 70) : ERROR_COLOR, 1),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                RegisterUI frame = new RegisterUI();
                frame.setVisible(true);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Failed to initialize RegisterUI", ex);
                JOptionPane.showMessageDialog(null,
                        "Failed to start registration: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}