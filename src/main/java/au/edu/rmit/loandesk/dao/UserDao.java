package au.edu.rmit.loandesk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import au.edu.rmit.loandesk.model.PasswordCredentials;
import au.edu.rmit.loandesk.model.User;

public class UserDao {

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public int createUser(
            String username,
            String firstName,
            String lastName,
            String passwordHash,
            String passwordSalt,
            String passwordAlgorithm,
            int passwordIterations,
            int passwordKeyLength) throws SQLException {

        String sql = """
                INSERT INTO users (
                    username, first_name, last_name,
                    password_hash, password_salt,
                    password_algorithm, password_iterations,
                    password_key_length
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, passwordHash);
            statement.setString(5, passwordSalt);
            statement.setString(6, passwordAlgorithm);
            statement.setInt(7, passwordIterations);
            statement.setInt(8, passwordKeyLength);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Creating user failed: no ID was returned.");
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = """
                SELECT id, username, first_name, last_name
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    User user = new User(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("first_name"),
                            result.getString("last_name"));

                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    public boolean updateProfile(
            int userId, String firstName, String lastName)
            throws SQLException {

        String sql = """
                UPDATE users
                SET first_name = ?, last_name = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setInt(3, userId);

            return statement.executeUpdate() == 1;
        }
    }

    public Optional<PasswordCredentials> findCredentialsByUsername(
            String username) throws SQLException {

        String sql = """
                SELECT id, username, password_hash, password_salt,
                       password_algorithm, password_iterations,
                       password_key_length
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    PasswordCredentials credentials = new PasswordCredentials(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("password_hash"),
                            result.getString("password_salt"),
                            result.getString("password_algorithm"),
                            result.getInt("password_iterations"),
                            result.getInt("password_key_length"));

                    return Optional.of(credentials);
                }
            }
        }

        return Optional.empty();
    }

    public boolean updatePasswordCredentials(
            int userId,
            String passwordHash,
            String passwordSalt,
            String passwordAlgorithm,
            int passwordIterations,
            int passwordKeyLength) throws SQLException {

        String sql = """
                UPDATE users
                SET password_hash = ?,
                    password_salt = ?,
                    password_algorithm = ?,
                    password_iterations = ?,
                    password_key_length = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setString(2, passwordSalt);
            statement.setString(3, passwordAlgorithm);
            statement.setInt(4, passwordIterations);
            statement.setInt(5, passwordKeyLength);
            statement.setInt(6, userId);

            return statement.executeUpdate() == 1;
        }
    }
}
