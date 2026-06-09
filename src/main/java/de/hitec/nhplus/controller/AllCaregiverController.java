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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AllCaregiverController {

    private static final String ACTION_VIEW = "Pflegekraftdaten anzeigen";
    private static final String ACTION_CREATE = "Pflegekräfte anlegen";
    private static final String ACTION_EDIT = "Pflegekraftdaten bearbeiten";
    private static final String ACTION_DELETE = "Pflegekräfte zur Löschung vormerken";

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

    @FXML
    private CheckBox checkBoxShowScheduledDeletions;

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
        this.tableView.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(Caregiver caregiver, boolean empty) {
                super.updateItem(caregiver, empty);
                if (empty || caregiver == null) {
                    getStyleClass().remove("caregiver-scheduled-delete-row");
                    return;
                }
                if (caregiver.isDeleted()) {
                    if (!getStyleClass().contains("caregiver-scheduled-delete-row")) {
                        getStyleClass().add("caregiver-scheduled-delete-row");
                    }
                } else {
                    getStyleClass().remove("caregiver-scheduled-delete-row");
                }
            }
        });

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Caregiver>() {
            @Override
            public void changed(ObservableValue<? extends Caregiver> observableValue, Caregiver oldCaregiver, Caregiver newCaregiver) {
                AllCaregiverController.this.buttonDelete.setDisable(newCaregiver == null
                        || !AppSession.hasPermission(Permission.DELETE)
                        || newCaregiver.isDeleted());
            }
        });

        this.buttonAdd.setDisable(!AppSession.hasPermission(Permission.CREATE));
        ChangeListener<String> inputListener = (observableValue, oldText, newText) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AppSession.hasPermission(Permission.CREATE) || !AllCaregiverController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputListener);
        this.textFieldFirstName.textProperty().addListener(inputListener);
        this.textFieldPhoneNumber.textProperty().addListener(inputListener);
        this.textFieldWeeklyWorkingHours.textProperty().addListener(inputListener);

        this.checkBoxShowScheduledDeletions.setVisible(canManageDeletionQueue());
        this.checkBoxShowScheduledDeletions.setManaged(canManageDeletionQueue());
        this.checkBoxShowScheduledDeletions.setSelected(true);
        this.checkBoxShowScheduledDeletions.selectedProperty().addListener((observable, oldValue, newValue) -> readAllAndShowInTableView());

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
            if (this.checkBoxShowScheduledDeletions != null
                    && this.checkBoxShowScheduledDeletions.isVisible()
                    && this.checkBoxShowScheduledDeletions.isSelected()) {
                this.caregivers.addAll(this.dao.readAllIncludingDeleted());
            } else {
                this.caregivers.addAll(this.dao.readAll());
            }
        } catch (SecurityException securityException) {
            AlertUtil.showPermissionDenied(ACTION_VIEW + " (inkl. vorgemerkter Datensätze)");
            if (this.checkBoxShowScheduledDeletions != null) {
                this.checkBoxShowScheduledDeletions.setSelected(false);
            }
            this.caregivers.clear();
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
        if (!ensurePermission(Permission.DELETE, ACTION_DELETE) || !canManageDeletionQueue()) {
            if (!canManageDeletionQueue()) {
                AlertUtil.showPermissionDenied(ACTION_DELETE);
            }
            return;
        }
        Caregiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        if (selectedItem.isDeleted()) {
            AlertUtil.showWarning("Löschvormerkung nicht möglich",
                    "Datensatz bereits vorgemerkt",
                    "Diese Pflegekraft ist bereits zur Löschung vorgemerkt und kann nicht erneut vorgemerkt werden.");
            return;
        }

        LocalDate deletionDueDate = LocalDate.now().plusYears(CaregiverDao.RETENTION_YEARS);
        String formattedDate = deletionDueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Zur Löschung vormerken");
        confirm.setHeaderText("Pflegekraft zur Löschung vormerken");
        confirm.setContentText(
                "Pflegekraft: " + selectedItem.getFirstName() + " " + selectedItem.getSurname() + "\n\n" +
                "Der Datensatz wird als \"zur Löschung vorgemerkt\" markiert und ist\n" +
                "für normale Nutzer nicht mehr sichtbar.\n\n" +
                "Aufbewahrungsfrist: " + CaregiverDao.RETENTION_YEARS + " Jahre (gem. §147 AO / §257 HGB)\n" +
                "Geplantes Löschdatum: " + formattedDate + "\n\n" +
                "Möchten Sie fortfahren?"
        );

        ButtonType confirmButton = new ButtonType("Vormerken", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            try {
                this.dao.scheduleForDeletion(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SecurityException securityException) {
                AlertUtil.showPermissionDenied(ACTION_DELETE);
            } catch (SQLException exception) {
                exception.printStackTrace();
                AlertUtil.showError("Fehler", "Löschvormerkung fehlgeschlagen",
                        "Der Datensatz konnte nicht vorgemerkt werden: " + exception.getMessage());
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
        Caregiver selectedCaregiver = this.tableView.getSelectionModel().getSelectedItem();
        boolean canScheduleDeletion = canDelete && canManageDeletionQueue()
                && selectedCaregiver != null && !selectedCaregiver.isDeleted();

        this.tableView.setEditable(canEdit);
        this.buttonAdd.setDisable(!canCreate || !areInputDataValid());
        this.buttonDelete.setDisable(!canScheduleDeletion || this.tableView.getSelectionModel().getSelectedItem() == null);

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

    private boolean canManageDeletionQueue() {
        return AppSession.hasAnyRole("Wohnbereichsleiter", "Wohnbereichsleitung", "Compliance");
    }
}
