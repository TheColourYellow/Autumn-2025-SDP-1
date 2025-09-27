package com.group9.service;

import com.group9.dao.OrderDao;
import com.group9.model.Order;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
  private final OrderDao orderDao;

  public OrderService(OrderDao orderDao) {
    this.orderDao = orderDao;
  }

  public List<Order> getOrdersByUserId(int userId) {
    return orderDao.findOrdersByUserId(userId);
  }

  public int createOrder(Order order) {
    try {
      return orderDao.insertOrder(order); // Return the generated order ID
    } catch (SQLException e) {
      System.err.println("Error creating order: " + e.getMessage());
      return -1; // Indicate failure
    }
  }
}
