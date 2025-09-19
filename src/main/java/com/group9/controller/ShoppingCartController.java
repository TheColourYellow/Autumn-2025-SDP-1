package com.group9.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ShoppingCartController {

    @FXML private VBox cartItems;
    @FXML private Button emptyButton;
    @FXML private Button checkoutButton;

    public void setCart(Object cart) {
        // TODO: populate cartItems VBox with cart contents
        cartItems.getChildren().clear();
        cartItems.getChildren().add(new Label("Cart content goes here..."));
    }

    @FXML
    private void emptyCart() {
        cartItems.getChildren().clear();
        System.out.println("Empty cart...");
    }

    @FXML
    private void openCheckout() {
        System.out.println("Proceed to checkout...");
    }
}


