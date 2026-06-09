package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.AppSession;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Controller für das Login-Fenster.
 */
public class LoginController {

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button buttonLogin;

    @FXML
    private Label labelError;

    private Main mainApp;
    private Stage stage;

    public void initialize(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.labelError.setVisible(false);
        this.textFieldUsername.requestFocus();
    }

    @FXML
    private void handleLogin() {
        String username = this.textFieldUsername.getText() == null ? "" : this.textFieldUsername.getText().trim();
        String password = this.passwordField.getText() == null ? "" : this.passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Bitte geben Sie Benutzername und Passwort ein.");
            return;
        }

        UserDao userDao = DaoFactory.getDaoFactory().createUserDao();
        try {
            User user = userDao.readActiveByUsername(username);
            if (user == null || !PasswordUtil.verify(password, user.getSalt(), user.getPasswordHash())) {
                this.passwordField.clear();
                showError("Benutzername oder Passwort ist ungültig.");
                return;
            }

            AppSession.login(user);
            this.labelError.setVisible(false);
            this.mainApp.mainWindow();
        } catch (SQLException exception) {
            exception.printStackTrace();
            showError("Die Anmeldung konnte aufgrund eines Datenbankfehlers nicht durchgeführt werden.");
        }
    }

    private void showError(String message) {
        this.labelError.setText(message);
        this.labelError.setVisible(true);
        if (this.stage != null) {
            this.stage.sizeToScene();
        }
    }
}

