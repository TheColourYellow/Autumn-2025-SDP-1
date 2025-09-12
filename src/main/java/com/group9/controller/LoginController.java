package com.group9.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import static com.group9.util.PopupUtils.showError;

public class LoginController {

    @FXML
    private Label homeLabel;

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
}
