package gui;

import gui.components.UITheme;
import managers.CafeteriaManager;
import models.Person;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final CafeteriaManager manager = CafeteriaManager.getInstance();

    public LoginFrame() {
        setTitle("Cafeteria Preorder System - Login");
        setSize(420, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(UITheme.BG);
        root.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel logo = new JLabel("\uD83C\uDF7D\uFE0F", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Cafeteria Preorder");
        title.setFont(UITheme.H1);
        title.setForeground(UITheme.PRIMARY_D);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(UITheme.BODY);
        sub.setForeground(UITheme.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();      // Name (display label)
        nameField.setMaximumSize(new Dimension(300, 38));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(6, 10, 6, 10)));

        JTextField phoneField = new JTextField();     // Phone (unique identifier)
        phoneField.setMaximumSize(new Dimension(300, 38));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(6, 10, 6, 10)));

        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 38));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(6, 10, 6, 10)));

        JButton loginBtn = UITheme.primaryButton("Login");
        loginBtn.setMaximumSize(new Dimension(300, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("<html><center>Staff: type admin/admin123 or staff/staff123 in Name &amp; Password<br>"
                + "Customers: enter name, phone &amp; password to sign up,<br>"
                + "then use the same phone &amp; password to log in next time.</center></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(UITheme.MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(logo);
        root.add(Box.createVerticalStrut(8));
        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(sub);
        root.add(Box.createVerticalStrut(24));
        root.add(label("Name"));
        root.add(Box.createVerticalStrut(4));
        root.add(nameField);
        root.add(Box.createVerticalStrut(14));
        root.add(label("Phone"));
        root.add(Box.createVerticalStrut(4));
        root.add(phoneField);
        root.add(Box.createVerticalStrut(14));
        root.add(label("Password"));
        root.add(Box.createVerticalStrut(4));
        root.add(passField);
        root.add(Box.createVerticalStrut(22));
        root.add(loginBtn);
        root.add(Box.createVerticalStrut(18));
        root.add(hint);

        add(root);

        Runnable doLogin = () -> {
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String p     = new String(passField.getPassword());

            // 1) Admin / staff: type their name in the Name box (no phone needed)
            Person staff = manager.staffLogin(name, p);
            if (staff != null) {
                dispose();
                new MainFrame(staff).setVisible(true);
                return;
            }

            // 2) Seeded name with wrong password
            if (name.equals("admin") || name.equals("staff")) {
                JOptionPane.showMessageDialog(this, "Wrong password for " + name + ".",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3) Customer: needs name, phone, and password
            if (name.isEmpty() || phone.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter your name, phone, and password.");
                return;
            }

            CafeteriaManager.CustomerLogin cl = manager.customerLogin(name, phone, p);
            if (cl.result == CafeteriaManager.LoginResult.OK) {
                dispose();
                new MainFrame(cl.customer).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "This phone number is already registered and the password is incorrect.",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        };
        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(UITheme.BOLD);
        l.setForeground(UITheme.TEXT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(300, 20));
        return l;
    }
}