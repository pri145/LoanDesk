package au.edu.rmit.loandesk.dao;

import au.edu.rmit.loandesk.model.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link LoanRepository} backed by SQLite.
 * All values are bound through {@code PreparedStatement} parameters.
 */
public class LoanDao implements LoanRepository {

    @Override
    public Loan createLoan(Loan loan) throws SQLException {
        String sql = """
                INSERT INTO loans (
                    user_id, item_code, item_name, borrower_name,
                    category, loan_date, due_date, return_date, note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, loan.getUserId());
            statement.setString(2, loan.getItemCode());
            statement.setString(3, loan.getItemName());
            statement.setString(4, loan.getBorrowerName());
            statement.setString(5, loan.getCategory());
            statement.setString(6, loan.getLoanDate().toString());
            statement.setString(7, loan.getDueDate().toString());

            if (loan.getReturnDate() == null) {
                statement.setNull(8, java.sql.Types.VARCHAR);
            } else {
                statement.setString(8, loan.getReturnDate().toString());
            }

            statement.setString(9, loan.getNote());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Loan(
                            keys.getInt(1),
                            loan.getUserId(),
                            loan.getItemCode(),
                            loan.getItemName(),
                            loan.getBorrowerName(),
                            loan.getCategory(),
                            loan.getLoanDate(),
                            loan.getDueDate(),
                            loan.getReturnDate(),
                            loan.getNote());
                }
            }
        }

        throw new SQLException("The database did not return a loan ID.");
    }

    @Override
    public List<Loan> findLoansByUserId(int userId) throws SQLException {
        String sql = """
                SELECT id, user_id, item_code, item_name, borrower_name,
                       category, loan_date, due_date, return_date, note
                FROM loans
                WHERE user_id = ?
                ORDER BY loan_date DESC, id DESC
                """;

        List<Loan> loans = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    loans.add(mapLoan(resultSet));
                }
            }
        }

        return loans;
    }

    @Override
    public Optional<Loan> findById(int loanId, int userId)
            throws SQLException {

        String sql = """
                SELECT id, user_id, item_code, item_name, borrower_name,
                       category, loan_date, due_date, return_date, note
                FROM loans
                WHERE id = ? AND user_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, loanId);
            statement.setInt(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapLoan(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean updateLoan(Loan loan) throws SQLException {
        String sql = """
                UPDATE loans
                SET item_code = ?,
                    item_name = ?,
                    borrower_name = ?,
                    category = ?,
                    due_date = ?,
                    note = ?
                WHERE id = ?
                  AND user_id = ?
                  AND return_date IS NULL
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, loan.getItemCode());
            statement.setString(2, loan.getItemName());
            statement.setString(3, loan.getBorrowerName());
            statement.setString(4, loan.getCategory());
            statement.setString(5, loan.getDueDate().toString());
            statement.setString(6, loan.getNote());
            statement.setInt(7, loan.getId());
            statement.setInt(8, loan.getUserId());

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean markReturned(
            int loanId,
            int userId,
            LocalDate returnDate) throws SQLException {

        String sql = """
                UPDATE loans
                SET return_date = ?
                WHERE id = ?
                  AND user_id = ?
                  AND return_date IS NULL
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, returnDate.toString());
            statement.setInt(2, loanId);
            statement.setInt(3, userId);

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteLoan(int loanId, int userId) throws SQLException {
        String sql = """
                DELETE FROM loans
                WHERE id = ? AND user_id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, loanId);
            statement.setInt(2, userId);

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean hasActiveLoanWithItemCode(
            int userId,
            String itemCode) throws SQLException {

        String sql = """
                SELECT 1
                FROM loans
                WHERE user_id = ?
                  AND item_code = ? COLLATE NOCASE
                  AND return_date IS NULL
                LIMIT 1
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, itemCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Loan mapLoan(ResultSet resultSet) throws SQLException {
        String loanDateValue = resultSet.getString("loan_date");
        String dueDateValue = resultSet.getString("due_date");
        String returnDateValue = resultSet.getString("return_date");

        LocalDate loanDate = loanDateValue == null
                ? null : LocalDate.parse(loanDateValue);

        LocalDate dueDate = dueDateValue == null
                ? null : LocalDate.parse(dueDateValue);

        LocalDate returnDate = returnDateValue == null
                ? null : LocalDate.parse(returnDateValue);

        return new Loan(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("item_code"),
                resultSet.getString("item_name"),
                resultSet.getString("borrower_name"),
                resultSet.getString("category"),
                loanDate,
                dueDate,
                returnDate,
                resultSet.getString("note"));
    }
}
