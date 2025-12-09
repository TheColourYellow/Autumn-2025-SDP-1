package com.group9.service;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
  private OrderDao orderDao;
  private OrderService orderService;

  @BeforeEach
  public void setUp() {
    orderDao = mock(OrderDao.class);
    orderService = new OrderService(orderDao);
  }

  @Test
  public void testGetOrdersByUserId() {
    // Valid user ID
    int userId = 1;
    orderService.getOrdersByUserId(userId);
    verify(orderDao).findOrdersByUserId(userId);

    // Mock Dao response
    when(orderDao.findOrdersByUserId(userId)).thenReturn(new ArrayList<>());

    // Ensure the returned list is as expected
    assertEquals(new ArrayList<Order>(), orderService.getOrdersByUserId(userId));

    // Invalid user ID
    int invalidUserId = -1;
    assertThrows(IllegalArgumentException.class, () -> orderService.getOrdersByUserId(invalidUserId));
    verify(orderDao, never()).findOrdersByUserId(invalidUserId);
  }

  @Test
  public void testCreateOrder() throws SQLException {
    Order testOrder = new Order();

    // Successful insertion should return generated ID
    when(orderDao.insertOrder(testOrder)).thenReturn(1);
    assertEquals(1, orderService.createOrder(testOrder));
    verify(orderDao).insertOrder(testOrder);

    // Simulate DB error on insertion should return -1
    when(orderDao.insertOrder(testOrder)).thenThrow(new SQLException("DB error"));
    assertEquals(-1, orderService.createOrder(testOrder));
  }
}
