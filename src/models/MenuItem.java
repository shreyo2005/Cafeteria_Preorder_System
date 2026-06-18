package models;

/** ENCAPSULATION: a single item on the cafeteria menu. */
public class MenuItem {
    private int itemId;
    private String name;
    private String category;
    private double price;
    private boolean available;

    public MenuItem(int itemId, String name, String category, double price, boolean available) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() { return name + " - Rs." + price; }
}
