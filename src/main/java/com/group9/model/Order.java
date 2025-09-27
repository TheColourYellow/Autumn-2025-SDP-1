package com.group9.model;

public class Order {
    private int id = -1;
    private int userId;

    public Order() {}

    public Order(int userId) {
        this.userId = userId;
    }

    public Order(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

}
