package models;

/**
 * COMPOSITION: an Order is composed of OrderItem objects.
 * Each line stores the item, quantity and the price at order time.
 */
public class OrderItem {
    private int orderItemId;
    private int itemId;
    private String itemName;
    private int quantity;
    private double unitPrice;

    public OrderItem(int orderItemId, int itemId, String itemName, int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() { return quantity * unitPrice; }   // behaviour on the object

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
