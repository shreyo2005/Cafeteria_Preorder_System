package gui;

import gui.components.UITheme;
import managers.CafeteriaManager;
import models.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import models.MenuItem;   // disambiguate from java.awt.MenuItem
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private final CafeteriaManager manager = CafeteriaManager.getInstance();
    private final Person user;

    public MainFrame(Person user) {
        this.user = user;
        setTitle("Cafeteria Preorder System - " + user.getRole());
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BOLD);
        tabs.setBackground(UITheme.BG);

        // Role-based screens
        if (user.getRole().equals("ADMIN")) {
            tabs.addTab("Dashboard", buildDashboard());
            tabs.addTab("Menu", buildMenuManagement());
            tabs.addTab("All Orders", buildOrderManagement(false));
        } else if (user.getRole().equals("STAFF")) {
            tabs.addTab("Dashboard", buildDashboard());
            tabs.addTab("Kitchen Orders", buildOrderManagement(true));
        } else { // CUSTOMER
            tabs.addTab("Place Order", buildPlaceOrder());
            tabs.addTab("My Orders", buildMyOrders());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // ---------------- Header ----------------
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.PRIMARY);
        h.setBorder(new EmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("\uD83C\uDF7D\uFE0F  Cafeteria Preorder System");
        title.setFont(UITheme.H2);
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JLabel who = new JLabel(user.getFullName() + "  (" + user.getRole() + ")");
        who.setFont(UITheme.BODY);
        who.setForeground(Color.WHITE);
        JButton logout = UITheme.flatButton("Logout", UITheme.PRIMARY_D);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        right.add(who);
        right.add(logout);

        h.add(title, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ---------------- Dashboard ----------------
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setBackground(UITheme.BG);

        grid.add(UITheme.statCard("Menu Items", String.valueOf(manager.countRow("menu_items")), UITheme.PRIMARY));
        grid.add(UITheme.statCard("Customers", String.valueOf(customerCount()), UITheme.ACCENT));
        grid.add(UITheme.statCard("Total Orders", String.valueOf(manager.countRow("orders")), new Color(0x15,0x65,0xC0)));
        grid.add(UITheme.statCard("Active Orders", String.valueOf(manager.countPendingOrders()), new Color(0xE5,0x39,0x35)));
        grid.add(UITheme.statCard("Revenue", "Rs." + manager.totalRevenue(), UITheme.PRIMARY_D));

        JButton refresh = UITheme.primaryButton("Refresh");
        refresh.addActionListener(e -> { /* simplest refresh: rebuild frame */ rebuild(); });

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BG);
        JLabel t = new JLabel("Overview");
        t.setFont(UITheme.H1);
        t.setForeground(UITheme.TEXT);
        top.add(t, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        p.add(top, BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private int customerCount() {
        int total = manager.countRow("users");
        return total; // includes staff/admin; fine for a demo overview
    }

    // ---------------- Menu management (ADMIN) ----------------
    private JPanel buildMenuManagement() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UITheme.BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"ID", "Name", "Category", "Price", "Available"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(UITheme.BODY);
        loadMenuIntoTable(model);

        // add-item form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        form.setBackground(UITheme.BG);
        JTextField name = new JTextField(14);
        JComboBox<String> cat = new JComboBox<>(new String[]{"Breakfast","Lunch","Snacks","Beverages"});
        JTextField price = new JTextField(6);
        JButton add = UITheme.primaryButton("Add Item");
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Category:")); form.add(cat);
        form.add(new JLabel("Price:")); form.add(price);
        form.add(add);

        add.addActionListener(e -> {
            try {
                double pr = Double.parseDouble(price.getText().trim());
                if (name.getText().trim().isEmpty()) throw new NumberFormatException();
                if (manager.addMenuItem(name.getText().trim(), (String) cat.getSelectedItem(), pr)) {
                    name.setText(""); price.setText("");
                    loadMenuIntoTable(model);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid name and price.");
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(UITheme.BG);
        JButton toggle = UITheme.flatButton("Toggle Available", UITheme.ACCENT);
        JButton delete = UITheme.flatButton("Delete", new Color(0xE5,0x39,0x35));
        actions.add(toggle); actions.add(delete);

        toggle.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (int) model.getValueAt(r, 0);
            boolean avail = "Yes".equals(model.getValueAt(r, 4));
            manager.toggleAvailability(id, !avail);
            loadMenuIntoTable(model);
        });
        delete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (int) model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                manager.deleteMenuItem(id);
                loadMenuIntoTable(model);
            }
        });

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setBackground(UITheme.BG);
        bottom.add(form, BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.SOUTH);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadMenuIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (MenuItem m : manager.getMenu()) {
            model.addRow(new Object[]{
                    m.getItemId(), m.getName(), m.getCategory(),
                    "Rs." + m.getPrice(), m.isAvailable() ? "Yes" : "No"});
        }
    }

    // ---------------- Place order (CUSTOMER) ----------------
    private JPanel buildPlaceOrder() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBackground(UITheme.BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        Map<Integer, Integer> cart = new HashMap<>();   // itemId -> qty

        // menu table (available only)
        String[] cols = {"ID", "Name", "Category", "Price"};
        DefaultTableModel menuModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<MenuItem> menu = manager.getMenu();
        Map<Integer, MenuItem> byId = new HashMap<>();
        for (MenuItem m : menu) {
            byId.put(m.getItemId(), m);
            if (m.isAvailable())
                menuModel.addRow(new Object[]{m.getItemId(), m.getName(), m.getCategory(), "Rs." + m.getPrice()});
        }
        JTable menuTable = new JTable(menuModel);
        menuTable.setRowHeight(28);
        menuTable.setFont(UITheme.BODY);

        // cart area
        DefaultListModel<String> cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        cartList.setFont(UITheme.BODY);
        JLabel totalLbl = new JLabel("Total: Rs.0.0");
        totalLbl.setFont(UITheme.H2);
        totalLbl.setForeground(UITheme.PRIMARY_D);

        Runnable refreshCart = () -> {
            cartModel.clear();
            double total = 0;
            for (Map.Entry<Integer,Integer> en : cart.entrySet()) {
                MenuItem m = byId.get(en.getKey());
                double sub = m.getPrice() * en.getValue();
                total += sub;
                cartModel.addElement(m.getName() + "  x" + en.getValue() + "   Rs." + sub);
            }
            totalLbl.setText("Total: Rs." + total);
        };

        JButton addToCart = UITheme.primaryButton("Add to Cart");
        addToCart.addActionListener(e -> {
            int r = menuTable.getSelectedRow();
            if (r < 0) return;
            int id = (int) menuModel.getValueAt(r, 0);
            String qStr = JOptionPane.showInputDialog(this, "Quantity:", "1");
            if (qStr == null) return;
            try {
                int q = Integer.parseInt(qStr.trim());
                if (q <= 0) return;
                cart.merge(id, q, Integer::sum);
                refreshCart.run();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
            }
        });

        JButton clear = UITheme.flatButton("Clear Cart", new Color(0xE5,0x39,0x35));
        clear.addActionListener(e -> { cart.clear(); refreshCart.run(); });

        JComboBox<String> slot = new JComboBox<>(new String[]{
                "12:00 PM","12:30 PM","1:00 PM","1:30 PM","5:00 PM","5:30 PM"});
        JButton placeBtn = UITheme.primaryButton("Place Order");
        placeBtn.addActionListener(e -> {
            if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty."); return; }
            java.util.List<OrderItem> items = new java.util.ArrayList<>();
            for (Map.Entry<Integer,Integer> en : cart.entrySet()) {
                MenuItem m = byId.get(en.getKey());
                items.add(new OrderItem(0, m.getItemId(), m.getName(), en.getValue(), m.getPrice()));
            }
            int orderId = manager.placeOrder(user.getId(), (String) slot.getSelectedItem(), items);
            if (orderId > 0) {
                double total = 0;
                for (OrderItem oi : items) total += oi.getSubtotal();
                cart.clear(); refreshCart.run();
                int choice = JOptionPane.showConfirmDialog(this,
                        "Order #" + orderId + " placed! Pickup at " + slot.getSelectedItem() +
                        "\nTotal: Rs." + total + "\n\nPay online now?",
                        "Order placed", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    payNow(orderId, total);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "You can pay later from the My Orders tab, or pay cash at the counter.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not place order.");
            }
        });

        // left = menu, right = cart
        JPanel left = new JPanel(new BorderLayout(0, 8));
        left.setBackground(UITheme.BG);
        JLabel ml = new JLabel("Menu"); ml.setFont(UITheme.H2); ml.setForeground(UITheme.TEXT);
        left.add(ml, BorderLayout.NORTH);
        left.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        left.add(addToCart, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(UITheme.BG);
        right.setPreferredSize(new Dimension(320, 0));
        JLabel cl = new JLabel("Your Cart"); cl.setFont(UITheme.H2); cl.setForeground(UITheme.TEXT);
        right.add(cl, BorderLayout.NORTH);
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel checkout = new JPanel();
        checkout.setLayout(new BoxLayout(checkout, BoxLayout.Y_AXIS));
        checkout.setBackground(UITheme.BG);
        totalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel slotRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        slotRow.setBackground(UITheme.BG);
        slotRow.add(new JLabel("Pickup:")); slotRow.add(slot);
        slotRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkout.add(totalLbl);
        checkout.add(Box.createVerticalStrut(8));
        checkout.add(slotRow);
        checkout.add(Box.createVerticalStrut(8));
        clear.setAlignmentX(Component.LEFT_ALIGNMENT);
        placeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkout.add(clear);
        checkout.add(Box.createVerticalStrut(6));
        checkout.add(placeBtn);
        right.add(checkout, BorderLayout.SOUTH);

        p.add(left, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ---------------- Orders (ADMIN sees all, STAFF kitchen view) ----------------
    private JPanel buildOrderManagement(boolean kitchen) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UITheme.BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"Order #", "Customer", "Items", "Pickup", "Status", "Total", "Payment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UITheme.BODY);
        loadOrdersIntoTable(model, manager.getAllOrders());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(UITheme.BG);
        String[] next = {"PENDING","PREPARING","READY","COMPLETED","CANCELLED"};
        JComboBox<String> statusBox = new JComboBox<>(next);
        JButton update = UITheme.primaryButton("Update Status");
        JButton cashPaid = UITheme.flatButton("Mark Cash Paid", new Color(0x00,0x89,0x7B));
        JButton refresh = UITheme.flatButton("Refresh", new Color(0x15,0x65,0xC0));
        actions.add(new JLabel("Set status:")); actions.add(statusBox);
        actions.add(update); actions.add(cashPaid); actions.add(refresh);

        update.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (int) model.getValueAt(r, 0);
            manager.updateOrderStatus(id, (String) statusBox.getSelectedItem());
            loadOrdersIntoTable(model, manager.getAllOrders());
        });
        cashPaid.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select an order."); return; }
            int id = (int) model.getValueAt(r, 0);
            if (((String) model.getValueAt(r, 6)).startsWith("PAID")) {
                JOptionPane.showMessageDialog(this, "This order is already paid.");
                return;
            }
            manager.payOrder(id, "Cash");
            loadOrdersIntoTable(model, manager.getAllOrders());
        });
        refresh.addActionListener(e -> loadOrdersIntoTable(model, manager.getAllOrders()));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    // ---------------- My orders (CUSTOMER) ----------------
    private JPanel buildMyOrders() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UITheme.BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"Order #", "Items", "Pickup", "Status", "Total", "Payment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UITheme.BODY);

        Runnable load = () -> {
            model.setRowCount(0);
            for (Order o : manager.getOrdersForCustomer(user.getId())) {
                String pay = o.getPaymentStatus().equals("PAID")
                        ? "PAID (" + o.getPaymentMethod() + ")" : "UNPAID";
                model.addRow(new Object[]{
                        o.getOrderId(), itemsSummary(o), o.getPickupSlot(),
                        o.getStatus(), "Rs." + o.getTotal(), pay});
            }
        };
        load.run();

        JButton refresh = UITheme.flatButton("Refresh", new Color(0x15,0x65,0xC0));
        refresh.addActionListener(e -> load.run());

        JButton payBtn = UITheme.flatButton("Pay Now", new Color(0x00,0x89,0x7B));
        payBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select an order to pay for."); return; }
            int orderId = (int) model.getValueAt(r, 0);
            String payCell = (String) model.getValueAt(r, 5);
            if (payCell.startsWith("PAID")) {
                JOptionPane.showMessageDialog(this, "This order is already paid.");
                return;
            }
            String totalStr = ((String) model.getValueAt(r, 4)).replace("Rs.", "");
            double amount = Double.parseDouble(totalStr);
            if (payNow(orderId, amount)) load.run();
        });

        JComboBox<String> slot = new JComboBox<>(new String[]{
                "12:00 PM","12:30 PM","1:00 PM","1:30 PM","5:00 PM","5:30 PM"});

        JButton reorderSel = UITheme.primaryButton("Reorder Selected");
        reorderSel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select an order to repeat."); return; }
            int orderId = (int) model.getValueAt(r, 0);
            int newId = manager.reorder(user.getId(), (String) slot.getSelectedItem(), orderId);
            if (newId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Reordered! New order #" + newId + " for pickup at " + slot.getSelectedItem());
                load.run();
            } else {
                JOptionPane.showMessageDialog(this, "Could not reorder.");
            }
        });

        JButton reorderLast = UITheme.flatButton("Repeat Last Order", UITheme.ACCENT);
        reorderLast.addActionListener(e -> {
            Order last = manager.getLastOrder(user.getId());
            if (last == null) { JOptionPane.showMessageDialog(this, "You have no previous orders yet."); return; }
            int newId = manager.reorder(user.getId(), (String) slot.getSelectedItem(), last.getOrderId());
            if (newId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Repeated your last order (#" + last.getOrderId() + ").\n" +
                        "New order #" + newId + " for pickup at " + slot.getSelectedItem());
                load.run();
            } else {
                JOptionPane.showMessageDialog(this, "Could not reorder.");
            }
        });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(UITheme.BG);
        bar.add(new JLabel("Pickup:"));
        bar.add(slot);
        bar.add(reorderSel);
        bar.add(reorderLast);
        bar.add(payBtn);
        bar.add(refresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private void loadOrdersIntoTable(DefaultTableModel model, List<Order> orders) {
        model.setRowCount(0);
        for (Order o : orders) {
            String pay = o.getPaymentStatus().equals("PAID")
                    ? "PAID (" + o.getPaymentMethod() + ")" : "UNPAID";
            model.addRow(new Object[]{
                    o.getOrderId(), o.getCustomerName(), itemsSummary(o),
                    o.getPickupSlot(), o.getStatus(), "Rs." + o.getTotal(), pay});
        }
    }

    /** Simulated payment: pick a method, "process" it, mark the order PAID. */
    private boolean payNow(int orderId, double amount) {
        String[] methods = {"UPI", "Card"};
        String method = (String) JOptionPane.showInputDialog(this,
                "Order #" + orderId + "\nAmount: Rs." + amount + "\n\nChoose payment method:",
                "Pay Online", JOptionPane.PLAIN_MESSAGE, null, methods, methods[0]);
        if (method == null) return false;   // cancelled

        JOptionPane.showMessageDialog(this,
                "Processing " + method + " payment of Rs." + amount + " ...",
                "Please wait", JOptionPane.INFORMATION_MESSAGE);

        if (manager.payOrder(orderId, method)) {
            JOptionPane.showMessageDialog(this,
                    "Payment successful!\nOrder #" + orderId + " is now PAID via " + method +
                    ".\nJust pick it up and go.",
                    "Payment complete", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Payment could not be recorded.");
            return false;
        }
    }

    private String itemsSummary(Order o) {
        StringBuilder sb = new StringBuilder();
        for (OrderItem oi : o.getItems()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(oi.getItemName()).append(" x").append(oi.getQuantity());
        }
        return sb.toString();
    }

    private void rebuild() {
        dispose();
        new MainFrame(user).setVisible(true);
    }
}
