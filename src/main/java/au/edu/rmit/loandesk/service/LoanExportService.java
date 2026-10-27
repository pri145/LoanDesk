package au.edu.rmit.loandesk.service;

import au.edu.rmit.loandesk.model.Loan;
import au.edu.rmit.loandesk.model.User;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Builds the plain-text loan register. It contains no file or GUI code, so
 * it can be tested directly; the controller decides where the text is saved.
 */
public class LoanExportService {

    private final Clock clock;

    public LoanExportService() {
        this(Clock.systemDefaultZone());
    }

    public LoanExportService(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null.");
        }

        this.clock = clock;
    }

    public String generateExportContents(User user, List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);

        if (user == null) {
            throw new IllegalArgumentException(
                    "A logged-in user is required for export.");
        }

        if (loans == null) {
            throw new IllegalArgumentException(
                    "The loan list cannot be null.");
        }

        StringBuilder content = new StringBuilder();

        content.append("LOANDESK LOAN REGISTER\n");
        content.append("======================\n\n");

        content.append("Username: ")
                .append(user.getUsername())
                .append('\n');

        content.append("Full name: ")
                .append(user.getFirstName())
                .append(' ')
                .append(user.getLastName())
                .append('\n');

        content.append("Export date: ")
                .append(today)
                .append("\n\n");

        content.append("LOAN RECORDS\n");
        content.append("------------\n\n");

        if (loans.isEmpty()) {
            content.append("No loan records found.\n");
            return content.toString();
        }

        for (Loan loan : loans) {
            content.append("Loan ID: ")
                    .append(loan.getId())
                    .append('\n');

            content.append("Item code: ")
                    .append(orDash(loan.getItemCode()))
                    .append('\n');

            content.append("Item name: ")
                    .append(orDash(loan.getItemName()))
                    .append('\n');

            content.append("Borrower name: ")
                    .append(orDash(loan.getBorrowerName()))
                    .append('\n');

            content.append("Category: ")
                    .append(orDash(loan.getCategory()))
                    .append('\n');

            content.append("Loan date: ")
                    .append(orDash(loan.getLoanDate()))
                    .append('\n');

            content.append("Due date: ")
                    .append(orDash(loan.getDueDate()))
                    .append('\n');

            content.append("Return date: ")
                    .append(orDash(loan.getReturnDate()))
                    .append('\n');

            content.append("Status: ")
                    .append(loan.getStatus(today))
                    .append('\n');

            content.append("Note: ")
                    .append(orDash(loan.getNote()))
                    .append("\n\n");
        }

        return content.toString();
    }

    private String orDash(Object value) {
        if (value == null || value.toString().isBlank()) {
            return "-";
        }

        return value.toString();
    }
}
