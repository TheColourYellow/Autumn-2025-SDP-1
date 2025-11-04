package com.group9.controller;

import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

public class AcceptController {

    @FXML private Label confirmationLabel;
    @FXML private Label thankyouLabel;

    private ResourceBundle rb;

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        updateUI();
    }

    private void updateUI() {
        confirmationLabel.setText(rb.getString("paymentConfirmed"));
        thankyouLabel.setText(rb.getString("thankYouForPurchase"));
    }
}
