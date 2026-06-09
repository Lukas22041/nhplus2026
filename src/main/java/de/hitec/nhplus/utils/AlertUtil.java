package de.hitec.nhplus.utils;

import javafx.scene.control.Alert;

/**
 * Zentrale Hilfsmethoden für Meldungsdialoge.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showError(String title, String header, String content) {
        show(Alert.AlertType.ERROR, title, header, content);
    }

    public static void showWarning(String title, String header, String content) {
        show(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showInfo(String title, String header, String content) {
        show(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showPermissionDenied(String actionDescription) {
        showWarning(
                "Keine Berechtigung",
                "Aktion nicht erlaubt",
                "Sie dürfen diese Aktion nicht ausführen: " + actionDescription + "."
        );
    }

    private static void show(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

