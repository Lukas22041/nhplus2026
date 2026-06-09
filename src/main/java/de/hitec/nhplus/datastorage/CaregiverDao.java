package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static de.hitec.nhplus.utils.DateConverter.convertLocalDateToString;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import de.hitec.nhplus.utils.AppSession;

public class CaregiverDao extends DaoImp<Caregiver> {
    private static final Logger LOGGER = Logger.getLogger(CaregiverDao.class.getName());

    /** Gesetzliche Aufbewahrungsfrist in Jahren (§147 AO, §257 HGB). Konfigurierbar. */
    public static final int RETENTION_YEARS = 10;

    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO caregiver (firstname, surname, phonenumber, weeklyworkinghours, " +
                    "deleted, deletion_scheduled_date, scheduled_deletion_date) " +
                    "VALUES (?, ?, ?, ?, 0, NULL, NULL)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getFirstName());
            preparedStatement.setString(2, caregiver.getSurname());
            preparedStatement.setString(3, caregiver.getPhoneNumber());
            preparedStatement.setString(4, caregiver.getWeeklyWorkingHours());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long cid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caregiver WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, cid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        String deletionScheduledStr = result.getString("deletion_scheduled_date");
        String scheduledDeletionStr = result.getString("scheduled_deletion_date");
        return new Caregiver(
                result.getLong("cid"),
                result.getString("firstname"),
                result.getString("surname"),
                result.getString("phonenumber"),
                result.getString("weeklyworkinghours"),
                result.getInt("deleted") == 1,
                deletionScheduledStr != null ? convertStringToLocalDate(deletionScheduledStr) : null,
                scheduledDeletionStr != null ? convertStringToLocalDate(scheduledDeletionStr) : null);
    }

    /**
     * Liest alle aktiven (nicht zur Löschung vorgemerkten) Pflegekräfte.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM caregiver WHERE deleted = 0";
            statement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    /**
     * Liest ALLE Pflegekräfte, auch zur Löschung vorgemerkte (nur für berechtigte Nutzer).
     */
    public List<Caregiver> readAllIncludingDeleted() throws SQLException {
        ensureDeletionAccessAllowed();
        final String SQL = "SELECT * FROM caregiver";
        try (PreparedStatement statement = this.connection.prepareStatement(SQL);
             ResultSet result = statement.executeQuery()) {
            LOGGER.info("Einsicht in zur Loeschung vorgemerkte Pflegekraefte durch Benutzer: "
                    + AppSession.getCurrentUser().getUsername());
            return getListFromResultSet(result);
        }
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Caregiver> list = new ArrayList<>();
        while (result.next()) {
            String deletionScheduledStr = result.getString("deletion_scheduled_date");
            String scheduledDeletionStr = result.getString("scheduled_deletion_date");
            Caregiver caregiver = new Caregiver(
                    result.getLong("cid"),
                    result.getString("firstname"),
                    result.getString("surname"),
                    result.getString("phonenumber"),
                    result.getString("weeklyworkinghours"),
                    result.getInt("deleted") == 1,
                    deletionScheduledStr != null ? convertStringToLocalDate(deletionScheduledStr) : null,
                    scheduledDeletionStr != null ? convertStringToLocalDate(scheduledDeletionStr) : null);
            list.add(caregiver);
        }
        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL =
                    "UPDATE caregiver SET " +
                            "firstname = ?, " +
                            "surname = ?, " +
                            "phonenumber = ?, " +
                            "weeklyworkinghours = ? " +
                            "WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getFirstName());
            preparedStatement.setString(2, caregiver.getSurname());
            preparedStatement.setString(3, caregiver.getPhoneNumber());
            preparedStatement.setString(4, caregiver.getWeeklyWorkingHours());
            preparedStatement.setLong(5, caregiver.getCid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long cid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM caregiver WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, cid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Merkt eine Pflegekraft zur Löschung vor (Soft-Delete).
     * Setzt deleted=1, erfasst das heutige Datum und berechnet das Löschdatum
     * auf Basis der konfigurierten Aufbewahrungsfrist ({@value #RETENTION_YEARS} Jahre).
     *
     * @param cid ID der Pflegekraft
     * @throws SQLException bei Datenbankfehlern
     */
    public void scheduleForDeletion(long cid) throws SQLException {
        ensureDeletionAccessAllowed();
        LocalDate today = LocalDate.now();
        LocalDate deletionDue = today.plusYears(RETENTION_YEARS);
        final String SQL = "UPDATE caregiver SET deleted = 1, " +
                "deletion_scheduled_date = ?, " +
                "scheduled_deletion_date = ? " +
                "WHERE cid = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, convertLocalDateToString(today));
            preparedStatement.setString(2, convertLocalDateToString(deletionDue));
            preparedStatement.setLong(3, cid);
            preparedStatement.executeUpdate();
        }
        int disabledLogins = deactivateLinkedUserAccount(cid);
        LOGGER.info("Pflegekraft " + cid + " zur Loeschung vorgemerkt; deaktivierte Login-Konten: " + disabledLogins);
    }

    private int deactivateLinkedUserAccount(long cid) throws SQLException {
        final String SQL = "UPDATE app_user SET is_active = 0 WHERE caregiver_cid = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, cid);
            return preparedStatement.executeUpdate();
        }
    }

    private void ensureDeletionAccessAllowed() {
        boolean hasRequiredRole = AppSession.hasAnyRole("Wohnbereichsleiter", "Wohnbereichsleitung", "Compliance");
        if (!hasRequiredRole) {
            throw new SecurityException("Nur Wohnbereichsleitung/Compliance darf Loeschungen anstossen.");
        }
    }
}

