package de.hitec.nhplus.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Repräsentiert einen Anwendungsnutzer mit Login-Daten und zugewiesener Rolle.
 */
public class User {
    private final SimpleLongProperty uid;
    private final SimpleStringProperty username;
    private final String passwordHash;
    private final String salt;
    private final ObjectProperty<Role> role;

    public User(String username, String passwordHash, String salt, Role role) {
        this(0, username, passwordHash, salt, role);
    }

    public User(long uid, String username, String passwordHash, String salt, Role role) {
        this.uid = new SimpleLongProperty(uid);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = new SimpleObjectProperty<>(role);
    }

    public long getUid() {
        return uid.get();
    }

    public SimpleLongProperty uidProperty() {
        return uid;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public Role getRole() {
        return role.get();
    }

    public ObjectProperty<Role> roleProperty() {
        return role;
    }

    public void setRole(Role role) {
        this.role.set(role);
    }

    public boolean hasPermission(Permission permission) {
        return getRole() != null && getRole().hasPermission(permission);
    }
}

