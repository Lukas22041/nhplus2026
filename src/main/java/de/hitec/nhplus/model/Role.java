package de.hitec.nhplus.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * Repräsentiert eine Rolle mit den dazugehörigen Berechtigungen.
 */
public class Role {
    private final SimpleLongProperty rid;
    private final SimpleStringProperty roleName;
    private final SimpleBooleanProperty viewAllowed;
    private final SimpleBooleanProperty createAllowed;
    private final SimpleBooleanProperty editAllowed;
    private final SimpleBooleanProperty deleteAllowed;
    private final SimpleBooleanProperty manageUsersAllowed;

    public Role(String roleName, boolean viewAllowed, boolean createAllowed, boolean editAllowed,
                boolean deleteAllowed, boolean manageUsersAllowed) {
        this(0, roleName, viewAllowed, createAllowed, editAllowed, deleteAllowed, manageUsersAllowed);
    }

    public Role(long rid, String roleName, boolean viewAllowed, boolean createAllowed, boolean editAllowed,
                boolean deleteAllowed, boolean manageUsersAllowed) {
        this.rid = new SimpleLongProperty(rid);
        this.roleName = new SimpleStringProperty(roleName);
        this.viewAllowed = new SimpleBooleanProperty(viewAllowed);
        this.createAllowed = new SimpleBooleanProperty(createAllowed);
        this.editAllowed = new SimpleBooleanProperty(editAllowed);
        this.deleteAllowed = new SimpleBooleanProperty(deleteAllowed);
        this.manageUsersAllowed = new SimpleBooleanProperty(manageUsersAllowed);
    }

    public long getRid() {
        return rid.get();
    }

    public SimpleLongProperty ridProperty() {
        return rid;
    }

    public String getRoleName() {
        return roleName.get();
    }

    public SimpleStringProperty roleNameProperty() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    public boolean isViewAllowed() {
        return viewAllowed.get();
    }

    public SimpleBooleanProperty viewAllowedProperty() {
        return viewAllowed;
    }

    public void setViewAllowed(boolean viewAllowed) {
        this.viewAllowed.set(viewAllowed);
    }

    public boolean isCreateAllowed() {
        return createAllowed.get();
    }

    public SimpleBooleanProperty createAllowedProperty() {
        return createAllowed;
    }

    public void setCreateAllowed(boolean createAllowed) {
        this.createAllowed.set(createAllowed);
    }

    public boolean isEditAllowed() {
        return editAllowed.get();
    }

    public SimpleBooleanProperty editAllowedProperty() {
        return editAllowed;
    }

    public void setEditAllowed(boolean editAllowed) {
        this.editAllowed.set(editAllowed);
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed.get();
    }

    public SimpleBooleanProperty deleteAllowedProperty() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed.set(deleteAllowed);
    }

    public boolean isManageUsersAllowed() {
        return manageUsersAllowed.get();
    }

    public SimpleBooleanProperty manageUsersAllowedProperty() {
        return manageUsersAllowed;
    }

    public void setManageUsersAllowed(boolean manageUsersAllowed) {
        this.manageUsersAllowed.set(manageUsersAllowed);
    }

    public boolean hasPermission(Permission permission) {
        return switch (permission) {
            case VIEW -> isViewAllowed();
            case EXPORT -> isViewAllowed();
            case CREATE -> isCreateAllowed();
            case EDIT -> isEditAllowed();
            case DELETE -> isDeleteAllowed();
            case MANAGE_USERS -> isManageUsersAllowed();
        };
    }

    @Override
    public String toString() {
        return getRoleName();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Role role)) {
            return false;
        }
        return getRid() == role.getRid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRid());
    }
}

