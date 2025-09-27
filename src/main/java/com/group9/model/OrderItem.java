package com.group9.model;

public class OrderItem {
    private int id = -1;
    private int orderId;
    private Book book;
    private int quantity;
    //private int price; tarvitaanko?

    public OrderItem() {}

    public OrderItem(int orderId, Book book, int quantity) {
        this.orderId = orderId;
        this.book = book;
        this.quantity = quantity;
    }

    public OrderItem(int id, int orderId, Book book, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.book = book;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Book getBook() { return book; }
    public void setBookId(Book book) { this.book = book; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
