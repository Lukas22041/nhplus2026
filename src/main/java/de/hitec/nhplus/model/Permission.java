package de.hitec.nhplus.model;

/**
 * Beschreibt die fachlichen Berechtigungen innerhalb der Anwendung.
 */
public enum Permission {
    VIEW("Daten anzeigen"),
    EXPORT("Daten exportieren"),
    CREATE("Daten anlegen"),
    EDIT("Daten bearbeiten"),
    DELETE("Daten löschen"),
    MANAGE_USERS("Benutzer und Rollen verwalten");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

