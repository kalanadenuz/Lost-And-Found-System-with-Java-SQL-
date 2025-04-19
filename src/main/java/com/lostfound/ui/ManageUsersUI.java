package com.lostfound.ui;

// UI for admin to manage users (delete, promote/demote admin).
import com.lostfound.model.User;
import com.lostfound.service.UserService;
import com.lostfound.service.AdminService;
import com.lostfound.model.Admin;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageUsersUI extends JFrame {
    private JTable userTable;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JLabel statusLabel;
    private UserService userService;
    private AdminService adminService;
    private static final Logger LOGGER = Logger.getLogger(ManageUsersUI.class.getName());
    private static final Color BACKGROUND_DARK = new Color(32, 34, 37);
    private static final Color CARD_COLOR = new Color(44, 47, 51);
    private static final Color PRIMARY_COLOR = new Color(0, 168, 150);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color SECONDARY_TEXT = new Color(180, 180, 180);

    public ManageUsersUI() throws SQLException {
        setTitle("User Management - Lost & Found");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));

        userService = new UserService();
        adminService = new AdminService();

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            LOGGER.log(Level.WARNING, "No user logged in, redirecting to LoginUI");
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening LoginUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            return;
        }
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.log(Level.WARNING, "Non-admin user attempted to access ManageUsersUI: {0}", currentUser.getEmail());
            JOptionPane.showMessageDialog(this, "Access denied: Admin privileges required", "Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserPanelUI().setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening UserPanelUI", ex);
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            return;
        }

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("Loading users...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(SECONDARY_TEXT);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        userTable = new JTable();
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setRowHeight(25);
        userTable.setBackground(CARD_COLOR);
        userTable.setForeground(TEXT_COLOR);
        userTable.setSelectionBackground(PRIMARY_COLOR);
        userTable.setGridColor(new Color(70, 70, 70));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(new Color(50, 54, 59));
        userTable.getTableHeader().setForeground(TEXT_COLOR);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        String[] columnNames = {"ID", "Username", "Email", "Contact", "Role"};
        DefaultTableModel emptyModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        emptyModel.addRow(new Object[]{"", "Loading users...", "", "", ""});
        userTable.setModel(emptyModel);

        scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnBack = createStyledButton("Back to Admin Panel", new Color(255, 165, 0));
        JButton btnDelete = createStyledButton("Delete User", PRIMARY_COLOR);
        JButton btnMakeAdmin = createStyledButton("Make Admin", new Color(120, 111, 253));
        JButton btnDemoteAdmin = createStyledButton("Demote Admin", new Color(220, 53, 69));
        JButton btnRefresh = createStyledButton("Refresh", new Color(70, 200, 150));
        JButton btnDashboard = createStyledButton("Back to Dashboard", SECONDARY_TEXT);
        JButton btnLogout = createStyledButton("Logout", SECONDARY_TEXT);
        JButton btnClose = createStyledButton("Close", SECONDARY_TEXT);

        buttonPanel.add(btnBack);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnMakeAdmin);
        buttonPanel.add(btnDemoteAdmin);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnDashboard);
        buttonPanel.add(btnLogout);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> {
            try {
                navigateTo(new AdminPanelUI(), "Admin Panel");
            } catch (IOException ex) {
                Logger.getLogger(ManageUsersUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ManageUsersUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                deleteUser();
            } catch (SQLException ex) {
                showErrorDialog("Failed to delete user: " + ex.getMessage());
            }
        });
        btnMakeAdmin.addActionListener(e -> {
            try {
                makeAdmin();
            } catch (SQLException ex) {
                showErrorDialog(ex.getMessage().contains("already exists") ?
                        "User is already an admin." : "Failed to promote user: " + ex.getMessage());
            }
        });
        btnDemoteAdmin.addActionListener(e -> {
            try {
                demoteAdmin();
            } catch (SQLException ex) {
                showErrorDialog("Failed to demote user: " + ex.getMessage());
            }
        });
        btnRefresh.addActionListener(e -> refreshData());
        btnDashboard.addActionListener(e -> {
            try {
                navigateTo(new UserPanelUI(), "Dashboard");
            } catch (SQLException ex) {
                Logger.getLogger(ManageUsersUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnLogout.addActionListener(e -> logout());
        btnClose.addActionListener(e -> System.exit(0));

        refreshData();
    }

    private void loadUserData() throws SQLException {
        LOGGER.log(Level.INFO, "Loading user data...");
        statusLabel.setText("Loading users...");
        statusLabel.setForeground(SECONDARY_TEXT);

        List<User> users;
        List<Admin> admins;
        try {
            users = userService.getAllUsers();
            admins = adminService.getAllAdmins();
            LOGGER.log(Level.INFO, "Fetched {0} users and {1} admins", new Object[]{users.size(), admins.size()});
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching data: {0}", ex.getMessage());
            statusLabel.setText("Failed to load users: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            DefaultTableModel errorModel = new DefaultTableModel(new String[]{"ID", "Username", "Email", "Contact", "Role"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            errorModel.addRow(new Object[]{"", "Error: " + ex.getMessage(), "", "", ""});
            userTable.setModel(errorModel);
            updateTableUI();
            throw ex;
        }

        String[] columnNames = {"ID", "Username", "Email", "Contact", "Role"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (users == null || users.isEmpty()) {
            LOGGER.log(Level.WARNING, "No users found");
            model.addRow(new Object[]{"", "No users found", "", "", ""});
            statusLabel.setText("No users found in the database.");
            statusLabel.setForeground(Color.YELLOW);
        } else {
            for (User user : users) {
                String name = user.getName() != null ? user.getName() : "";
                String email = user.getEmail() != null ? user.getEmail() : "";
                String contact = user.getContact() != null ? user.getContact() : "";
                String role = user.getRole() != null ? user.getRole() : "user";
                boolean isAdmin = admins != null && admins.stream().anyMatch(admin -> admin.getUserId() == user.getUserId());
                model.addRow(new Object[]{user.getUserId(), name, email, contact, isAdmin ? "admin" : role});
            }
            statusLabel.setText("Loaded " + users.size() + " users.");
            statusLabel.setForeground(PRIMARY_COLOR);
        }

        userTable.setModel(model);
        LOGGER.log(Level.INFO, "Table model set with {0} rows", model.getRowCount());

        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        updateTableUI();
    }

    private void updateTableUI() {
        SwingUtilities.invokeLater(() -> {
            userTable.revalidate();
            userTable.repaint();
            scrollPane.revalidate();
            scrollPane.repaint();
            mainPanel.revalidate();
            mainPanel.repaint();
            LOGGER.log(Level.INFO, "Table UI updated, visible: {0}, rows: {1}, table size: {2}, scrollPane size: {3}",
                    new Object[]{userTable.isVisible(), userTable.getRowCount(), userTable.getSize(), scrollPane.getSize()});
        });
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(baseColor.equals(SECONDARY_TEXT) ? PRIMARY_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private void deleteUser() throws SQLException {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a user to delete.");
            LOGGER.log(Level.WARNING, "No user selected for deletion");
            return;
        }

        Object idValue = userTable.getValueAt(selectedRow, 0);
        if (idValue == null || idValue.toString().isEmpty()) {
            showErrorDialog("Cannot delete placeholder row.");
            LOGGER.log(Level.WARNING, "Attempted to delete placeholder row");
            return;
        }

        int userId = Integer.parseInt(idValue.toString());
        String email = (String) userTable.getValueAt(selectedRow, 2);
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && userId == currentUser.getUserId()) {
            showErrorDialog("Cannot delete your own account.");
            LOGGER.log(Level.WARNING, "Attempted to delete own account: {0}", email);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete user: " + email + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userService.deleteUser(userId);
            refreshData();
            JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Deleted user ID: {0}, Email: {1}", new Object[]{userId, email});
        }
    }

    private void makeAdmin() throws SQLException {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a user to promote.");
            LOGGER.log(Level.WARNING, "No user selected for admin promotion");
            return;
        }

        Object idValue = userTable.getValueAt(selectedRow, 0);
        if (idValue == null || idValue.toString().isEmpty()) {
            showErrorDialog("Cannot promote placeholder row.");
            LOGGER.log(Level.WARNING, "Attempted to promote placeholder row");
            return;
        }

        int userId = Integer.parseInt(idValue.toString());
        String email = (String) userTable.getValueAt(selectedRow, 2);
        if (isUserAdmin(userId)) {
            JOptionPane.showMessageDialog(this, "User is already an admin.", "Info", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "User already admin: {0}", email);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Promote " + email + " to admin?", "Confirm Promotion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Admin admin = new Admin(0, userId, "Moderator");
            if (adminService.addAdmin(admin)) {
                userService.updateUserRole(userId, "admin");
                refreshData();
                JOptionPane.showMessageDialog(this, "User promoted to admin.", "Success", JOptionPane.INFORMATION_MESSAGE);
                LOGGER.log(Level.INFO, "Promoted user ID: {0}, Email: {1}", new Object[]{userId, email});
            } else {
                throw new SQLException("Failed to add admin record");
            }
        }
    }

    private void demoteAdmin() throws SQLException {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a user to demote.");
            LOGGER.log(Level.WARNING, "No user selected for admin demotion");
            return;
        }

        Object idValue = userTable.getValueAt(selectedRow, 0);
        if (idValue == null || idValue.toString().isEmpty()) {
            showErrorDialog("Cannot demote placeholder row.");
            LOGGER.log(Level.WARNING, "Attempted to demote placeholder row");
            return;
        }

        int userId = Integer.parseInt(idValue.toString());
        String email = (String) userTable.getValueAt(selectedRow, 2);
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && userId == currentUser.getUserId()) {
            showErrorDialog("Cannot demote your own account.");
            LOGGER.log(Level.WARNING, "Attempted to demote own account: {0}", email);
            return;
        }

        if (!isUserAdmin(userId)) {
            JOptionPane.showMessageDialog(this, "User is not an admin.", "Info", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "User not admin: {0}", email);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Demote " + email + " from admin?", "Confirm Demotion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Admin> admins = adminService.getAllAdmins();
            Admin adminToDelete = admins.stream().filter(admin -> admin.getUserId() == userId).findFirst().orElse(null);
            if (adminToDelete != null && adminService.deleteAdmin(adminToDelete.getAdminId())) {
                userService.updateUserRole(userId, "user");
                refreshData();
                JOptionPane.showMessageDialog(this, "User demoted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                LOGGER.log(Level.INFO, "Demoted user ID: {0}, Email: {1}", new Object[]{userId, email});
            } else {
                throw new SQLException("Failed to delete admin record");
            }
        }
    }

    private boolean isUserAdmin(int userId) throws SQLException {
        List<Admin> admins = adminService.getAllAdmins();
        return admins != null && admins.stream().anyMatch(admin -> admin.getUserId() == userId);
    }

    private void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                loadUserData();
                statusLabel.setText("Data refreshed successfully.");
                statusLabel.setForeground(PRIMARY_COLOR);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error refreshing data: {0}", ex.getMessage());
                statusLabel.setText("Failed to refresh: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
                showErrorDialog("Failed to refresh data: " + ex.getMessage());
            }
        });
    }

    private void navigateTo(JFrame frame, String destination) {
        LOGGER.log(Level.INFO, "Navigating to {0}", destination);
        int confirm = JOptionPane.showConfirmDialog(this, "Go to " + destination + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                frame.setVisible(true);
                dispose();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error navigating to {0}: {1}", new Object[]{destination, ex.getMessage()});
                showErrorDialog("Error opening " + destination + ": " + ex.getMessage());
            }
        }
    }

    private void logout() {
        LOGGER.log(Level.INFO, "Logging out user");
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userService.logout();
            try {
                new LoginUI().setVisible(true);
                dispose();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error opening LoginUI: {0}", ex.getMessage());
                showErrorDialog("Error opening login page: " + ex.getMessage());
            }
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ManageUsersUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(ManageUsersUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}