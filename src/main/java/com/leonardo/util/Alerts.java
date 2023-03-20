package com.leonardo.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Alerts {

    public static void showWarningPopup(String title, String header, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
    public static Optional<ButtonType> showConfirmation(String title, String content) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(content);
      return alert.showAndWait();
    }
}
