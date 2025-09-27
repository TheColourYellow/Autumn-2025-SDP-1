package com.group9.controller;

import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CheckoutController {

    @FXML private Button orderButton;
    @FXML private TextField cardNumberField; // text field for card number
    @FXML private Label totalLabel; // label for total amount
    @FXML private VBox checkoutBox; // vbox for checkout items
    @FXML private ImageView visaImage;
    @FXML private ImageView masterCardImage;

    @FXML
    private void placeOrder() {
        System.out.println("Order placed!");
    }
}
