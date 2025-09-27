package com.group9.dao;

import com.group9.model.Book;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private BookDao bookDao = new BookDao();

    public List<Order> findOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    orders.add(resultSetToOrder(rs));
                }

                return orders;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders by user ID: " + e.getMessage(), e);
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = bookDao.getBookById(rs.getInt("book_id"));

                if (book != null) {
                    OrderItem item = new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            book,
                            rs.getInt("quantity")
                    );
                    items.add(item);
                } else {
                    throw new RuntimeException("Book with ID " + rs.getInt("book_id") + " not found for order item ID " + rs.getInt("id"));
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order items by order ID: " + e.getMessage(), e);
        }
    }

    private Order resultSetToOrder(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getInt("user_id"),
                getOrderItemsByOrderId(rs.getInt("id"))
        );
    }
}
