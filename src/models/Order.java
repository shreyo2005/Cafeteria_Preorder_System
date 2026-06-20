package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Order demonstrates COMPOSITION: it owns a list of OrderItem objects.
 * When an Order is removed, its items go with it.
 */
public class Order {
    private int orderId;
    private int customerId;
    private String customerName;
    private String orderTime;
    private String pickupSlot;
    private String status;          // PENDING / PREPARING / READY / COMPLETED / CANCELLED
    private List<OrderItem> items;

    public Order(int orderId, int customerId, String customerName,
                 String orderTime, String pickupSlot, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.orderTime = orderTime;
        this.pickupSlot = pickupSlot;
        this.status = status;
        this.items = new ArrayList<>();
    }

    public void addItem(OrderItem item) { items.add(item); }

    /** Total computed from composed items. */
    public double getTotal() {
        double t = 0;
        for (OrderItem oi : items) t += oi.getSubtotal();
        return t;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getOrderTime() { return orderTime; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }

    public String getPickupSlot() { return pickupSlot; }
    public void setPickupSlot(String pickupSlot) { this.pickupSlot = pickupSlot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    private String paymentStatus = "UNPAID";   // PAID / UNPAID
private String paymentMethod = "-";        // UPI / Card / Cash / -

public String getPaymentStatus() { return paymentStatus; }
public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

public String getPaymentMethod() { return paymentMethod; }
public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
