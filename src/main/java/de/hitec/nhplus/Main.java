package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.utils.AlertUtil;
import de.hitec.nhplus.utils.AppSession;
import de.hitec.nhplus.utils.SetUpDB;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        SetUpDB.initializeDb();
        showLoginWindow();
    }

    public void showLoginWindow() {
        try {
            AppSession.logout();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox pane = loader.load();

            de.hitec.nhplus.controller.LoginController controller = loader.getController();
            controller.initialize(this, this.primaryStage);

            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus - Login");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> {
                event.consume();
                AlertUtil.showError(
                        "Anmeldung erforderlich",
                        "Anwendung wird beendet",
                        "Ohne erfolgreichen Login kann NHPlus nicht genutzt werden."
                );
                shutdown();
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();

            de.hitec.nhplus.controller.MainWindowController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> shutdown());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void shutdown() {
        ConnectionBuilder.closeConnection();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}