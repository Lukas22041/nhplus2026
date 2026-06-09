package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO für Nutzer inkl. zugewiesener Rolle.
 */
public class UserDao extends DaoImp<User> {

    public UserDao(Connection connection) {
        super(connection);
    }

    @Override
    protected User getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new User(
                result.getLong("uid"),
                result.getString("username"),
                result.getString("password_hash"),
                result.getString("salt"),
                createRoleFromResultSet(result)
        );
    }

    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (result.next()) {
            list.add(getInstanceFromResultSet(result));
        }
        return list;
    }

    @Override
    protected PreparedStatement getCreateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "INSERT INTO app_user (username, password_hash, salt, role_id) VALUES (?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setLong(4, user.getRole().getRid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.connection.prepareStatement(getSelectBaseSql() + " WHERE u.uid = ?");
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
            preparedStatement = this.connection.prepareStatement(getSelectBaseSql() + " ORDER BY u.username");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "UPDATE app_user SET username = ?, password_hash = ?, salt = ?, role_id = ? WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setLong(4, user.getRole().getRid());
            preparedStatement.setLong(5, user.getUid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String sql = "DELETE FROM app_user WHERE uid = ?";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    public User readByUsername(String username) throws SQLException {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getSelectBaseSql() + " WHERE u.username = ?")) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return getInstanceFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public User readActiveByUsername(String username) throws SQLException {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                getSelectBaseSql() + " WHERE u.username = ? AND u.is_active = 1")) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return getInstanceFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    private String getSelectBaseSql() {
        return "SELECT u.uid, u.username, u.password_hash, u.salt, " +
                "r.rid AS role_rid, r.role_name, r.can_view, r.can_create, r.can_edit, r.can_delete, r.can_manage_users " +
                "FROM app_user u JOIN role r ON u.role_id = r.rid";
    }

    private Role createRoleFromResultSet(ResultSet result) throws SQLException {
        return new Role(
                result.getLong("role_rid"),
                result.getString("role_name"),
                result.getBoolean("can_view"),
                result.getBoolean("can_create"),
                result.getBoolean("can_edit"),
                result.getBoolean("can_delete"),
                result.getBoolean("can_manage_users")
        );
    }
}

