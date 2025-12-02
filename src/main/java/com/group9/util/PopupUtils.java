package com.group9.util;

import javafx.scene.control.Alert;

public class PopupUtils {

    // Prevent instantiation
    private PopupUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

  public static void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static boolean showConfirmation(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
  }
}
