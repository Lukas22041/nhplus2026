package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.model.Permission;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.AlertUtil;
import de.hitec.nhplus.utils.AppSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label labelUsername;

    @FXML
    private Label labelRole;

    @FXML
    private Button buttonCaregivers;

    @FXML
    private Button buttonPatients;

    @FXML
    private Button buttonTreatments;

    @FXML
    private Button buttonUsers;

    private Main mainApp;

    public void initialize() {
        User currentUser = AppSession.getCurrentUser();
        if (currentUser != null) {
            this.labelUsername.setText(currentUser.getUsername());
            this.labelRole.setText(currentUser.getRole().getRoleName());
        } else {
            this.labelUsername.setText("Nicht angemeldet");
            this.labelRole.setText("");
        }

        this.buttonCaregivers.setDisable(false);
        this.buttonPatients.setDisable(false);
        this.buttonTreatments.setDisable(false);

        boolean canManageUsers = AppSession.hasPermission(Permission.MANAGE_USERS);
        this.buttonUsers.setVisible(canManageUsers);
        this.buttonUsers.setManaged(canManageUsers);

        showWelcomeMessage();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleShowAllCaregiver(ActionEvent event) {
        loadCenter("/de/hitec/nhplus/AllCaregiverView.fxml");
    }

    @FXML
    private void handleShowAllPatient(ActionEvent event) {
        loadCenter("/de/hitec/nhplus/AllPatientView.fxml");
    }

    @FXML
    private void handleShowAllTreatments(ActionEvent event) {
        loadCenter("/de/hitec/nhplus/AllTreatmentView.fxml");
    }

    @FXML
    private void handleShowUserManagement(ActionEvent event) {
        if (!ensurePermission(Permission.MANAGE_USERS)) {
            return;
        }
        loadCenter("/de/hitec/nhplus/AllUserView.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        if (this.mainApp != null) {
            this.mainApp.showLoginWindow();
        }
    }

    private void loadCenter(String resourcePath) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(resourcePath));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private boolean ensurePermission(Permission permission) {
        if (AppSession.hasPermission(permission)) {
            return true;
        }
        AlertUtil.showPermissionDenied(permission.getDescription());
        return false;
    }

    private void showWelcomeMessage() {
        Label label = new Label("Bitte wählen Sie links einen Bereich aus. Nicht erlaubte Aktionen sind in den Ansichten deaktiviert.");
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 18px; -fx-padding: 30px;");
        this.mainBorderPane.setCenter(new StackPane(label));
    }
}
