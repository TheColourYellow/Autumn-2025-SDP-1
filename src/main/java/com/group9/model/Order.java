package com.group9.model;

import java.util.List;

public class Order {
    private int id = -1;
    private int userId;
    private List<OrderItem> orderItems;

    public Order() {}

    public Order(int userId) {
        this.userId = userId;
    }

    public Order(int id, int userId, List<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
