package au.edu.rmit.loandesk.service;

import au.edu.rmit.loandesk.dao.LoanDao;
import au.edu.rmit.loandesk.dao.LoanRepository;
import au.edu.rmit.loandesk.model.Loan;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanService {

    private static final List<String> VALID_CATEGORIES =
            List.of("Tools", "Electronics", "Outdoor", "Other");

    private final LoanRepository loanRepository;
    private final Clock clock;

    public LoanService() {
        this(new LoanDao(), Clock.systemDefaultZone());
    }

    /**
     * @param loanRepository where loans are stored (a JDBC DAO in the app,
     *                       an in-memory fake in tests)
     * @param clock          source of "today", so that tests can fix the date
     */
    public LoanService(LoanRepository loanRepository, Clock clock) {
        if (loanRepository == null || clock == null) {
            throw new IllegalArgumentException(
                    "Repository and clock cannot be null.");
        }

        this.loanRepository = loanRepository;
        this.clock = clock;
    }

    /** Today's date according to this service's clock. */
    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public Loan createLoan(
            int userId,
            String itemCode,
            String itemName,
            String borrowerName,
            String category,
            LocalDate loanDate,
            LocalDate dueDate,
            String note) throws SQLException {

        validateUserId(userId);

        // New loans must always be recorded with today's date.
        LocalDate actualLoanDate = today();

        validateLoanDetails(
                itemCode, itemName, borrowerName,
                category, actualLoanDate, dueDate, note);

        String cleanItemCode = normaliseItemCode(itemCode);

        if (loanRepository.hasActiveLoanWithItemCode(
                userId, cleanItemCode)) {
            throw new IllegalArgumentException(
                    "An active loan already exists for this item code.");
        }

        Loan loan = new Loan(
                0,
                userId,
                cleanItemCode,
                itemName.trim(),
                borrowerName.trim(),
                category.trim(),
                actualLoanDate,
                dueDate,
                null,
                cleanNote(note)
        );

        return loanRepository.createLoan(loan);
    }

    public List<Loan> getLoansForUser(int userId)
            throws SQLException {
        validateUserId(userId);
        return loanRepository.findLoansByUserId(userId);
    }

    public Optional<Loan> getLoanById(int loanId, int userId)
            throws SQLException {
        validateLoanId(loanId);
        validateUserId(userId);
        return loanRepository.findById(loanId, userId);
    }

    public Loan updateLoan(
            int loanId,
            int userId,
            String itemCode,
            String itemName,
            String borrowerName,
            String category,
            LocalDate loanDate,
            LocalDate dueDate,
            LocalDate returnDate,
            String note) throws SQLException {

        validateLoanId(loanId);
        validateUserId(userId);

        Loan existingLoan = loanRepository.findById(loanId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Loan not found for this user."));

        if (existingLoan.isReturned()) {
            throw new IllegalArgumentException(
                    "Returned loans cannot be edited.");
        }

        // The original loan date and return date are not editable.
        if (loanDate != null
                && !existingLoan.getLoanDate().equals(loanDate)) {
            throw new IllegalArgumentException(
                    "The original loan date cannot be changed.");
        }

        if (returnDate != null) {
            throw new IllegalArgumentException(
                    "Use Mark Returned to record a return.");
        }

        validateLoanDetails(
                itemCode, itemName, borrowerName,
                category, existingLoan.getLoanDate(), dueDate, note);

        String cleanItemCode = normaliseItemCode(itemCode);

        if (loanRepository.hasActiveLoanWithItemCode(userId, cleanItemCode)
                && !existingLoan.getItemCode()
                        .equalsIgnoreCase(cleanItemCode)) {
            throw new IllegalArgumentException(
                    "An active loan already exists for this item code.");
        }

        existingLoan.setItemCode(cleanItemCode);
        existingLoan.setItemName(itemName.trim());
        existingLoan.setBorrowerName(borrowerName.trim());
        existingLoan.setCategory(category.trim());
        existingLoan.setDueDate(dueDate);
        existingLoan.setNote(cleanNote(note));

        if (!loanRepository.updateLoan(existingLoan)) {
            throw new IllegalArgumentException(
                    "The loan could not be updated.");
        }

        return existingLoan;
    }

    public void markLoanReturned(
            int loanId, int userId, LocalDate returnDate)
            throws SQLException {

        validateLoanId(loanId);
        validateUserId(userId);

        Loan loan = loanRepository.findById(loanId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Loan not found for this user."));

        if (loan.isReturned()) {
            throw new IllegalArgumentException(
                    "This loan has already been returned.");
        }

        // The application, not the user, determines the return date.
        LocalDate actualReturnDate = today();

        if (returnDate != null
                && !today().equals(returnDate)) {
            throw new IllegalArgumentException(
                    "The return date must be today's date.");
        }

        if (!loanRepository.markReturned(
                loanId, userId, actualReturnDate)) {
            throw new IllegalArgumentException(
                    "The loan could not be marked as returned.");
        }
    }

    public void deleteLoan(int loanId, int userId)
            throws SQLException {

        validateLoanId(loanId);
        validateUserId(userId);

        if (!loanRepository.deleteLoan(loanId, userId)) {
            throw new IllegalArgumentException(
                    "Loan not found for this user.");
        }
    }

    private void validateLoanDetails(
            String itemCode,
            String itemName,
            String borrowerName,
            String category,
            LocalDate loanDate,
            LocalDate dueDate,
            String note) {

        String cleanCode = requireText(
                itemCode, "Item code", 20);

        if (!cleanCode.matches("[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException(
                    "Item code may contain only English letters, "
                    + "digits and hyphens.");
        }

        requireText(itemName, "Item name", 80);
        requireText(borrowerName, "Borrower name", 60);

        if (category == null
                || !VALID_CATEGORIES.contains(category.trim())) {
            throw new IllegalArgumentException(
                    "Select a valid category.");
        }

        if (loanDate == null) {
            throw new IllegalArgumentException(
                    "Loan date is required.");
        }

        if (dueDate == null) {
            throw new IllegalArgumentException(
                    "Due date is required.");
        }

        if (dueDate.isBefore(loanDate)) {
            throw new IllegalArgumentException(
                    "Due date cannot be before the loan date.");
        }

        if (note != null && note.length() > 250) {
            throw new IllegalArgumentException(
                    "Note cannot exceed 250 characters.");
        }
    }

    private String requireText(
            String value, String fieldName, int maxLength) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is required.");
        }

        String trimmed = value.trim();

        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " cannot exceed "
                            + maxLength + " characters.");
        }

        return trimmed;
    }

    private String normaliseItemCode(String itemCode) {
        return itemCode.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private String cleanNote(String note) {
        return note == null ? "" : note;
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID.");
        }
    }

    private void validateLoanId(int loanId) {
        if (loanId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid loan ID.");
        }
    }
}
