package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.PasswordUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link UserDao} using an in-memory SQLite database.
 */
class UserDaoTest {

    private static Connection connection;
    private UserDao dao;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE \"user\" (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT NOT NULL UNIQUE, " +
                            "password_hash TEXT NOT NULL, " +
                            "salt TEXT NOT NULL)"
            );
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        dao = new UserDao(connection);
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM \"user\"");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("create() und readByUsername(): Ein Nutzer wird gespeichert und wiedergefunden")
    void createAndReadByUsername() throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hash("geheim123", salt);
        dao.create(new User("pflege", passwordHash, salt));

        User loaded = dao.readByUsername("pflege");

        assertNotNull(loaded);
        assertEquals("pflege", loaded.getUsername());
        assertEquals(passwordHash, loaded.getPasswordHash());
        assertEquals(salt, loaded.getSalt());
        assertTrue(PasswordUtil.verify("geheim123", loaded.getSalt(), loaded.getPasswordHash()));
    }

    @Test
    @DisplayName("readAll(): Alle gespeicherten Nutzer werden geladen")
    void readAllUsers() throws SQLException {
        String saltOne = PasswordUtil.generateSalt();
        String saltTwo = PasswordUtil.generateSalt();
        dao.create(new User("alpha", PasswordUtil.hash("eins", saltOne), saltOne));
        dao.create(new User("beta", PasswordUtil.hash("zwei", saltTwo), saltTwo));

        List<User> users = dao.readAll();

        assertEquals(2, users.size());
        assertEquals("alpha", users.get(0).getUsername());
        assertEquals("beta", users.get(1).getUsername());
    }
}

