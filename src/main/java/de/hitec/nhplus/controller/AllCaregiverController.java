package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Permission;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.AlertUtil;
import de.hitec.nhplus.utils.AppSession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

public class AllCaregiverController {

    private static final String ACTION_VIEW = "Pflegekraftdaten anzeigen";
    private static final String ACTION_CREATE = "Pflegekräfte anlegen";
    private static final String ACTION_EDIT = "Pflegekraftdaten bearbeiten";
    private static final String ACTION_DELETE = "Pflegekräfte löschen";

    @FXML
    private TableView<Caregiver> tableView;

    @FXML
    private TableColumn<Caregiver, Number> columnId;

    @FXML
    private TableColumn<Caregiver, String> columnSurname;

    @FXML
    private TableColumn<Caregiver, String> columnFirstName;

    @FXML
    private TableColumn<Caregiver, String> columnPhoneNumber;

    @FXML
    private TableColumn<Caregiver, String> columnWeeklyWorkingHours;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonDelete;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldPhoneNumber;

    @FXML
    private TextField textFieldWeeklyWorkingHours;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
    private CaregiverDao dao;

    public void initialize() {
        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));

        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        this.columnPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnWeeklyWorkingHours.setCellValueFactory(new PropertyValueFactory<>("weeklyWorkingHours"));
        this.columnWeeklyWorkingHours.setCellFactory(TextFieldTableCell.forTableColumn());

        this.tableView.setItems(this.caregivers);
        this.tableView.setEditable(AppSession.hasPermission(Permission.EDIT));

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Caregiver>() {
            @Override
            public void changed(ObservableValue<? extends Caregiver> observableValue, Caregiver oldCaregiver, Caregiver newCaregiver) {
                AllCaregiverController.this.buttonDelete.setDisable(newCaregiver == null || !AppSession.hasPermission(Permission.DELETE));
            }
        });

        this.buttonAdd.setDisable(!AppSession.hasPermission(Permission.CREATE));
        ChangeListener<String> inputListener = (observableValue, oldText, newText) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AppSession.hasPermission(Permission.CREATE) || !AllCaregiverController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputListener);
        this.textFieldFirstName.textProperty().addListener(inputListener);
        this.textFieldPhoneNumber.textProperty().addListener(inputListener);
        this.textFieldWeeklyWorkingHours.textProperty().addListener(inputListener);

        this.readAllAndShowInTableView();
        applyPermissionsToForm();
    }

    @FXML
    public void handleOnEditFirstName(TableColumn.CellEditEvent<Caregiver, String> event) {
        if (!ensurePermission(Permission.EDIT, ACTION_EDIT)) {
            this.tableView.refresh();
            return;
        }
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Caregiver, String> event) {
        if (!ensurePermission(Permission.EDIT, ACTION_EDIT)) {
            this.tableView.refresh();
            return;
        }
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditPhoneNumber(TableColumn.CellEditEvent<Caregiver, String> event) {
        if (!ensurePermission(Permission.EDIT, ACTION_EDIT)) {
            this.tableView.refresh();
            return;
        }
        event.getRowValue().setPhoneNumber(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    public void handleOnEditWeeklyWorkingHours(TableColumn.CellEditEvent<Caregiver, String> event) {
        if (!ensurePermission(Permission.EDIT, ACTION_EDIT)) {
            this.tableView.refresh();
            return;
        }
        event.getRowValue().setWeeklyWorkingHours(event.getNewValue());
        this.doUpdate(event);
    }

    private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void readAllAndShowInTableView() {
        this.caregivers.clear();
        this.dao = DaoFactory.getDaoFactory().createCaregiverDao();
        if (!AppSession.hasPermission(Permission.VIEW)) {
            this.tableView.setPlaceholder(new Label("Keine Berechtigung zum Anzeigen von Pflegekraftdaten."));
            return;
        }
        try {
            this.caregivers.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleAdd() {
        if (!ensurePermission(Permission.CREATE, ACTION_CREATE)) {
            return;
        }
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String phoneNumber = this.textFieldPhoneNumber.getText();
        String weeklyWorkingHours = this.textFieldWeeklyWorkingHours.getText();
        try {
            this.dao.create(new Caregiver(firstName, surname, phoneNumber, weeklyWorkingHours));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    @FXML
    public void handleDelete() {
        if (!ensurePermission(Permission.DELETE, ACTION_DELETE)) {
            return;
        }
        Caregiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                this.dao.deleteById(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldPhoneNumber.clear();
        this.textFieldWeeklyWorkingHours.clear();
    }

    private boolean areInputDataValid() {
        return !this.textFieldFirstName.getText().isBlank() &&
                !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldPhoneNumber.getText().isBlank() &&
                !this.textFieldWeeklyWorkingHours.getText().isBlank();
    }

    private void applyPermissionsToForm() {
        boolean canView = AppSession.hasPermission(Permission.VIEW);
        boolean canCreate = AppSession.hasPermission(Permission.CREATE);
        boolean canEdit = AppSession.hasPermission(Permission.EDIT);
        boolean canDelete = AppSession.hasPermission(Permission.DELETE);

        this.tableView.setEditable(canEdit);
        this.buttonAdd.setDisable(!canCreate || !areInputDataValid());
        this.buttonDelete.setDisable(!canDelete || this.tableView.getSelectionModel().getSelectedItem() == null);

        this.textFieldFirstName.setDisable(!canCreate);
        this.textFieldSurname.setDisable(!canCreate);
        this.textFieldPhoneNumber.setDisable(!canCreate);
        this.textFieldWeeklyWorkingHours.setDisable(!canCreate);
        this.tableView.setDisable(!canView);
    }

    private boolean ensurePermission(Permission permission, String actionDescription) {
        if (AppSession.hasPermission(permission)) {
            return true;
        }
        AlertUtil.showPermissionDenied(actionDescription);
        return false;
    }
}
