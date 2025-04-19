package com.lostfound.ui;

// Login UI for user authentication.
import com.lostfound.model.User;
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

public class LoginUI extends JFrame {
    private UserService userService;
    private static final Logger LOGGER = Logger.getLogger(LoginUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginUI() throws SQLException {
        setTitle("Lost & Found System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        add(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.getAccessibleContext().setAccessibleName("Login Title");
        titleLabel.getAccessibleContext().setAccessibleDescription("Title for login interface");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnClose = new JButton("× Close");
        styleMinimalButton(btnClose);
        btnClose.getAccessibleContext().setAccessibleName("Close");
        btnClose.getAccessibleContext().setAccessibleDescription("Close application");
        btnClose.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Closing LoginUI");
            System.exit(0);
        });
        headerPanel.add(btnClose, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.getAccessibleContext().setAccessibleName("Email Label");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setForeground(TEXT_COLOR);
        emailField.setBackground(CARD_COLOR);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        emailField.getAccessibleContext().setAccessibleName("Email Field");
        emailField.getAccessibleContext().setAccessibleDescription("Enter your email address");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.getAccessibleContext().setAccessibleName("Password Label");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBackground(CARD_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 70, 70)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        passwordField.getAccessibleContext().setAccessibleName("Password Field");
        passwordField.getAccessibleContext().setAccessibleDescription("Enter your password");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        loginButton = stylePrimaryButton("Login");
        loginButton.getAccessibleContext().setAccessibleName("Login Button");
        loginButton.getAccessibleContext().setAccessibleDescription("Log in to the application");
        loginButton.addActionListener(e -> performLogin());
        buttonPanel.add(loginButton);

        registerButton = stylePrimaryButton("Register");
        registerButton.getAccessibleContext().setAccessibleName("Register Button");
        registerButton.getAccessibleContext().setAccessibleDescription("Open registration form");
        registerButton.addActionListener(e -> openRegisterUI());
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        LOGGER.log(Level.INFO, "Attempting login with email: {0}", email);

        if (email.isEmpty() || password.isEmpty()) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Please enter both email and password.",
                            "Error", JOptionPane.ERROR_MESSAGE));
            return;
        }
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                            "Error", JOptionPane.ERROR_MESSAGE));
            return;
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        try {
            User user = userService.login(email, password);
            if (user != null) {
                LOGGER.log(Level.INFO, "Opening UserPanelUI for user: {0}", user.getEmail());
                SwingUtilities.invokeLater(() -> {
                    try {
                        new UserPanelUI().setVisible(true);
                        dispose();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Failed to open UserPanelUI", ex);
                        JOptionPane.showMessageDialog(this, "Error opening dashboard: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
            });
        }
    }

    private void openRegisterUI() {
        LOGGER.log(Level.INFO, "Navigating to RegisterUI");
        SwingUtilities.invokeLater(() -> {
            try {
                new RegisterUI().setVisible(true);
                dispose();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to open RegisterUI", ex);
                JOptionPane.showMessageDialog(this, "Error opening Register: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JButton stylePrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_COLOR.darker().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2.setColor(PRIMARY_COLOR);
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
                new LoginUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(LoginUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}