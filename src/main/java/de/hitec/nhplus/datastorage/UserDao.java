package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides database access for {@link User} objects.
 * Implements CRUD operations and adds a convenience method to load users by username.
 */
public class UserDao extends DaoImp<User> {

    /**
     * Creates a new DAO using the given database connection.
     *
     * @param connection Open database connection.
     */
    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Loads a user by its unique username.
     *
     * @param username Username to search for.
     * @return Matching user or {@code null} if no user exists for the username.
     * @throws SQLException if the query fails.
     */
    public User readByUsername(String username) throws SQLException {
        final String sql = "SELECT * FROM \"user\" WHERE username = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return getInstanceFromResultSet(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Generates a {@link PreparedStatement} to insert a new user.
     *
     * @param user User object to persist.
     * @return Insert statement for the given user.
     */
    @Override
    protected PreparedStatement getCreateStatement(User user) {
        try {
            final String sql = "INSERT INTO \"user\" (username, password_hash, salt) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a {@link PreparedStatement} to load a user by id.
     *
     * @param id User id to search for.
     * @return Query statement for the id.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long id) {
        try {
            final String sql = "SELECT * FROM \"user\" WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Maps the current row of a result set to a {@link User} object.
     *
     * @param result Result set positioned on a user row.
     * @return User containing the row data.
     * @throws SQLException if the row cannot be read.
     */
    @Override
    protected User getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new User(
                result.getLong("id"),
                result.getString("username"),
                result.getString("password_hash"),
                result.getString("salt")
        );
    }

    /**
     * Generates a {@link PreparedStatement} to load all users.
     *
     * @return Query statement for all users.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        try {
            return this.connection.prepareStatement("SELECT * FROM \"user\" ORDER BY username");
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Maps all rows of a result set to a list of {@link User} objects.
     *
     * @param result Result set with zero or more user rows.
     * @return List of mapped users.
     * @throws SQLException if the result set cannot be read.
     */
    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        while (result.next()) {
            users.add(getInstanceFromResultSet(result));
        }
        return users;
    }

    /**
     * Generates a {@link PreparedStatement} to update a user.
     *
     * @param user User object with changed values.
     * @return Update statement for the user.
     */
    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        try {
            final String sql = "UPDATE \"user\" SET username = ?, password_hash = ?, salt = ? WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setLong(4, user.getId());
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a {@link PreparedStatement} to delete a user by id.
     *
     * @param id Id of the user to delete.
     * @return Delete statement for the id.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long id) {
        try {
            final String sql = "DELETE FROM \"user\" WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            return preparedStatement;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}

