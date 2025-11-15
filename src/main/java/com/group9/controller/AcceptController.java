package com.group9.controller;

import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.ResourceBundle;

public class AcceptController {

    private LayoutOrienter orienter = new LayoutOrienter();

    @FXML private AnchorPane acceptAnchor;
    @FXML private Label confirmationLabel;
    @FXML private Label thankyouLabel;

    private ResourceBundle rb;

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(acceptAnchor);
        updateUI();
    }

    private void updateUI() {
        confirmationLabel.setText(rb.getString("paymentConfirmed"));
        thankyouLabel.setText(rb.getString("thankYouForPurchase"));
    }
}
