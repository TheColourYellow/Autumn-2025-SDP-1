
package com.group9.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BookstoreView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file for UI
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/bookstore_view.fxml"));
        Parent root = fxmlLoader.load();

        // Set the window title, create the scene, and show the stage
        stage.setTitle("Bookstore Management System");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
