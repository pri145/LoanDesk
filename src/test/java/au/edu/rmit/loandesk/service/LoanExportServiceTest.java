package au.edu.rmit.loandesk.service;

import au.edu.rmit.loandesk.model.Loan;
import au.edu.rmit.loandesk.model.User;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanExportServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    private final LoanExportService exporter = new LoanExportService(
            Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC),
                    ZoneOffset.UTC));

    private User user() {
        return new User(7, "priya_1", "Priya", "Parekh");
    }

    @Test
    void emptyListExportsHeaderAndNoRecordsMessage() {
        String text = exporter.generateExportContents(user(), List.of());

        assertTrue(text.contains("Username: priya_1"));
        assertTrue(text.contains("Full name: Priya Parekh"));
        assertTrue(text.contains("Export date: 2026-10-12"));
        assertTrue(text.contains("No loan records found."));
    }

    @Test
    void exportListsAllFieldsInGivenOrderWithFixedDateStatus() {
        Loan overdue = new Loan(2, 7, "A2", "Saw", "Kim", "Outdoor",
                TODAY.minusDays(10), TODAY.minusDays(1), null, "");
        Loan returned = new Loan(1, 7, "A1", "Drill", "Sam", "Tools",
                TODAY.minusDays(20), TODAY.minusDays(5),
                TODAY.minusDays(2), "Handle with care");

        String text = exporter.generateExportContents(
                user(), List.of(overdue, returned));

        assertTrue(text.contains("Status: Overdue"));
        assertTrue(text.contains("Status: Returned"));
        assertTrue(text.contains("Note: Handle with care"));
        assertTrue(text.contains("Return date: -"));
        assertTrue(text.indexOf("Loan ID: 2") < text.indexOf("Loan ID: 1"));
        assertFalse(text.toLowerCase().contains("password"));
    }

    @Test
    void exportRejectsMissingUserOrList() {
        assertThrows(IllegalArgumentException.class,
                () -> exporter.generateExportContents(null, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> exporter.generateExportContents(user(), null));
    }
}
