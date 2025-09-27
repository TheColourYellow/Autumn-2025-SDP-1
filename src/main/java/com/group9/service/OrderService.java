package com.group9.service;

import com.group9.dao.OrderDao;

public class OrderService {
  private final OrderDao orderDao;

  public OrderService(OrderDao orderDao) {
    this.orderDao = orderDao;
  }


}
