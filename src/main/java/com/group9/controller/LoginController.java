package com.group9.controller;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.service.UserService;
import com.group9.util.LayoutOrienter;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.group9.util.PopupUtils.showError;

public class LoginController {

    private ResourceBundle rb;
    private LayoutOrienter orienter = new LayoutOrienter();

    @FXML private AnchorPane loginAnchor;
    @FXML private Label homeLabel;
    @FXML private Label registerLabel;
    @FXML private ImageView shoppingCart;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginText;

    @FXML
    private Label loginLabel;

    @FXML
    private Label bookstoreLabel;

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(loginAnchor);
        updateUI();
    }

    @FXML
    private void handleMouseEnterLabel() {
        registerLabel.setUnderline(true);   // underline sign up text when mouse hovers over it
        registerLabel.setStyle("-fx-text-fill: #FA33A0;"); // change color
    }

    @FXML
    private void handleMouseExitLabel() {
        registerLabel.setUnderline(false);  // remove underline
        registerLabel.setStyle("-fx-text-fill: black;"); // return original color
    }

    private final UserService userService = new UserService(new UserDao());

    @FXML
    private void openHomeWindow() {
        try {
            // Load the FXML file for the home window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookstore_view.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) homeLabel.getScene().getWindow();

            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open home window");
        }
    }

    @FXML
    private void openRegisterWindow() {
        try {
            // Load the FXML file for the register window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registerLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(rb.getString("registerPageText"));
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open register window.");
        }
    }

    @FXML
    private void openShoppingCart() {
        rb = SessionManager.getResourceBundle();
        System.out.println("Shopping cart clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL); // makes the cart window as modal
            stage.setTitle(rb.getString("yourCartLabel"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Attempt to log in user
            User user = userService.loginUser(username, password);
            // Store logged-in user in session
            SessionManager.login(user);

            // Login successful, open Profile View
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/look.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Profile");
            stage.show();

        } catch (IllegalArgumentException e) {
            showError("Login Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Could not log in user");
        }
    }

    private void updateUI() {
        homeLabel.setText(rb.getString("homeLabel"));
        registerLabel.setText(rb.getString("registerLabel"));
        loginButton.setText(rb.getString("loginButton"));
        usernameField.setPromptText(rb.getString("usernamePrompt"));
        passwordField.setPromptText(rb.getString("passwordPrompt"));
        loginText.setText(rb.getString("loginText"));
        bookstoreLabel.setText(rb.getString("bookStoreLabel"));
        loginLabel.setText(SessionManager.isLoggedIn() ? rb.getString("profileLabel") : rb.getString("loginLabel"));
    }
}

