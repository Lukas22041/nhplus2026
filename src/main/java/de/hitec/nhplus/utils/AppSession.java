package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Permission;
import de.hitec.nhplus.model.User;

/**
 * Hält den aktuell angemeldeten Nutzer für die Laufzeit der Anwendung.
 */
public final class AppSession {
    private static User currentUser;

    private AppSession() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean hasPermission(Permission permission) {
        return currentUser != null && currentUser.hasPermission(permission);
    }
}

