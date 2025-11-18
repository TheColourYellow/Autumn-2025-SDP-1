package com.group9;

import com.group9.dao.BookDao;
import com.group9.dao.OrderDao;
import com.group9.model.Book;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.service.BookService;
import com.group9.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderTesti {
    private static final Logger log = Logger.getLogger(OrderTesti.class.getName());
  public static void main(String[] args) {
    // Vaatii tietokannan jossa on 1 kirja ja 1 käyttäjä (id=1)
    // Ajamalla MockData luokan saa luotua 1 kirjan, admin käyttäjä tulee jo automaattisesti

    // Service luokat
    OrderService orderService = new OrderService(new OrderDao());
    BookService bookService = new BookService(new BookDao());

    // Haetaan kirja ID:llä 1
    Book book = bookService.getBookById(1);

    // Luodaan orderItem
    OrderItem orderItem1 = new OrderItem(
            -1, // id, ei väliä, tietokanta asettaa
            -1, // orderId, ei väliä, tietokanta asettaa
            book, // kirja
            2 // määrä
    );

    // Lisätään orderItem/it listaan
    List<OrderItem> orderItems = new ArrayList<>();
    orderItems.add(orderItem1);

    // Luodaan order
    Order order = new Order(
            -1, // ei väliä, tietokanta asettaa
            1, // userId
            orderItems // orderItems lista
    );

    // Tallennetaan order tietokantaan
    int orderId = orderService.createOrder(order);
    log.log(Level.INFO, "Created order with ID: {0}", new Object[]{orderId});

    // Haetaan käyttäjän 1 orderit
    List<Order> orders = orderService.getOrdersByUserId(1);

    // Tulostetaan orderit
    for (Order o : orders) {
      log.log(Level.INFO, "Order ID: {0}", new Object[]{o.getId()});
      for (OrderItem item : o.getOrderItems()) {
        log.log(Level.INFO, "  Book: {0}, Quantity: {1}, Price: {2}$", new Object[]{item.getBook().getTitle(), item.getQuantity(), (item.getBook().getPrice() * item.getQuantity())});
      }
    }
  }
}
