package com.group9.model;

import java.util.List;

public class Cart {
    private List<CartItem> items;

    public List<CartItem> getItems() { return items; }
    public void addItem(Book book) { }
    public void removeItem(Book book) {}
    public void clear() { items.clear(); }
}




