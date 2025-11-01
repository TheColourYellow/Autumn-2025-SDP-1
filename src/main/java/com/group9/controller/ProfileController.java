package com.group9.controller;

import com.group9.dao.OrderDao;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.model.User;
import com.group9.service.OrderService;
import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class ProfileController {
    private ResourceBundle rb;

    @FXML private Label homeLabel;
    @FXML private ImageView shoppingCart;
    @FXML private ListView <String> orderListView;

    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;

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
        rb = SessionManager.getResourceBundle();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL); // makes the cart window as modal
            stage.setTitle(rb.getString("yourCartLabel"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/look.css").toExternalForm());
            stage.setScene(scene);
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

        // Get user account details
        User currentUser = SessionManager.getCurrentUser();

        // Show account details
        nameLabel.setText("Name: " + currentUser.getUsername());
        emailLabel.setText("Email: " + currentUser.getEmail());

        System.out.println("POPULATING HISTORY ");
        OrderService orderService = new OrderService(new OrderDao());
        List<Order> orders = orderService.getOrdersByUserId(SessionManager.getCurrentUser().getId());

        for (Order o : orders) {
            System.out.println("Order ID: " + o.getId());
            for (OrderItem item : o.getOrderItems()) {
                System.out.println(item.getBook().getTitle());
                orderListView.getItems().add(item.getBook().getTitle());
            }
        }
    }
}
