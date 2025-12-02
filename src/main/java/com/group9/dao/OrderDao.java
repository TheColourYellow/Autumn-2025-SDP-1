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
    private final BookDao bookDao = new BookDao();

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
            throw new IllegalArgumentException("Error retrieving orders by user ID: " + userId, e);
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            // For each order item, fetch the associated book details
            while (rs.next()) {
                Book book = bookDao.getBookById(rs.getInt("book_id"));

                // Ensure book exists before creating OrderItem
                if (book != null) {
                    OrderItem item = new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            book,
                            rs.getInt("quantity")
                    );
                    items.add(item);
                } else {
                    throw new IllegalArgumentException("Book with ID " + rs.getInt("book_id") + " not found for order item ID " + rs.getInt("id"));
                }
            }
            return items;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error retrieving order items by order ID: " + orderId, e);
        }
    }

    public int insertOrder(Order order) throws SQLException {
        String insertOrderSql = "INSERT INTO orders (user_id) VALUES (?)";
        String insertItemSql = "INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, java.sql.Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {

                    // Insert order
                    orderStmt.setInt(1, order.getUserId());
                    orderStmt.executeUpdate();

                    // Get generated order id
                    int orderId;
                    try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to retrieve order ID.");
                        }
                    }

                    // Insert order items
                    for (OrderItem item : order.getOrderItems()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, item.getBook().getId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getBook().getPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();

                    conn.commit();
                    return orderId;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
                throw new SQLException("Error inserting order: " + order, e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    // ignore
                }
            }
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
