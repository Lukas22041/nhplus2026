package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.RoleDao;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.Permission;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.AppSession;
import de.hitec.nhplus.utils.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.sql.SQLException;

/**
 * Verwaltung von Benutzern, Rollen und Berechtigungen.
 */
public class AllUserController {

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, Number> columnUserId;

    @FXML
    private TableColumn<User, String> columnUsername;

    @FXML
    private TableColumn<User, String> columnRole;

    @FXML
    private ComboBox<Role> comboBoxUserRole;

    @FXML
    private Button buttonSaveUserRole;

    @FXML
    private Label labelSelectedUsername;

    @FXML
    private ComboBox<Role> comboBoxRoleSelection;

    @FXML
    private CheckBox checkBoxView;

    @FXML
    private CheckBox checkBoxCreate;

    @FXML
    private CheckBox checkBoxEdit;

    @FXML
    private CheckBox checkBoxDelete;

    @FXML
    private CheckBox checkBoxManageUsers;

    @FXML
    private Button buttonSaveRole;

    @FXML
    private Label labelStatus;

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Role> roles = FXCollections.observableArrayList();
    private UserDao userDao;
    private RoleDao roleDao;

    public void initialize() {
        this.userDao = DaoFactory.getDaoFactory().createUserDao();
        this.roleDao = DaoFactory.getDaoFactory().createRoleDao();

        this.columnUserId.setCellValueFactory(new PropertyValueFactory<>("uid"));
        this.columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        this.columnRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().getRoleName()));
        this.tableViewUsers.setItems(this.users);

        this.comboBoxUserRole.setItems(this.roles);
        this.comboBoxRoleSelection.setItems(this.roles);
        this.buttonSaveUserRole.setDisable(true);
        this.buttonSaveRole.setDisable(true);
        this.labelSelectedUsername.setText("Kein Nutzer ausgewählt");
        this.labelStatus.setText("");

        this.tableViewUsers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldUser, newUser) -> {
            if (newUser == null) {
                this.labelSelectedUsername.setText("Kein Nutzer ausgewählt");
                this.comboBoxUserRole.setValue(null);
                this.buttonSaveUserRole.setDisable(true);
                return;
            }
            this.labelSelectedUsername.setText(newUser.getUsername());
            this.comboBoxUserRole.setValue(newUser.getRole());
            this.buttonSaveUserRole.setDisable(this.comboBoxUserRole.getValue() == null);
        });

        this.comboBoxUserRole.valueProperty().addListener((observableValue, oldRole, newRole) ->
                this.buttonSaveUserRole.setDisable(this.tableViewUsers.getSelectionModel().getSelectedItem() == null || newRole == null));

        this.comboBoxRoleSelection.valueProperty().addListener((observableValue, oldRole, newRole) -> {
            if (newRole == null) {
                clearRolePermissions();
                this.buttonSaveRole.setDisable(true);
                return;
            }
            this.checkBoxView.setSelected(newRole.isViewAllowed());
            this.checkBoxCreate.setSelected(newRole.isCreateAllowed());
            this.checkBoxEdit.setSelected(newRole.isEditAllowed());
            this.checkBoxDelete.setSelected(newRole.isDeleteAllowed());
            this.checkBoxManageUsers.setSelected(newRole.isManageUsersAllowed());
            this.buttonSaveRole.setDisable(false);
        });

        if (!AppSession.hasPermission(Permission.MANAGE_USERS)) {
            disableManagementView("Die Benutzerverwaltung ist nur für die Wohnbereichsleitung sichtbar.");
            return;
        }

        loadRoles();
        loadUsers();
        if (!this.roles.isEmpty()) {
            this.comboBoxRoleSelection.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleSaveUserRole() {
        if (!AppSession.hasPermission(Permission.MANAGE_USERS)) {
            AlertUtil.showPermissionDenied(Permission.MANAGE_USERS.getDescription());
            disableManagementView("Ihre Berechtigung zur Benutzerverwaltung wurde entzogen.");
            return;
        }

        User selectedUser = this.tableViewUsers.getSelectionModel().getSelectedItem();
        Role selectedRole = this.comboBoxUserRole.getValue();
        if (selectedUser == null || selectedRole == null) {
            return;
        }

        selectedUser.setRole(selectedRole);
        try {
            this.userDao.update(selectedUser);
            if (AppSession.getCurrentUser() != null && AppSession.getCurrentUser().getUid() == selectedUser.getUid()) {
                AppSession.getCurrentUser().setRole(selectedRole);
            }
            loadUsers();
            selectUserById(selectedUser.getUid());
            showSuccess("Die Rolle für den Nutzer wurde gespeichert.");
            if (!AppSession.hasPermission(Permission.MANAGE_USERS)) {
                disableManagementView("Ihre eigene Rolle wurde geändert. Die Benutzerverwaltung ist nicht mehr verfügbar.");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            showFailure("Die Rolle des Nutzers konnte nicht gespeichert werden.");
        }
    }

    @FXML
    private void handleSaveRole() {
        if (!AppSession.hasPermission(Permission.MANAGE_USERS)) {
            AlertUtil.showPermissionDenied(Permission.MANAGE_USERS.getDescription());
            disableManagementView("Ihre Berechtigung zur Benutzerverwaltung wurde entzogen.");
            return;
        }

        Role selectedRole = this.comboBoxRoleSelection.getValue();
        if (selectedRole == null) {
            return;
        }

        selectedRole.setViewAllowed(this.checkBoxView.isSelected());
        selectedRole.setCreateAllowed(this.checkBoxCreate.isSelected());
        selectedRole.setEditAllowed(this.checkBoxEdit.isSelected());
        selectedRole.setDeleteAllowed(this.checkBoxDelete.isSelected());
        selectedRole.setManageUsersAllowed(this.checkBoxManageUsers.isSelected());

        try {
            this.roleDao.update(selectedRole);
            if (AppSession.getCurrentUser() != null
                    && AppSession.getCurrentUser().getRole() != null
                    && AppSession.getCurrentUser().getRole().getRid() == selectedRole.getRid()) {
                AppSession.getCurrentUser().setRole(selectedRole);
            }
            long selectedRoleId = selectedRole.getRid();
            loadRoles();
            loadUsers();
            selectRoleById(selectedRoleId);
            showSuccess("Die Berechtigungen der Rolle wurden gespeichert.");
            if (!AppSession.hasPermission(Permission.MANAGE_USERS)) {
                disableManagementView("Ihre eigene Rolle wurde geändert. Die Benutzerverwaltung ist nicht mehr verfügbar.");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            showFailure("Die Berechtigungen der Rolle konnten nicht gespeichert werden.");
        }
    }

    private void loadUsers() {
        this.users.clear();
        try {
            this.users.addAll(this.userDao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
            showFailure("Die Nutzer konnten nicht geladen werden.");
        }
    }

    private void loadRoles() {
        this.roles.clear();
        try {
            this.roles.addAll(this.roleDao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
            showFailure("Die Rollen konnten nicht geladen werden.");
        }
    }

    private void selectUserById(long uid) {
        for (User user : this.users) {
            if (user.getUid() == uid) {
                this.tableViewUsers.getSelectionModel().select(user);
                return;
            }
        }
    }

    private void selectRoleById(long rid) {
        for (Role role : this.roles) {
            if (role.getRid() == rid) {
                this.comboBoxRoleSelection.setValue(role);
                return;
            }
        }
    }

    private void clearRolePermissions() {
        this.checkBoxView.setSelected(false);
        this.checkBoxCreate.setSelected(false);
        this.checkBoxEdit.setSelected(false);
        this.checkBoxDelete.setSelected(false);
        this.checkBoxManageUsers.setSelected(false);
    }

    private void disableManagementView(String message) {
        this.users.clear();
        this.roles.clear();
        this.tableViewUsers.setDisable(true);
        this.comboBoxUserRole.setDisable(true);
        this.comboBoxRoleSelection.setDisable(true);
        this.checkBoxView.setDisable(true);
        this.checkBoxCreate.setDisable(true);
        this.checkBoxEdit.setDisable(true);
        this.checkBoxDelete.setDisable(true);
        this.checkBoxManageUsers.setDisable(true);
        this.buttonSaveUserRole.setDisable(true);
        this.buttonSaveRole.setDisable(true);
        this.labelSelectedUsername.setText("Kein Nutzer ausgewählt");
        this.labelStatus.setTextFill(Color.web("#c0392b"));
        this.labelStatus.setText(message);
    }

    private void showSuccess(String message) {
        this.labelStatus.setTextFill(Color.web("#27ae60"));
        this.labelStatus.setText(message);
    }

    private void showFailure(String message) {
        this.labelStatus.setTextFill(Color.web("#c0392b"));
        this.labelStatus.setText(message);
    }
}

