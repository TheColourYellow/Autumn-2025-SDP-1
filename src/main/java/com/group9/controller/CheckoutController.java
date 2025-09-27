package com.group9.controller;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.service.OrderService;
import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutController {

    @FXML private Button orderButton;
    @FXML private TextField cardNumberField; // text field for card number
    @FXML private Label totalLabel; // label for total amount
    @FXML private VBox checkoutBox; // vbox for checkout items
    @FXML private ImageView visaImage;
    @FXML private ImageView masterCardImage;

    @FXML
    public void initialize() {
        int userId = SessionManager.getCurrentUser().getId();
        OrderService orderService = new OrderService(new OrderDao());
        List<Order> orders = orderService.getOrdersByUserId(userId);

        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            double total = 0.0;
            for (OrderItem item : order.getOrderItems()) {
                List<String> authorNames = item.getBook().getAuthors()
                        .stream()
                        .map(author -> author.getName())
                        .collect(Collectors.toList());
                String authors = String.join(", ", authorNames);
                String info = item.getBook().getTitle() + " by " + authors + " " + item.getBook().getPrice() + "€ quantity: " + item.getQuantity();
                Label label = new Label(info);
                checkoutBox.getChildren().add(label);

                total += item.getBook().getPrice() * item.getQuantity();
            }
            totalLabel.setText(String.format("%.2f", total));
        }
    }

    @FXML
    private void placeOrder() {
        System.out.println("placeOrder");
    }
}
