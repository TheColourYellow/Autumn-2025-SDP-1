package com.group9.controller;

import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class ProfileController {

    @FXML private Label homeLabel;
    @FXML private ImageView shoppingCart;

    @FXML
    private void openHomeWindow() {
        try {
            // Load the FXML file for the home window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookstore_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) homeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bookstore Management System");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open home window");
        }
    }

    @FXML
    private void openShoppingCart() {
        System.out.println("Shopping cart clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL); // makes the cart window as modal
            stage.setTitle("Your Cart");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        // Confirm logout action
        if (showConfirmation("Logout", "Are you sure you want to logout?")) {
            // Handle logout
            SessionManager.logout();
            openHomeWindow();
        }
    }

    @FXML
    public void initialize() {
        // Go back to home window if not logged in
        if (!SessionManager.isLoggedIn()) {
            openHomeWindow();
        }

        // Use SessionManager to get user details
        // SessionManager.getCurrentUser() returns a User object
    }
}
