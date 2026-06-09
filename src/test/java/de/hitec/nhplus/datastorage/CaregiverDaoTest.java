package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.AppSession;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaregiverDaoTest {

    private static Connection connection;
    private CaregiverDao dao;

    /**
     * Wird einmal vor allen Tests ausgeführt: Erstellt eine Test-Datenbank im Arbeitsspeicher.
     */
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        // In-Memory-Datenbank: existiert nur während der Tests
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS caregiver (" +
                            "   cid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "   firstname TEXT NOT NULL, " +
                            "   surname TEXT NOT NULL, " +
                            "   phonenumber TEXT NOT NULL, " +
                            "   weeklyworkinghours TEXT NOT NULL, " +
                            "   deleted INTEGER NOT NULL DEFAULT 0, " +
                            "   deletion_scheduled_date TEXT, " +
                            "   scheduled_deletion_date TEXT" +
                            ");"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS app_user (" +
                            "   uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "   username TEXT NOT NULL UNIQUE, " +
                            "   password_hash TEXT NOT NULL, " +
                            "   salt TEXT NOT NULL, " +
                            "   role_id INTEGER NOT NULL, " +
                            "   caregiver_cid INTEGER, " +
                            "   is_active INTEGER NOT NULL DEFAULT 1" +
                            ");"
            );
        }
    }

    /**
     * Wird vor jedem einzelnen Test ausgeführt: Erstellt ein frisches DAO-Objekt
     * und leert die Tabelle, damit Tests sich nicht gegenseitig beeinflussen.
     */
    @BeforeEach
    void setUp() throws SQLException {
        dao = new CaregiverDao(connection);
        AppSession.login(new User("admin", "hash", "salt",
                new Role("Wohnbereichsleitung", true, true, true, true, true)));
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM caregiver");
        }
    }

    /**
     * Wird einmal nach allen Tests ausgeführt: Schließt die Datenbankverbindung.
     */
    @AfterAll
    static void tearDown() throws SQLException {
        AppSession.logout();
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("create() und readAll(): Ein neuer Pfleger wird gespeichert und kann gelesen werden")
    void createAndReadAll() throws SQLException {
        // Arrange: Einen neuen Patienten vorbereiten
        Caregiver caregiver = new Caregiver(
                "Max", "Test", "123456", "40"
        );

        // Act: Pfleger in der Datenbank anlegen und alle Patienten auslesen
        dao.create(caregiver);
        List<Caregiver> allPatients = dao.readAll();

        // Assert: Genau ein Pfleger sollte vorhanden sein
        assertEquals(1, allPatients.size(), "Es sollte genau ein Patient in der Datenbank sein");

        Caregiver loaded = allPatients.get(0);
        assertEquals("Max", loaded.getFirstName());
        assertEquals("Test", loaded.getSurname());
        assertEquals("123456", loaded.getPhoneNumber());
        assertEquals("40", loaded.getWeeklyWorkingHours());
    }

    @Test
    @DisplayName("deleteById(): Ein gelöschter Pfleger ist nicht mehr auffindbar")
    void deleteById() throws SQLException {
        // Arrange: Einen Pfleger anlegen
        Caregiver caregiver = new Caregiver(
                "Max", "Test", "123456", "40"
        );
        dao.create(caregiver);

        // Die ID des angelegten Pfleger ermitteln
        List<Caregiver> allPatients = dao.readAll();
        assertEquals(1, allPatients.size());
        long pid = allPatients.get(0).getCid();

        // Act: Pfleger löschen
        dao.deleteById(pid);

        // Assert: Kein Pfleger sollte mehr vorhanden sein
        List<Caregiver> afterDelete = dao.readAll();
        assertTrue(afterDelete.isEmpty(), "Nach dem Löschen sollte kein Pfleger mehr vorhanden sein");
    }

    @Test
    @DisplayName("scheduleForDeletion(): Ein vorgemerkter Pfleger ist in readAll() nicht mehr sichtbar")
    void scheduleForDeletionHidesFromActiveList() throws SQLException {
        Caregiver caregiver = new Caregiver("Max", "Test", "123456", "40");
        dao.create(caregiver);

        List<Caregiver> allCaregivers = dao.readAll();
        assertEquals(1, allCaregivers.size());
        long cid = allCaregivers.get(0).getCid();

        dao.scheduleForDeletion(cid);

        List<Caregiver> activeCaregivers = dao.readAll();
        List<Caregiver> includingDeleted = dao.readAllIncludingDeleted();

        assertTrue(activeCaregivers.isEmpty(), "Vorgemerkte Pfleger dürfen nicht in der Standardliste erscheinen");
        assertEquals(1, includingDeleted.size(), "In der erweiterten Liste muss der Datensatz weiterhin vorhanden sein");
        assertTrue(includingDeleted.get(0).isDeleted(), "Der Datensatz muss als gelöscht markiert sein");
    }
}
