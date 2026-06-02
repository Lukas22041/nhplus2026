package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Controls the login view and grants access to the main application scene
 * only after successful authentication.
 */
public class LoginController {

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelError;

    private Stage stage;
    private Scene mainScene;
    private boolean authenticated;

    /**
     * Injects the primary stage so the controller can switch scenes after login.
     *
     * @param stage Primary application stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Injects the main application scene that should be shown after login.
     *
     * @param mainScene Main scene of the application.
     */
    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    /**
     * Returns whether a successful login already happened.
     *
     * @return {@code true} if the user is authenticated, otherwise {@code false}.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Handles the login button. Empty input and invalid credentials are rejected
     * with a visible error message. On success the main scene is shown.
     */
    @FXML
    public void handleLogin() {
        String username = textFieldUsername.getText() == null ? "" : textFieldUsername.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Bitte Benutzername und Passwort eingeben.");
            return;
        }

        UserDao userDao = DaoFactory.getDaoFactory().createUserDao();
        try {
            User user = userDao.readByUsername(username);
            if (user == null) {
                showError("Unbekannter Benutzername.");
                passwordField.clear();
                return;
            }
            if (!PasswordUtil.verify(password, user.getSalt(), user.getPasswordHash())) {
                passwordField.clear();
                showError("Benutzername oder Passwort ist ungültig.");
                return;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            showError("Anmeldung konnte wegen eines Datenbankfehlers nicht geprüft werden.");
            return;
        }

        authenticated = true;
        labelError.setVisible(false);
        passwordField.clear();
        if (stage != null && mainScene != null) {
            stage.setScene(mainScene);
            stage.centerOnScreen();
        }
    }

    private void showError(String message) {
        labelError.setText(message);
        labelError.setVisible(true);
    }
}


