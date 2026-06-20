package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite connection helper.
 *
 * No server, no password, no manual schema step:
 * the database lives in a single file (cafeteria.db) right next to the app,
 * and is created + seeded automatically the first time you run the program.
 */
public class DBConnection {

    private static final String DB_FILE = "cafeteria.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    private static boolean initialised = false;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "SQLite JDBC driver not found. Make sure the sqlite-jdbc jar is in the lib/ folder.", e);
        }
        Connection c = DriverManager.getConnection(URL);
        // Enable foreign keys (off by default in SQLite)
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    /** Creates the tables and seed data once, if they don't already exist. */
    public static synchronized void init() {
        if (initialised) return;
        try (Connection c = getConnection(); Statement st = c.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE, phone TEXT NOT NULL, password TEXT NOT NULL," +
                "role TEXT NOT NULL, full_name TEXT NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS menu_items (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL, category TEXT NOT NULL," +
                "price REAL NOT NULL, available INTEGER NOT NULL DEFAULT 1)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customer_id INTEGER NOT NULL, order_time TEXT NOT NULL," +
                "pickup_slot TEXT NOT NULL, status TEXT NOT NULL, total REAL NOT NULL," +
                "FOREIGN KEY (customer_id) REFERENCES users(user_id))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS order_items (" +
                "order_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL, item_id INTEGER NOT NULL," +
                "quantity INTEGER NOT NULL, unit_price REAL NOT NULL," +
                "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE," +
                "FOREIGN KEY (item_id) REFERENCES menu_items(item_id))");

            // Seed only if empty
            boolean empty;
            try (java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                rs.next();
                empty = rs.getInt(1) == 0;
            }
            if (empty) {
st.executeUpdate("INSERT INTO users(username,phone,password,role,full_name) VALUES" +
    "('admin','','admin123','ADMIN','System Administrator')," +
    "('staff','','staff123','STAFF','Kitchen Staff')");

                st.executeUpdate("INSERT INTO menu_items(name,category,price,available) VALUES" +
                    "('Masala Dosa','Breakfast',60.0,1)," +
                    "('Idli Vada Combo','Breakfast',50.0,1)," +
                    "('Veg Thali','Lunch',90.0,1)," +
                    "('Paneer Butter Masala','Lunch',120.0,1)," +
                    "('Veg Fried Rice','Lunch',80.0,1)," +
                    "('Samosa (2 pcs)','Snacks',30.0,1)," +
                    "('Veg Puff','Snacks',25.0,1)," +
                    "('Masala Chai','Beverages',15.0,1)," +
                    "('Filter Coffee','Beverages',20.0,1)," +
                    "('Cold Coffee','Beverages',45.0,1)");
            }
            initialised = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
