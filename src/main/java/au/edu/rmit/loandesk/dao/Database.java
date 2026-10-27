
package au.edu.rmit.loandesk.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    /**
     * System property that overrides the database file location. It is used
     * by tests so that the application's working database is never touched.
     */
    public static final String PATH_PROPERTY = "loandesk.db.path";

    private static final Path DEFAULT_PATH = Path.of(
            System.getProperty("user.home"), ".loandesk", "loandesk.db");

    private Database() {
        // Prevent instantiation of this utility class.
    }

    /** Returns the database file in use (default: ~/.loandesk/loandesk.db). */
    public static Path getDatabasePath() {
        String override = System.getProperty(PATH_PROPERTY);

        if (override == null || override.isBlank()) {
            override = System.getenv("LOANDESK_DB_PATH");
        }

        return override == null || override.isBlank()
                ? DEFAULT_PATH
                : Path.of(override);
    }

    public static Connection getConnection() throws SQLException {
        Path databasePath = getDatabasePath().toAbsolutePath();

        try {
            Files.createDirectories(databasePath.getParent());
        } catch (IOException e) {
            throw new SQLException(
                    "Could not create the database directory.", e);
        }

        Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databasePath);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    public static void initializeDatabase() {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    password_hash TEXT NOT NULL,
                    password_salt TEXT NOT NULL,
                    password_algorithm TEXT NOT NULL,
                    password_iterations INTEGER NOT NULL,
                    password_key_length INTEGER NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createLoansTable = """
                CREATE TABLE IF NOT EXISTS loans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    item_code TEXT NOT NULL,
                    item_name TEXT NOT NULL,
                    borrower_name TEXT NOT NULL,
                    category TEXT NOT NULL
                        CHECK (category IN (
                            'Tools', 'Electronics', 'Outdoor', 'Other'
                        )),
                    loan_date TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    return_date TEXT,
                    note TEXT NOT NULL DEFAULT '',
                    FOREIGN KEY (user_id) REFERENCES users(id)
                        ON DELETE CASCADE
                )
                """;

        String normaliseExistingItemCodes = """
                UPDATE loans
                SET item_code = UPPER(TRIM(item_code))
                """;

        String dropOldActiveLoanIndex = """
                DROP INDEX IF EXISTS idx_loans_unique_active_item_code
                """;

        String createActiveLoanIndex = """
                CREATE UNIQUE INDEX idx_loans_unique_active_item_code
                ON loans(user_id, item_code COLLATE NOCASE)
                WHERE return_date IS NULL
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createUsersTable);
            statement.execute(createLoansTable);

            // Normalise codes already stored in the database.
            statement.executeUpdate(normaliseExistingItemCodes);

            // Rebuild the index with case-insensitive comparison.
            statement.execute(dropOldActiveLoanIndex);
            statement.execute(createActiveLoanIndex);

            System.out.println("Database initialized successfully.");
            System.out.println("Database location: " + getDatabasePath().toAbsolutePath());

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to initialize the database.", e);
        }
    }
}
