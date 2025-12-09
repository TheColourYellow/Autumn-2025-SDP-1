package com.group9.model;

/**
 * Represents an item in the shopping cart.
 */
public class CartItem {
    private Book book;
    private int quantity;

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
