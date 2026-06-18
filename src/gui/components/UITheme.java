package gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Shared colours, fonts and small reusable UI helpers (light theme). */
public class UITheme {

    public static final Color BG        = new Color(0xF4, 0xF6, 0xF9);
    public static final Color CARD      = Color.WHITE;
    //public static final Color PRIMARY   = new Color(0x2E, 0x7D, 0x32);  // green
    public static final Color PRIMARY_D = new Color(0x1B, 0x5E, 0x20);
    //public static final Color ACCENT    = new Color(0xFF, 0x8F, 0x00);  // amber
    public static final Color TEXT      = new Color(0x21, 0x21, 0x21);
    public static final Color MUTED     = new Color(0x75, 0x75, 0x75);
    public static final Color BORDER    = new Color(0xE0, 0xE0, 0xE0);
    public static final Color PRIMARY   = new Color(0x1B, 0x8A, 0x3C);  // bolder green
public static final Color ACCENT    = new Color(0xE6, 0x5A, 0x00);  // strong orange

    public static final Font H1   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font H2   = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);

 public static JButton primaryButton(String text) {
    JButton b = new JButton(text);
    b.setBackground(PRIMARY);
    b.setForeground(Color.WHITE);
    b.setFont(BOLD);
    b.setOpaque(true);                 // force our background to paint
    b.setContentAreaFilled(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_D, 1),
            new EmptyBorder(10, 18, 10, 18)));
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return b;
}
public static JButton flatButton(String text, Color bg) {
    JButton b = new JButton(text);
    b.setBackground(bg);
    b.setForeground(Color.WHITE);
    b.setFont(BOLD);
    b.setOpaque(true);                 // force our background to paint
    b.setContentAreaFilled(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            new EmptyBorder(8, 14, 8, 14)));
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return b;
}

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    /** Coloured stat card for the dashboard. */
    public static JPanel statCard(String title, String value, Color accent) {
        JPanel p = card();
        p.setLayout(new BorderLayout(0, 8));
        p.setPreferredSize(new Dimension(200, 100));

        JLabel t = new JLabel(title);
        t.setFont(BODY);
        t.setForeground(MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(accent);

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}
