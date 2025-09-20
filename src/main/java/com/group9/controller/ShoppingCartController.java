package com.group9.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ShoppingCartController {

    @FXML private VBox cartItems;
    @FXML private Button emptyButton;
    @FXML private Button checkoutButton;
    @FXML private VBox cartVbox; // vbox where items go in cart

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
    private void openCheckoutWindow() {

        System.out.println("Proceed to checkout...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkout_view.fxml"));
            Parent checkoutRoot = loader.load();

            // Get current window (the cart stage)
            Stage stage = (Stage) checkoutButton.getScene().getWindow();

            stage.setScene(new Scene(checkoutRoot));
            stage.setTitle("Checkout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


