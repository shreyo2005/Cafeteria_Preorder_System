-- Cafeteria Preorder System - SQLite schema
-- The Java app runs this automatically on first launch; you don't run it manually.

CREATE TABLE IF NOT EXISTS users (
    user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    role      TEXT NOT NULL,
    full_name TEXT NOT NULL,
    phone TEXT
);

CREATE TABLE IF NOT EXISTS menu_items (
    item_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name      TEXT NOT NULL,
    category  TEXT NOT NULL,
    price     REAL NOT NULL,
    available INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS orders (
    order_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    order_time  TEXT NOT NULL,
    pickup_slot TEXT NOT NULL,
    status      TEXT NOT NULL,
    total       REAL NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id   INTEGER NOT NULL,
    item_id    INTEGER NOT NULL,
    quantity   INTEGER NOT NULL,
    unit_price REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id)  REFERENCES menu_items(item_id)
);
