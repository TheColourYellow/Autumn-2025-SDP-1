package com.group9.controller;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.service.OrderService;
import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutController {

    @FXML private Button orderButton;
    @FXML private Button returnButton; // return to shopping cart
    @FXML private TextField cardNumberField; // text field for card number
    @FXML private Label totalLabel; // label for total amount
    @FXML private VBox checkoutBox; // vbox for checkout items
    @FXML private ImageView visaImage;
    @FXML private ImageView mastercardImage;

    // selected card
    private ImageView selectedCard = null;
    private final String NORMAL_STYLE = "-fx-effect: dropshadow(gaussian, gray, 5, 0.3, 0, 0); -fx-cursor: hand;";
    private final String SELECTED_STYLE = "-fx-effect: dropshadow(gaussian, blue, 15, 0.7, 0, 0); -fx-border-color: blue; -fx-border-width: 3; -fx-cursor: hand;";


    @FXML
    public void initialize() {
        // initial style
        visaImage.setStyle(NORMAL_STYLE);
        mastercardImage.setStyle(NORMAL_STYLE);

        // click handler
        visaImage.setOnMouseClicked(e -> selectCard(visaImage));
        mastercardImage.setOnMouseClicked(e -> selectCard(mastercardImage));

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

    private void selectCard(ImageView card) {
        // reset previous selection
        if (selectedCard != null) {
            selectedCard.setStyle(NORMAL_STYLE);
        }
        // new selection
        selectedCard = card;
        selectedCard.setStyle(SELECTED_STYLE);
    }

    @FXML
    private void placeOrder() { // TODO: new window for order confirmation and moving data to history

        if (selectedCard == null) {
            System.out.println("No card selected!");
            return;
        }

        // number from text field
        String cardNumber = cardNumberField.getText().trim();

        if (cardNumber.isEmpty()) {
            System.out.println("Card number cannot be empty!");
            return;
        }

        // remove spaces
        cardNumber = cardNumber.replaceAll("\\s+", "");

        // validate length and digits
        if (!cardNumber.matches("\\d{16}")) {
            System.out.println("Card number must be 16 digits!");
            return;
        }

        // if all validations pass
        String selectedCardType = selectedCard == visaImage ? "Visa" : "MasterCard";
        System.out.println("Placing order with " + selectedCardType + " card number: " + cardNumber);

        // change window to payment accept
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/accept_view.fxml"));
//            Parent checkoutRoot = loader.load();
//            Stage stage = (Stage) orderButton.getScene().getWindow();
//
//            stage.setScene(new Scene(checkoutRoot));
//            stage.setTitle("Shopping Cart");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
   }

    @FXML
    private void returnToCart() {

        System.out.println("Return to cart...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent checkoutRoot = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();

            stage.setScene(new Scene(checkoutRoot));
            stage.setTitle("Shopping Cart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
