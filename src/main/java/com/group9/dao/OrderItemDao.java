package com.group9.dao;

import com.group9.model.OrderItem;
import com.group9.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class OrderItemDao {

    public List<OrderItem> findItemsByOrderId(int orderId) {
        String sql = "SELECT oi.*, b.title, b.price FROM order_items oi JOIN books b ON oi.book_id = b.id WHERE order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            List<OrderItem> items = new java.util.ArrayList<>();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("book_id"),
                        rs.getInt("quantity")
                );
                items.add(item);
            }
            return items;
        } catch (Exception e) {
            throw new RuntimeException("Error finding order items: " + e.getMessage(), e);
        }
    }
}
