package com.group9;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GuiTestMain extends Application {

    // For testing the JavaFX user interface

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML layout for the bookstore_view.fmxl
        Parent root = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/bookstore_view.fxml")
        ));

        // Set the window title
        primaryStage.setTitle("Bookstore Management System");

        // Set the scene and show the window
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

