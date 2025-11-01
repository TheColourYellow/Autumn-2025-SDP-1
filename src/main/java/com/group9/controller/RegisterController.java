package com.group9.controller;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.service.UserService;
import com.group9.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.group9.util.PopupUtils.showError;

public class RegisterController {

    private ResourceBundle rb;

    @FXML private Label homeLabel;

    @FXML private ImageView shoppingCart;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Label registerLabel;

    @FXML
    private Label bookstoreLabel;

    @FXML
    private Label loginLabel;

    // UserService manages all registration related tasks -> give it new UserDao to get access to users table
    private final UserService userService = new UserService(new UserDao());

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        updateUI();
    }

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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/look.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method called when user clicks the Register button, handles reading input, validating, and creating new user
    @FXML
    private void registerUser() {
        try {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            // Attempt to register user
            User newUser = userService.registerUser(username, password, email);
            // Log in the newly registered user
            SessionManager.login(newUser);

            // Registration successful, open Profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();

        } catch (IllegalArgumentException e) {
            // Handle errors caused by invalid input or already existing user
            showError("Registration Error", e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors that occur during registration process (for example database connection issues)
            showError("Error", "Could not register user");
        }
    }

    @FXML
    private void handleRegister() {
        registerUser(); // Call the main registration logic
    }

    private void updateUI() {
        homeLabel.setText(rb.getString("homeLabel"));
        usernameField.setPromptText(rb.getString("usernamePrompt"));
        emailField.setPromptText(rb.getString("emailPrompt"));
        passwordField.setPromptText(rb.getString("passwordPrompt"));
        registerButton.setText(rb.getString("registerButton"));
        registerLabel.setText(rb.getString("registerLabel"));
        bookstoreLabel.setText(rb.getString("bookStoreLabel"));
        loginLabel.setText(SessionManager.isLoggedIn() ? rb.getString("profileLabel") : rb.getString("loginLabel"));

    }




}
