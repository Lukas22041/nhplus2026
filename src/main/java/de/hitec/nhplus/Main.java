package de.hitec.nhplus;

import de.hitec.nhplus.controller.LoginController;
import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.utils.SetUpDB;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    private Stage primaryStage;
    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        SetUpDB.initializeDatabase();
        mainWindow();
    }

    public void mainWindow() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane mainPane = mainLoader.load();
            Scene mainScene = new Scene(mainPane);

            FXMLLoader loginLoader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            Parent loginPane = loginLoader.load();
            Scene loginScene = new Scene(loginPane);

            this.loginController = loginLoader.getController();
            this.loginController.setStage(this.primaryStage);
            this.loginController.setMainScene(mainScene);

            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.setScene(loginScene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> {
                if (this.loginController != null && !this.loginController.isAuthenticated()) {
                    ButtonType cancelButton = new ButtonType("Zurück zum Login", ButtonBar.ButtonData.CANCEL_CLOSE);
                    ButtonType closeButton = new ButtonType("Anwendung beenden", ButtonBar.ButtonData.OK_DONE);
                    Alert alert = new Alert(
                            Alert.AlertType.WARNING,
                            "Ohne erfolgreiche Anmeldung kann NHPlus nicht genutzt werden.\nSoll die Anwendung beendet werden?",
                            cancelButton,
                            closeButton
                    );
                    alert.setHeaderText("Anmeldung abgebrochen");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() != closeButton) {
                        event.consume();
                        return;
                    }
                }
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}