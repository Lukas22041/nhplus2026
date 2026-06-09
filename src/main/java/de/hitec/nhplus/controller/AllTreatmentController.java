package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Permission;
import de.hitec.nhplus.utils.AlertUtil;
import de.hitec.nhplus.utils.AppSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AllTreatmentController {

    private static final String ACTION_VIEW = "Behandlungsdaten anzeigen";
    private static final String ACTION_CREATE = "Behandlungen anlegen";
    private static final String ACTION_EDIT = "Behandlungen bearbeiten";
    private static final String ACTION_DELETE = "Behandlungen löschen";

    @FXML
    private TableView<Treatment> tableView;

    @FXML
    private TableColumn<Treatment, Number> columnId;

    @FXML
    private TableColumn<Treatment, Number> columnPid;

    @FXML
    private TableColumn<Treatment, String> columnDate;

    @FXML
    private TableColumn<Treatment, String> columnBegin;

    @FXML
    private TableColumn<Treatment, String> columnEnd;

    @FXML
    private TableColumn<Treatment, String> columnDescription;

    @FXML
    private ComboBox<String> comboBoxPatientSelection;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonNewTreament;

    private TreatmentDao dao;
    private final ObservableList<String> patientSelection = FXCollections.observableArrayList();
    private final ObservableList<Treatment> treatments = FXCollections.observableArrayList();
    private ArrayList<Patient> patientList = new ArrayList<>();


    public void initialize() {
        this.columnId.setCellValueFactory(new PropertyValueFactory<>("tid"));
        this.columnPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        this.columnEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.tableView.setItems(this.treatments);

        this.comboBoxPatientSelection.setItems(patientSelection);
        this.comboBoxPatientSelection.getSelectionModel().select(0);

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldTreatment, newTreatment) ->
                        AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null || !AppSession.hasPermission(Permission.DELETE)));

        readAllAndShowInTableView();
        this.createComboBoxData();
        applyPermissionsToControls();
    }

    public void readAllAndShowInTableView() {
        this.treatments.clear();
        if (!AppSession.hasPermission(Permission.VIEW)) {
            this.tableView.setPlaceholder(new Label("Keine Berechtigung zum Anzeigen von Behandlungen."));
            return;
        }
        if (comboBoxPatientSelection != null) {
            comboBoxPatientSelection.getSelectionModel().select(0);
        }
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            this.treatments.addAll(dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void createComboBoxData() {
        patientSelection.clear();
        patientSelection.add("alle");

        if (!AppSession.hasPermission(Permission.VIEW)) {
            comboBoxPatientSelection.setItems(patientSelection);
            comboBoxPatientSelection.getSelectionModel().selectFirst();
            return;
        }

        PatientDao dao = DaoFactory.getDaoFactory().createPatientDao();
        try {
            patientList = (ArrayList<Patient>) dao.readAll();
            for (Patient patient: patientList) {
                this.patientSelection.add(formatPatientDisplayName(patient));
            }
            comboBoxPatientSelection.setItems(patientSelection);
            comboBoxPatientSelection.getSelectionModel().selectFirst(); // "alle" wird vorausgewählt
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private String formatPatientDisplayName(Patient patient) {
        return String.format("%s, %s", patient.getSurname(), patient.getFirstName());
    }

    @FXML
    public void handleComboBox() {
        if (!AppSession.hasPermission(Permission.VIEW)) {
            return;
        }
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        this.treatments.clear();
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();

        if (selectedPatient == null || selectedPatient.equals("alle")) {
            try {
                this.treatments.addAll(this.dao.readAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        else {
            Patient patient = getPatientFromDisplayName(selectedPatient);
            if (patient != null) {
                try {
                    this.treatments.addAll(this.dao.readTreatmentsByPid(patient.getPid()));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private Patient getPatientFromDisplayName(String displayName) {
        if (patientList == null) {
            return null;
        }
        for (Patient patient : patientList) {
            if (displayName.equals(formatPatientDisplayName(patient))) {
                return patient;
            }
        }
        return null;
    }

    @FXML
    public void handleDelete() {
        if (!ensurePermission(Permission.DELETE, ACTION_DELETE)) {
            return;
        }
        int index = this.tableView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        Treatment t = this.treatments.remove(index);
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.deleteById(t.getTid());
            this.buttonDelete.setDisable(true);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleNewTreatment() {
        if (!ensurePermission(Permission.CREATE, ACTION_CREATE)) {
            return;
        }
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        if (selectedPatient == null || selectedPatient.equals("alle")) {
            AlertUtil.showInfo("Information", "Patient für die Behandlung fehlt!", "Wählen Sie über die Combobox einen Patienten aus!");
            return;
        }
        Patient patient = getPatientFromDisplayName(selectedPatient);
        if (patient == null) {
            AlertUtil.showInfo("Information", "Patient nicht gefunden!", "Der ausgewählte Patient konnte nicht gefunden werden.");
            return;
        }
        newTreatmentWindow(patient);
    }

    @FXML
    public void handleMouseClick(javafx.scene.input.MouseEvent event) {
        if (!AppSession.hasPermission(Permission.VIEW)) {
            return;
        }
        if (event.getClickCount() >= 2 && tableView.getSelectionModel().getSelectedItem() != null) {
            int index = this.tableView.getSelectionModel().getSelectedIndex();
            Treatment treatment = this.treatments.get(index);
            treatmentWindow(treatment);
        }
    }

    public void newTreatmentWindow(Patient patient) {
        if (!ensurePermission(Permission.CREATE, ACTION_CREATE)) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/NewTreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();

            NewTreatmentController controller = loader.getController();
            controller.initialize(this, stage, patient);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void treatmentWindow(Treatment treatment){
        if (!ensurePermission(Permission.VIEW, ACTION_VIEW)) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/TreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();
            TreatmentController controller = loader.getController();
            controller.initializeController(this, stage, treatment);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void applyPermissionsToControls() {
        boolean canView = AppSession.hasPermission(Permission.VIEW);
        boolean canCreate = AppSession.hasPermission(Permission.CREATE);
        boolean canDelete = AppSession.hasPermission(Permission.DELETE);

        this.tableView.setDisable(!canView);
        this.comboBoxPatientSelection.setDisable(!canView);
        this.buttonNewTreament.setDisable(!canCreate);
        this.buttonDelete.setDisable(!canDelete || this.tableView.getSelectionModel().getSelectedItem() == null);
    }

    private boolean ensurePermission(Permission permission, String actionDescription) {
        if (AppSession.hasPermission(permission)) {
            return true;
        }
        AlertUtil.showPermissionDenied(actionDescription);
        return false;
    }
}
