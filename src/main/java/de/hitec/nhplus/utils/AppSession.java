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

    public static boolean hasRole(String roleName) {
        return currentUser != null
                && currentUser.getRole() != null
                && roleName != null
                && roleName.equalsIgnoreCase(currentUser.getRole().getRoleName());
    }

    public static boolean hasAnyRole(String... roleNames) {
        if (currentUser == null || currentUser.getRole() == null || roleNames == null) {
            return false;
        }
        String currentRoleName = currentUser.getRole().getRoleName();
        for (String roleName : roleNames) {
            if (roleName != null && roleName.equalsIgnoreCase(currentRoleName)) {
                return true;
            }
        }
        return false;
    }
}

