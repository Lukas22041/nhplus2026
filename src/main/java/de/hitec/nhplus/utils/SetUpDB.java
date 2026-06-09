package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.RoleDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalTime;

/**
 * Call static class provides to static methods to set up and wipe the database. It uses the class ConnectionBuilder
 * and its path to build up the connection to the database. The class is executable. Executing the class will build
 * up a connection to the database and calls setUpDb() to wipe the database, build up a clean database and fill the
 * database with some test data.
 */
public class SetUpDB {

    private static final String ROLE_MANAGER = "Wohnbereichsleitung";
    private static final String ROLE_CAREGIVER = "Pflegekraft";
    private static final String ROLE_READER = "Leserechte";
    private static final String ROLE_BLOCKED = "Keine Rechte";

    /**
     * This method wipes the database by dropping the tables. Then the method calls DDL statements to build it up from
     * scratch and DML statements to fill the database with hard coded test data.
     */
    public static void setUpDb() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpDB.wipeDb(connection);
        initializeDb();
    }

    /**
     * Stellt sicher, dass alle Tabellen vorhanden sind und legt Standarddaten nur an,
     * wenn sie noch fehlen. Bereits vorhandene Daten bleiben erhalten.
     */
    public static void initializeDb() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpDB.setUpTablePatient(connection);
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpTableCaregiver(connection);
        SetUpDB.setUpTableRole(connection);
        SetUpDB.setUpTableUser(connection);

        try {
            if (isTableEmpty(connection, "patient")) {
                SetUpDB.setUpPatients();
            }
            if (isTableEmpty(connection, "treatment")) {
                SetUpDB.setUpTreatments();
            }
            if (isTableEmpty(connection, "caregiver")) {
                SetUpDB.setUpCaregivers();
            }
            SetUpDB.ensureDefaultRoles();
            SetUpDB.ensureDefaultUsers();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method wipes the database by dropping the tables.
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS treatment");
            statement.execute("DROP TABLE IF EXISTS app_user");
            statement.execute("DROP TABLE IF EXISTS patient");
            statement.execute("DROP TABLE IF EXISTS caregiver");
            statement.execute("DROP TABLE IF EXISTS role");
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTablePatient(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS patient (" +
                "   pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   dateOfBirth TEXT NOT NULL, " +
                "   carelevel TEXT NOT NULL, " +
                "   roomnumber TEXT NOT NULL, " +
                "   assets TEXT NOT NULL" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTableTreatment(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS treatment (" +
                "   tid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   pid INTEGER NOT NULL, " +
                "   treatment_date TEXT NOT NULL, " +
                "   begin TEXT NOT NULL, " +
                "   end TEXT NOT NULL, " +
                "   description TEXT NOT NULL, " +
                "   remark TEXT NOT NULL," +
                "   FOREIGN KEY (pid) REFERENCES patient (pid) ON DELETE CASCADE " +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTableRole(Connection connection) {
        final String sql = "CREATE TABLE IF NOT EXISTS role (" +
                "   rid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   role_name TEXT NOT NULL UNIQUE, " +
                "   can_view INTEGER NOT NULL, " +
                "   can_create INTEGER NOT NULL, " +
                "   can_edit INTEGER NOT NULL, " +
                "   can_delete INTEGER NOT NULL, " +
                "   can_manage_users INTEGER NOT NULL" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTableUser(Connection connection) {
        final String sql = "CREATE TABLE IF NOT EXISTS app_user (" +
                "   uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   username TEXT NOT NULL UNIQUE, " +
                "   password_hash TEXT NOT NULL, " +
                "   salt TEXT NOT NULL, " +
                "   role_id INTEGER NOT NULL, " +
                "   caregiver_cid INTEGER, " +
                "   is_active INTEGER NOT NULL DEFAULT 1, " +
                "   FOREIGN KEY (role_id) REFERENCES role (rid) ON DELETE RESTRICT" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        migrateTableUser(connection);
    }

    private static void migrateTableUser(Connection connection) {
        String[] migrations = {
                "ALTER TABLE app_user ADD COLUMN caregiver_cid INTEGER",
                "ALTER TABLE app_user ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1"
        };
        try (Statement statement = connection.createStatement()) {
            for (String sql : migrations) {
                try {
                    statement.execute(sql);
                } catch (SQLException ignored) {
                    // Spalte existiert bereits – ignorieren
                }
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }


    private static void setUpPatients() {
        try {
            PatientDao dao = DaoFactory.getDaoFactory().createPatientDao();
            dao.create(new Patient("Seppl", "Herberger", convertStringToLocalDate("1945-12-01"), "4", "202", "vermögend"));
            dao.create(new Patient("Martina", "Gerdsen", convertStringToLocalDate("1954-08-12"), "5", "010", "arm"));
            dao.create(new Patient("Gertrud", "Franzen", convertStringToLocalDate("1949-04-16"), "3", "002", "normal"));
            dao.create(new Patient("Ahmet", "Yilmaz", convertStringToLocalDate("1941-02-22"), "3", "013", "normal"));
            dao.create(new Patient("Hans", "Neumann", convertStringToLocalDate("1955-12-12"), "2", "001", "sehr vermögend"));
            dao.create(new Patient("Elisabeth", "Müller", convertStringToLocalDate("1958-03-07"), "5", "110", "arm"));

            dao.create(new Patient("Henrik", "B", convertStringToLocalDate("2006-06-12"), "1", "112", "arm"));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTreatments() {
        try {
            TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
            dao.create(new Treatment(1, 1, convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Der Patient hat enorme Angstgefühle und glaubt, er sei überfallen worden. Ihm seien alle Wertsachen gestohlen worden.\nPatient beruhigt sich erst, als alle Wertsachen im Zimmer gefunden worden sind."));
            dao.create(new Treatment(2, 1, convertStringToLocalDate("2023-06-05"), convertStringToLocalTime("11:00"), convertStringToLocalTime("12:30"), "Gespräch", "Patient irrt auf der Suche nach gestohlenen Wertsachen durch die Etage und bezichtigt andere Bewohner des Diebstahls.\nPatient wird in seinen Raum zurückbegleitet und erhält Beruhigungsmittel."));
            dao.create(new Treatment(3, 2, convertStringToLocalDate("2023-06-04"), convertStringToLocalTime("07:30"), convertStringToLocalTime("08:00"), "Waschen", "Patient mit Waschlappen gewaschen und frisch angezogen. Patient gewendet."));
            dao.create(new Treatment(4, 1, convertStringToLocalDate("2023-06-06"), convertStringToLocalTime("15:10"), convertStringToLocalTime("16:00"), "Spaziergang", "Spaziergang im Park, Patient döst  im Rollstuhl ein"));
            dao.create(new Treatment(8, 1, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("16:00"), "Spaziergang", "Parkspaziergang; Patient ist heute lebhafter und hat klare Momente; erzählt von seiner Tochter"));
            dao.create(new Treatment(9, 2, convertStringToLocalDate("2023-06-07"), convertStringToLocalTime("11:00"), convertStringToLocalTime("11:30"), "Waschen", "Waschen per Dusche auf einem Stuhl; Patientin gewendet;"));
            dao.create(new Treatment(12, 5, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("15:30"), "Physiotherapie", "Übungen zur Stabilisation und Mobilisierung der Rückenmuskulatur"));
            dao.create(new Treatment(14, 4, convertStringToLocalDate("2023-08-24"), convertStringToLocalTime("09:30"), convertStringToLocalTime("10:15"), "KG", "Lympfdrainage"));
            dao.create(new Treatment(16, 6, convertStringToLocalDate("2023-08-31"), convertStringToLocalTime("13:30"), convertStringToLocalTime("13:45"), "Toilettengang", "Hilfe beim Toilettengang; Patientin klagt über Schmerzen beim Stuhlgang. Gabe von Iberogast"));
            dao.create(new Treatment(17, 6, convertStringToLocalDate("2023-09-01"), convertStringToLocalTime("16:00"), convertStringToLocalTime("17:00"), "KG", "Massage der Extremitäten zur Verbesserung der Durchblutung"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTableCaregiver(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS caregiver (" +
                "   cid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   phonenumber TEXT NOT NULL, " +
                "   weeklyworkinghours TEXT NOT NULL, " +
                "   deleted INTEGER NOT NULL DEFAULT 0, " +
                "   deletion_scheduled_date TEXT, " +
                "   scheduled_deletion_date TEXT" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        // Migration: Spalten ergänzen, falls die Tabelle bereits ohne sie existiert
        migrateTableCaregiver(connection);
    }

    /**
     * Fügt fehlende Soft-Delete-Spalten zur bestehenden caregiver-Tabelle hinzu.
     * SQLite unterstützt kein „ADD COLUMN IF NOT EXISTS", daher werden Fehler ignoriert.
     */
    private static void migrateTableCaregiver(Connection connection) {
        String[] migrations = {
                "ALTER TABLE caregiver ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE caregiver ADD COLUMN deletion_scheduled_date TEXT",
                "ALTER TABLE caregiver ADD COLUMN scheduled_deletion_date TEXT"
        };
        try (Statement statement = connection.createStatement()) {
            for (String sql : migrations) {
                try {
                    statement.execute(sql);
                } catch (SQLException ignored) {
                    // Spalte existiert bereits – ignorieren
                }
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpCaregivers() {
        try {
            CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDao();
            dao.create(new Caregiver("Anna", "Schmidt", "0421-123456", "38"));
            dao.create(new Caregiver("Klaus", "Müller", "0421-234567", "35"));
            dao.create(new Caregiver("Maria", "Weber", "0421-345678", "40"));
            dao.create(new Caregiver("Thomas", "Fischer", "0421-456789", "32"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void ensureDefaultRoles() throws SQLException {
        RoleDao roleDao = DaoFactory.getDaoFactory().createRoleDao();
        ensureRole(roleDao, new Role(ROLE_MANAGER, true, true, true, true, true));
        ensureRole(roleDao, new Role(ROLE_CAREGIVER, true, true, true, false, false));
        ensureRole(roleDao, new Role(ROLE_READER, true, false, false, false, false));
        ensureRole(roleDao, new Role(ROLE_BLOCKED, false, false, false, false, false));
    }

    private static void ensureRole(RoleDao roleDao, Role role) throws SQLException {
        if (roleDao.readByName(role.getRoleName()) == null) {
            roleDao.create(role);
        }
    }

    private static void ensureDefaultUsers() throws SQLException {
        RoleDao roleDao = DaoFactory.getDaoFactory().createRoleDao();
        UserDao userDao = DaoFactory.getDaoFactory().createUserDao();

        ensureUser(userDao, "admin", "admin123", roleDao.readByName(ROLE_MANAGER));
        ensureUser(userDao, "pflege", "pflege", roleDao.readByName(ROLE_CAREGIVER));
        ensureUser(userDao, "guest", "qwerty", roleDao.readByName(ROLE_READER));
        ensureUser(userDao, "blocked", "password", roleDao.readByName(ROLE_BLOCKED));
    }

    private static void ensureUser(UserDao userDao, String username, String password, Role role) throws SQLException {
        if (role == null || userDao.readByUsername(username) != null) {
            return;
        }
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(password, salt);
        userDao.create(new User(username, hash, salt, role));
    }

    private static boolean isTableEmpty(Connection connection, String tableName) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    public static void main(String[] args) {
        SetUpDB.setUpDb();
    }
}
