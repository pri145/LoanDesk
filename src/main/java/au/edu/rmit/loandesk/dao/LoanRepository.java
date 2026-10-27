package au.edu.rmit.loandesk.dao;

import au.edu.rmit.loandesk.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for loan persistence (DAO / Repository pattern).
 *
 * <p>{@link au.edu.rmit.loandesk.service.LoanService} depends on this
 * interface rather than on JDBC, so the business rules can be tested with an
 * in-memory implementation and the storage technology can change without
 * touching the service or the GUI. Every operation that reads or changes an
 * existing loan takes the owning user's ID, so ownership is enforced at the
 * persistence layer and not only in the GUI.
 */
public interface LoanRepository {

    /** Stores a new loan and returns it with its generated ID. */
    Loan createLoan(Loan loan) throws SQLException;

    /** Returns the user's loans, newest loan date first, then highest ID. */
    List<Loan> findLoansByUserId(int userId) throws SQLException;

    /** Finds a loan only if it belongs to the given user. */
    Optional<Loan> findById(int loanId, int userId) throws SQLException;

    /** Updates an unreturned loan owned by the user; false if none changed. */
    boolean updateLoan(Loan loan) throws SQLException;

    /** Records the return date of an unreturned loan owned by the user. */
    boolean markReturned(int loanId, int userId, LocalDate returnDate)
            throws SQLException;

    /** Deletes a loan owned by the user; false if none was deleted. */
    boolean deleteLoan(int loanId, int userId) throws SQLException;

    /** True if the user already has an unreturned loan with this code. */
    boolean hasActiveLoanWithItemCode(int userId, String itemCode)
            throws SQLException;
}
