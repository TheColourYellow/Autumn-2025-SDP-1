package com.group9.model;

public class OrderItem {
    private int id = -1;
    private int orderId;
    private int bookId;
    private int quantity;
    //private int price; tarvitaanko?

    public OrderItem() {}

    public OrderItem(int orderId, int bookId, int quantity) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public OrderItem(int id, int orderId, int bookId, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
