package managers;

import models.*;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SINGLETON: one manager coordinates all data access (JDBC) for the system.
 * Every screen talks to the same instance via CafeteriaManager.getInstance().
 */
public class CafeteriaManager {

    private static CafeteriaManager instance;

    private CafeteriaManager() { }   // private constructor -> Singleton

    public static CafeteriaManager getInstance() {
        if (instance == null) instance = new CafeteriaManager();
        return instance;
    }

    // ==================== AUTH ====================
    public Person login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String role = rs.getString("role");
                    String name = rs.getString("full_name");
                    // POLYMORPHISM: build the right subclass for the role
                    switch (role) {
                        case "ADMIN":    return new Admin(id, username, password, name);
                        case "STAFF":    return new Staff(id, username, password, name);
                        default:         return new Customer(id, username, password, name);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // ==================== CUSTOMER AUTH (auto-create + check) ====================
public enum LoginResult { OK, WRONG_PASSWORD }

public static class CustomerLogin {
    public final LoginResult result;
    public final Customer customer;
    CustomerLogin(LoginResult r, Customer c) { this.result = r; this.customer = c; }
}

public CustomerLogin customerLogin(String username, String password) {
    try (Connection c = DBConnection.getConnection()) {
        String find = "SELECT user_id, password, full_name FROM users " +
                      "WHERE username=? AND role='CUSTOMER'";
        try (PreparedStatement ps = c.prepareStatement(find)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {                          // existing account -> check password
                    String stored = rs.getString("password");
                    if (stored.equals(password)) {
                        return new CustomerLogin(LoginResult.OK,
                            new Customer(rs.getInt("user_id"), username, password,
                                         rs.getString("full_name")));
                    } else {
                        return new CustomerLogin(LoginResult.WRONG_PASSWORD, null);
                    }
                }
            }
        }
        String ins = "INSERT INTO users(username,password,role,full_name) " + // new account -> create
                     "VALUES(?,?, 'CUSTOMER', ?)";
        try (PreparedStatement ps =
                     c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int newId = keys.getInt(1);
                return new CustomerLogin(LoginResult.OK,
                    new Customer(newId, username, password, username));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return new CustomerLogin(LoginResult.WRONG_PASSWORD, null);
}

public Person staffLogin(String username, String password) {
    Person p = login(username, password);
    if (p != null && (p.getRole().equals("ADMIN") || p.getRole().equals("STAFF")))
        return p;
    return null;
}

    // ==================== MENU ====================
    public List<MenuItem> getMenu() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY category, name";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MenuItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addMenuItem(String name, String category, double price) {
        String sql = "INSERT INTO menu_items(name,category,price,available) VALUES(?,?,?,TRUE)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean toggleAvailability(int itemId, boolean available) {
        String sql = "UPDATE menu_items SET available=? WHERE item_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteMenuItem(int itemId) {
        String sql = "DELETE FROM menu_items WHERE item_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== ORDERS ====================
    /**
     * Places an order using a TRANSACTION so the order and all its
     * line items are saved together (or not at all).
     */
    public int placeOrder(int customerId, String pickupSlot, List<OrderItem> items) {
        String orderSql = "INSERT INTO orders(customer_id,order_time,pickup_slot,status,total) " +
                          "VALUES(?,?,?, 'PENDING', ?)";
        String itemSql  = "INSERT INTO order_items(order_id,item_id,quantity,unit_price) VALUES(?,?,?,?)";

        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double total = 0;
        for (OrderItem oi : items) total += oi.getSubtotal();

        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);                 // begin transaction

            int orderId;
            try (PreparedStatement ps =
                         c.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setString(2, now);
                ps.setString(3, pickupSlot);
                ps.setDouble(4, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(itemSql)) {
                for (OrderItem oi : items) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, oi.getItemId());
                    ps.setInt(3, oi.getQuantity());
                    ps.setDouble(4, oi.getUnitPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();                             // all good -> commit
            return orderId;
        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return -1;
        } finally {
            try { if (c != null) { c.setAutoCommit(true); c.close(); } }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Order> getAllOrders() { return loadOrders(null); }

    public int reorder(int customerId, String pickupSlot, int pastOrderId) {
    Order past = getOrderById(pastOrderId);
    if (past == null || past.getItems().isEmpty()) return -1;
    List<OrderItem> items = new ArrayList<>();
    for (OrderItem oi : past.getItems()) {                 // rebuild items as a fresh order
        items.add(new OrderItem(0, oi.getItemId(), oi.getItemName(),
                                oi.getQuantity(), oi.getUnitPrice()));
    }
    return placeOrder(customerId, pickupSlot, items);
}

public Order getLastOrder(int customerId) {
    List<Order> list = getOrdersForCustomer(customerId);
    return list.isEmpty() ? null : list.get(0);            // list is newest-first
}

private Order getOrderById(int orderId) {
    String sql = "SELECT o.*, u.full_name FROM orders o " +
                 "JOIN users u ON o.customer_id = u.user_id WHERE o.order_id=?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Order o = new Order(rs.getInt("order_id"), rs.getInt("customer_id"),
                        rs.getString("full_name"), rs.getString("order_time"),
                        rs.getString("pickup_slot"), rs.getString("status"));
                loadItems(c, o);
                return o;
            }
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
}

    public List<Order> getOrdersForCustomer(int customerId) { return loadOrders(customerId); }

    private List<Order> loadOrders(Integer customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name FROM orders o " +
                     "JOIN users u ON o.customer_id = u.user_id " +
                     (customerId == null ? "" : "WHERE o.customer_id=? ") +
                     "ORDER BY o.order_id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (customerId != null) ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getString("full_name"),
                            rs.getString("order_time"),
                            rs.getString("pickup_slot"),
                            rs.getString("status"));
                    loadItems(c, o);
                    orders.add(o);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    private void loadItems(Connection c, Order o) throws SQLException {
        String sql = "SELECT oi.*, m.name FROM order_items oi " +
                     "JOIN menu_items m ON oi.item_id = m.item_id WHERE oi.order_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, o.getOrderId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    o.addItem(new OrderItem(
                            rs.getInt("order_item_id"),
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price")));
                }
            }
        }
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== DASHBOARD STATS ====================
    public int countRow(String table) {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double totalRevenue() {
        String sql = "SELECT IFNULL(SUM(total),0) FROM orders WHERE status='COMPLETED'";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countPendingOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status IN ('PENDING','PREPARING','READY')";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Initialises (creates + seeds) the SQLite DB and verifies connectivity. */
    public boolean testConnection() {
        try (Connection c = DBConnection.getConnection()) {
            boolean ok = c != null && !c.isClosed();
            if (ok) DBConnection.init();   // create tables + seed on first run
            return ok;
        } catch (SQLException e) {
            return false;
        }
    }
}
