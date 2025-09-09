package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class BookstoreController {

    @FXML
    private VBox genreSidebar;

    @FXML
    private Button sidebarButton;

    // Method for controlling visibility of genres sidebar
    @FXML
    private void toggleSidebar() {
        boolean isVisible = genreSidebar.isVisible();
        genreSidebar.setVisible(!isVisible);

        // Update button text, when sidebar is visble and when its not
        if (isVisible) {
            sidebarButton.setText("Show Genres");
        } else {
            sidebarButton.setText("Hide Genres");
        }
    }

}
