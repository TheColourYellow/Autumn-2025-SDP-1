package com.group9.service;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import com.group9.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing orders.
 */
public class OrderService {
  private final OrderDao orderDao;
  private ResourceBundle rb;
  private static final Logger log = Logger.getLogger(OrderService.class.getName());

  /**
   * Constructor for OrderService.
   *
   * @param orderDao the {@link OrderDao} instance for database operations
   */
  public OrderService(OrderDao orderDao) {
    this.orderDao = orderDao;
  }

  /**
   * Retrieves all orders for a specific user by their user ID.
   *
   * @param userId the ID of the user
   * @return a list of {@link Order} objects
   * @throws IllegalArgumentException if the user ID is invalid
   */
  public List<Order> getOrdersByUserId(int userId) {
    rb = SessionManager.getResourceBundle();
    if (userId <= 0) {
      String message = rb.getString("invalidUserId");
      throw new IllegalArgumentException(message);
    }
    return orderDao.findOrdersByUserId(userId);
  }

  /**
   * Creates a new order in the database.
   *
   * @param order the {@link Order} object to be created
   * @return the ID of the newly created order, or -1 if creation failed
   */
  public int createOrder(Order order) {
    rb = SessionManager.getResourceBundle();
    try {
      return orderDao.insertOrder(order); // Return the generated order ID
    } catch (SQLException e) {
      String message = rb.getString("orderError");
      log.log(Level.SEVERE, message, e.getMessage());
      return -1; // Indicate failure
    }
  }
}
