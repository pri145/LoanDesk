package au.edu.rmit.loandesk.service;

import au.edu.rmit.loandesk.dao.InMemoryLoanRepository;
import au.edu.rmit.loandesk.model.Loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Business-rule tests for {@link LoanService}. A fixed clock and an
 * in-memory repository are used, so the tests do not depend on the day they
 * run and never alter the application's database.
 */
class LoanServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);
    private static final int ALICE = 1;
    private static final int BOB = 2;

    private LoanService service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                TODAY.atStartOfDay().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        service = new LoanService(new InMemoryLoanRepository(), fixedClock);
    }

    private Loan addLoan(int userId, String code, LocalDate dueDate)
            throws SQLException {
        return service.createLoan(userId, code, "Cordless drill",
                "Sam Lee", "Tools", null, dueDate, "");
    }

    @Test
    void createLoanUsesTodayAndNormalisesItemCode() throws SQLException {
        Loan loan = service.createLoan(ALICE, " drill-01 ", "Drill",
                "Sam", "Tools", LocalDate.of(1999, 1, 1),
                TODAY.plusDays(7), null);

        assertEquals("DRILL-01", loan.getItemCode());
        assertEquals(TODAY, loan.getLoanDate());
        assertNull(loan.getReturnDate());
        assertTrue(loan.getId() > 0);
    }

    @Test
    void createLoanRejectsDueDateBeforeLoanDate() {
        assertThrows(IllegalArgumentException.class,
                () -> addLoan(ALICE, "A1", TODAY.minusDays(1)));
    }

    @Test
    void createLoanAcceptsDueDateEqualToLoanDate() throws SQLException {
        assertEquals(TODAY, addLoan(ALICE, "A1", TODAY).getDueDate());
    }

    @Test
    void createLoanRejectsInvalidItemCodeAndCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> addLoan(ALICE, "bad code!", TODAY));
        assertThrows(IllegalArgumentException.class,
                () -> service.createLoan(ALICE, "A1", "Drill", "Sam",
                        "Furniture", null, TODAY, ""));
    }

    @Test
    void createLoanRejectsNoteOverTwoHundredFiftyCharacters() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createLoan(ALICE, "A1", "Drill", "Sam",
                        "Tools", null, TODAY, "x".repeat(251)));
    }

    @Test
    void duplicateActiveItemCodeIsRejectedIgnoringCase()
            throws SQLException {
        addLoan(ALICE, "DRILL-01", TODAY);

        assertThrows(IllegalArgumentException.class,
                () -> addLoan(ALICE, "drill-01", TODAY));
    }

    @Test
    void sameItemCodeAllowedForOtherUserOrAfterReturn()
            throws SQLException {
        Loan first = addLoan(ALICE, "DRILL-01", TODAY);
        assertDoesNotThrow(() -> addLoan(BOB, "DRILL-01", TODAY));

        service.markLoanReturned(first.getId(), ALICE, TODAY);
        assertDoesNotThrow(() -> addLoan(ALICE, "DRILL-01", TODAY));
    }

    @Test
    void markReturnedRecordsTodayAndCannotRepeat() throws SQLException {
        Loan loan = addLoan(ALICE, "A1", TODAY.plusDays(3));

        service.markLoanReturned(loan.getId(), ALICE, TODAY);

        Loan stored = service.getLoanById(loan.getId(), ALICE).orElseThrow();
        assertEquals(TODAY, stored.getReturnDate());
        assertEquals(Loan.STATUS_RETURNED, stored.getStatus(TODAY));
        assertThrows(IllegalArgumentException.class,
                () -> service.markLoanReturned(
                        loan.getId(), ALICE, TODAY));
    }

    @Test
    void returnedLoanCannotBeEdited() throws SQLException {
        Loan loan = addLoan(ALICE, "A1", TODAY);
        service.markLoanReturned(loan.getId(), ALICE, TODAY);

        assertThrows(IllegalArgumentException.class,
                () -> service.updateLoan(loan.getId(), ALICE, "A1",
                        "Drill", "Sam", "Tools", null, TODAY,
                        null, ""));
    }

    @Test
    void updateChangesEditableFieldsButNotLoanDate() throws SQLException {
        Loan loan = addLoan(ALICE, "A1", TODAY);

        Loan updated = service.updateLoan(loan.getId(), ALICE, "a2",
                "Saw", "Kim", "Outdoor", null, TODAY.plusDays(5),
                null, "new note");

        assertEquals("A2", updated.getItemCode());
        assertEquals("Kim", updated.getBorrowerName());
        assertEquals(TODAY.plusDays(5), updated.getDueDate());
        assertEquals(TODAY, updated.getLoanDate());

        assertThrows(IllegalArgumentException.class,
                () -> service.updateLoan(loan.getId(), ALICE, "A2",
                        "Saw", "Kim", "Outdoor", TODAY.minusDays(3),
                        TODAY, null, ""));
    }

    @Test
    void userCannotViewEditReturnOrDeleteAnotherUsersLoan()
            throws SQLException {
        Loan loan = addLoan(ALICE, "A1", TODAY);
        int id = loan.getId();

        assertTrue(service.getLoanById(id, BOB).isEmpty());
        assertTrue(service.getLoansForUser(BOB).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> service.updateLoan(id, BOB, "A1", "Drill", "Sam",
                        "Tools", null, TODAY, null, ""));
        assertThrows(IllegalArgumentException.class,
                () -> service.markLoanReturned(id, BOB, TODAY));
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteLoan(id, BOB));

        assertTrue(service.getLoanById(id, ALICE).isPresent());
    }

    @Test
    void deleteRemovesOwnLoan() throws SQLException {
        Loan loan = addLoan(ALICE, "A1", TODAY);

        service.deleteLoan(loan.getId(), ALICE);

        assertTrue(service.getLoansForUser(ALICE).isEmpty());
    }

    @Test
    void loansAreListedNewestFirstThenHighestIdFirst()
            throws SQLException {
        Loan first = addLoan(ALICE, "A1", TODAY);
        Loan second = addLoan(ALICE, "A2", TODAY);

        List<Loan> loans = service.getLoansForUser(ALICE);

        assertEquals(second.getId(), loans.get(0).getId());
        assertEquals(first.getId(), loans.get(1).getId());
    }

    @Test
    void invalidIdentifiersAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getLoansForUser(0));
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteLoan(-1, ALICE));
    }
}
