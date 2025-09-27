package com.group9.dao;

import com.group9.model.Order;
import com.group9.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderDao {

    public Order findOrderByUserId(int userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id")
                    );
                } else {
                    return null; // No order found
                }
            } catch (Exception e) {
                throw new RuntimeException("Error finding active cart: " + e.getMessage(), e);
            }
    }
}
