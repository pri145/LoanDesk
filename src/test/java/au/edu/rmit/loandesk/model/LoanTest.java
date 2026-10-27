package au.edu.rmit.loandesk.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Status rules, tested against fixed reference dates. */
class LoanTest {

    private static final LocalDate DUE = LocalDate.of(2026, 10, 12);

    private Loan loan(LocalDate returnDate) {
        return new Loan(1, 1, "A1", "Drill", "Sam", "Tools",
                DUE.minusDays(7), DUE, returnDate, "");
    }

    @Test
    void dueTodayIsStillOnLoan() {
        assertEquals("On loan", loan(null).getStatus(DUE));
    }

    @Test
    void dayAfterDueDateIsOverdue() {
        assertEquals("Overdue", loan(null).getStatus(DUE.plusDays(1)));
    }

    @Test
    void beforeDueDateIsOnLoan() {
        assertEquals("On loan", loan(null).getStatus(DUE.minusDays(3)));
    }

    @Test
    void returnedLoanIsReturnedEvenWhenLate() {
        assertEquals("Returned",
                loan(DUE.plusDays(5)).getStatus(DUE.plusDays(30)));
    }
}
