import gui.LoginFrame;
import managers.CafeteriaManager;

import javax.swing.*;

/**
 * Cafeteria Preorder System - entry point.
 * OOP mini-project (Java + Swing + JDBC/MySQL).
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            // Verify DB connectivity before showing the UI
            if (!CafeteriaManager.getInstance().testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Cannot initialise the database.\n\n" +
                        "Make sure the SQLite connector jar is in the lib/ folder\n" +
                        "(sqlite-jdbc-x.x.x.jar). No MySQL server is needed \u2014\n" +
                        "the database file (cafeteria.db) is created automatically.",
                        "Database error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new LoginFrame().setVisible(true);
        });
    }
}
