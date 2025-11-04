package com.group9.service;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import com.group9.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class OrderService {
    private final OrderDao orderDao;
    private ResourceBundle rb;

    public OrderService(OrderDao orderDao) {
    this.orderDao = orderDao;
  }

    public List<Order> getOrdersByUserId(int userId) {
        rb = SessionManager.getResourceBundle();
        if (userId <= 0) {
            String message = rb.getString("invalidUserId");
            throw new IllegalArgumentException(message);
        }
        return orderDao.findOrdersByUserId(userId);
    }

  public int createOrder(Order order) {
      rb = SessionManager.getResourceBundle();
      try {
          return orderDao.insertOrder(order); // Return the generated order ID
      } catch (SQLException e) {
          String message = rb.getString("orderError");
          System.err.println(message + e.getMessage());
          return -1; // Indicate failure
      }
  }
}
