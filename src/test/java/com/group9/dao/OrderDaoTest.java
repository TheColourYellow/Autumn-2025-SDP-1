package com.group9.dao;

import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderDaoTest {
    private static OrderDao orderDao;
    private static User user;
    private static Order order;
    private static List <Order> orders;
    private static OrderItem orderItem;
    private static List <OrderItem> orderItems;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        orderDao = mock(OrderDao.class);
        user = mock(User.class);
        order = mock(Order.class);
        orders = new ArrayList<>();
        orderItem = mock(OrderItem.class);
        orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

    }

    @Test
    void findOrdersByUserId() {
        // Act
        List<Order> foundOrders = orderDao.findOrdersByUserId(user.getId());

        // Assert
        assertNotNull(foundOrders);
        assertFalse(foundOrders.isEmpty());
    }


    @Test
    void getOrderItemsByOrderId() throws SQLException {
        // Arrange
        orders.add(order);
        when(orderDao.getOrderItemsByOrderId(order.getId())).thenReturn(orderItems);

        // Act
        List<OrderItem> items = orderDao.getOrderItemsByOrderId(order.getId()); // renamed variable

        // Assert
        verify(orderDao).getOrderItemsByOrderId(order.getId());
        assertFalse(items.isEmpty()); // cleaner than !items.isEmpty()
    }

}