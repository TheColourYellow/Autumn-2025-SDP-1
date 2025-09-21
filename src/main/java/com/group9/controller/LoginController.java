package com.group9.controller;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.group9.util.PopupUtils.showError;

public class LoginController {

    @FXML
    private Label homeLabel;

    @FXML
    private Label registerLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

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

            stage.setTitle("Bookstore Management System");
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
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open register window.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = userService.loginUser(username, password);

            // Login successful, open Profile View
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();

        } catch (IllegalArgumentException e) {
            showError("Login Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Could not log in user");
        }
    }
}

