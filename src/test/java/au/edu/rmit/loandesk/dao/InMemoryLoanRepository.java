package au.edu.rmit.loandesk.dao;

import au.edu.rmit.loandesk.model.Loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Test double for {@link LoanRepository}. It mimics the rules of
 * {@link LoanDao} (ownership checks, no edits to returned loans, newest-first
 * ordering) without a database, so tests never touch the working database.
 */
public class InMemoryLoanRepository implements LoanRepository {

    private final List<Loan> loans = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Loan createLoan(Loan loan) {
        Loan saved = new Loan(
                nextId++, loan.getUserId(), loan.getItemCode(),
                loan.getItemName(), loan.getBorrowerName(),
                loan.getCategory(), loan.getLoanDate(), loan.getDueDate(),
                loan.getReturnDate(), loan.getNote());
        loans.add(saved);
        return saved;
    }

    @Override
    public List<Loan> findLoansByUserId(int userId) {
        return loans.stream()
                .filter(loan -> loan.getUserId() == userId)
                .sorted(Comparator.comparing(Loan::getLoanDate).reversed()
                        .thenComparing(Comparator.comparingInt(
                                Loan::getId).reversed()))
                .toList();
    }

    @Override
    public Optional<Loan> findById(int loanId, int userId) {
        return loans.stream()
                .filter(loan -> loan.getId() == loanId
                        && loan.getUserId() == userId)
                .findFirst();
    }

    @Override
    public boolean updateLoan(Loan loan) {
        return findById(loan.getId(), loan.getUserId())
                .filter(stored -> !stored.isReturned())
                .isPresent();
    }

    @Override
    public boolean markReturned(
            int loanId, int userId, LocalDate returnDate) {

        Optional<Loan> loan = findById(loanId, userId)
                .filter(stored -> !stored.isReturned());

        loan.ifPresent(stored -> stored.setReturnDate(returnDate));
        return loan.isPresent();
    }

    @Override
    public boolean deleteLoan(int loanId, int userId) {
        return loans.removeIf(loan -> loan.getId() == loanId
                && loan.getUserId() == userId);
    }

    @Override
    public boolean hasActiveLoanWithItemCode(int userId, String itemCode) {
        return loans.stream().anyMatch(loan ->
                loan.getUserId() == userId
                        && !loan.isReturned()
                        && loan.getItemCode().equalsIgnoreCase(itemCode));
    }
}
