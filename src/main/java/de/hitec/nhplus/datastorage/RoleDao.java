package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO für Rollen und deren Berechtigungen.
 */
public class RoleDao extends DaoImp<Role> {

    public RoleDao(Connection connection) {
        super(connection);
    }

    @Override
    protected Role getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Role(
                result.getLong("rid"),
                result.getString("role_name"),
                result.getBoolean("can_view"),
                result.getBoolean("can_create"),
                result.getBoolean("can_edit"),
                result.getBoolean("can_delete"),
                result.getBoolean("can_manage_users")
        );
    }

    @Override
    protected ArrayList<Role> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Role> list = new ArrayList<>();
        while (result.next()) {
            list.add(getInstanceFromResultSet(result));
        }
        return list;
    }

    @Override
    protected PreparedStatement getCreateStatement(Role role) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "INSERT INTO role (role_name, can_view, can_create, can_edit, can_delete, can_manage_users) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, role.getRoleName());
            preparedStatement.setBoolean(2, role.isViewAllowed());
            preparedStatement.setBoolean(3, role.isCreateAllowed());
            preparedStatement.setBoolean(4, role.isEditAllowed());
            preparedStatement.setBoolean(5, role.isDeleteAllowed());
            preparedStatement.setBoolean(6, role.isManageUsersAllowed());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "SELECT * FROM role WHERE rid = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "SELECT * FROM role ORDER BY role_name";
            preparedStatement = this.connection.prepareStatement(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Role role) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "UPDATE role SET role_name = ?, can_view = ?, can_create = ?, can_edit = ?, can_delete = ?, can_manage_users = ? WHERE rid = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, role.getRoleName());
            preparedStatement.setBoolean(2, role.isViewAllowed());
            preparedStatement.setBoolean(3, role.isCreateAllowed());
            preparedStatement.setBoolean(4, role.isEditAllowed());
            preparedStatement.setBoolean(5, role.isDeleteAllowed());
            preparedStatement.setBoolean(6, role.isManageUsersAllowed());
            preparedStatement.setLong(7, role.getRid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "DELETE FROM role WHERE rid = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    public Role readByName(String roleName) throws SQLException {
        final String sql = "SELECT * FROM role WHERE role_name = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setString(1, roleName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return getInstanceFromResultSet(resultSet);
                }
            }
        }
        return null;
    }
}

