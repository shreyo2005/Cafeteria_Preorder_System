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
        setSize(420, 520);
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

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 38));
        userField.setBorder(BorderFactory.createCompoundBorder(
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

        JLabel hint = new JLabel("<html><center>admin/admin123 &nbsp; staff/staff123<br>shreyo/pass123</center></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(UITheme.MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(logo);
        root.add(Box.createVerticalStrut(8));
        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(sub);
        root.add(Box.createVerticalStrut(28));
        root.add(label("Username"));
        root.add(Box.createVerticalStrut(4));
        root.add(userField);
        root.add(Box.createVerticalStrut(16));
        root.add(label("Password"));
        root.add(Box.createVerticalStrut(4));
        root.add(passField);
        root.add(Box.createVerticalStrut(24));
        root.add(loginBtn);
        root.add(Box.createVerticalStrut(20));
        root.add(hint);

        add(root);

        Runnable doLogin = () -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username and password.");
                return;
            }
            Person person = manager.login(u, p);
            if (person == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
            } else {
                dispose();
                new MainFrame(person).setVisible(true);
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
