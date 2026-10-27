package au.edu.rmit.loandesk.controller;

import au.edu.rmit.loandesk.model.Loan;
import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.LoanService;
import au.edu.rmit.loandesk.view.LoanFormView;
import au.edu.rmit.loandesk.view.LoanManagementView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LoanManagementController {

    private final Stage stage;
    private final User user;
    private final LoanService loanService;
    private final LoanManagementView view;
    private final Runnable onBack;

    private List<Loan> allLoans = List.of();

    public LoanManagementController(
            Stage stage,
            User user,
            LoanService loanService,
            Runnable onBack) {

        this.stage = stage;
        this.user = user;
        this.loanService = loanService;
        this.view = new LoanManagementView();
        this.onBack = onBack;

        connectActions();
        refreshLoans();
    }

    private void connectActions() {
        view.getAddButton().setOnAction(
                event -> openLoanForm(null));

        view.getEditButton().setOnAction(event -> {
            Loan selectedLoan = requireSelectedLoan();

            if (selectedLoan == null) {
                return;
            }

            if (selectedLoan.isReturned()) {
                showMessage("Returned loans cannot be edited.");
                return;
            }

            openLoanForm(selectedLoan);
        });

        view.getReturnButton().setOnAction(
                event -> markSelectedLoanReturned());

        view.getDeleteButton().setOnAction(
                event -> deleteSelectedLoan());

        view.getRefreshButton().setOnAction(
                event -> refreshLoans());

        view.getBackButton().setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        view.getSearchField().textProperty().addListener(
                (observable, oldValue, newValue) -> applyFilters());

        view.getStatusFilter().valueProperty().addListener(
                (observable, oldValue, newValue) -> applyFilters());
    }

    public void show() {
        view.show(stage);
    }

    /**
     * Reloads the logged-in user's loans from persistent storage.
     */
    private void refreshLoans() {
        try {
            allLoans = loanService.getLoansForUser(user.getId());
            applyFilters();
            view.showMessage("");

        } catch (SQLException | IllegalArgumentException exception) {
            showMessage("Could not load loans. Please try again.");
            exception.printStackTrace();
        }
    }

    /**
     * Filters the cached loan records without querying the database.
     */
    private void applyFilters() {
        String searchText = view.getSearchField().getText();

        if (searchText == null) {
            searchText = "";
        }

        String search = searchText.trim().toLowerCase(Locale.ROOT);
        String selectedStatus = view.getStatusFilter().getValue();

        List<Loan> filteredLoans = allLoans.stream()
                .filter(loan -> matchesSearch(loan, search))
                .filter(loan -> matchesStatus(loan, selectedStatus))
                .toList();

        view.setLoans(filteredLoans);
    }

    private boolean matchesSearch(Loan loan, String search) {
        if (search.isEmpty()) {
            return true;
        }

        return containsIgnoreCase(loan.getItemCode(), search)
                || containsIgnoreCase(loan.getItemName(), search)
                || containsIgnoreCase(loan.getBorrowerName(), search)
                || containsIgnoreCase(loan.getCategory(), search)
                || containsIgnoreCase(loan.getNote(), search)
                || String.valueOf(loan.getId()).contains(search);
    }

    private boolean matchesStatus(Loan loan, String selectedStatus) {
        return selectedStatus == null
                || selectedStatus.equals(LoanManagementView.ALL_STATUSES)
                || selectedStatus.equals(loan.getStatus());
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private void openLoanForm(Loan existingLoan) {
        LoanFormView form = new LoanFormView(stage, existingLoan);

        form.getSaveButton().setOnAction(event -> {
            try {
                if (existingLoan == null) {
                    loanService.createLoan(
                            user.getId(),
                            form.getItemCode(),
                            form.getItemName(),
                            form.getBorrowerName(),
                            form.getCategory(),
                            form.getLoanDate(),
                            form.getDueDate(),
                            form.getNote());
                } else {
                    loanService.updateLoan(
                            existingLoan.getId(),
                            user.getId(),
                            form.getItemCode(),
                            form.getItemName(),
                            form.getBorrowerName(),
                            form.getCategory(),
                            form.getLoanDate(),
                            form.getDueDate(),
                            null,
                            form.getNote());
                }

                form.close();
                refreshLoans();

                showMessage(existingLoan == null
                        ? "Loan added successfully."
                        : "Loan updated successfully.");

            } catch (SQLException | IllegalArgumentException exception) {
                form.showMessage(exception.getMessage());
            }
        });

        form.getCancelButton().setOnAction(event -> form.close());
        form.showAndWait();
    }

    private void markSelectedLoanReturned() {
        Loan selectedLoan = requireSelectedLoan();

        if (selectedLoan == null) {
            return;
        }

        if (selectedLoan.isReturned()) {
            showMessage("This loan has already been returned.");
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Mark item \"" + selectedLoan.getItemName()
                        + "\" as returned today?",
                ButtonType.YES,
                ButtonType.NO);

        confirmation.setTitle("Confirm Return");
        confirmation.setHeaderText("Mark Loan Returned");
        confirmation.initOwner(stage);

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            loanService.markLoanReturned(
                    selectedLoan.getId(),
                    user.getId(),
                    loanService.today());

            refreshLoans();
            showMessage("Loan marked as returned.");

        } catch (SQLException | IllegalArgumentException exception) {
            showMessage("Could not mark loan as returned. Please try again.");
            exception.printStackTrace();
        }
    }

    private void deleteSelectedLoan() {
        Loan selectedLoan = requireSelectedLoan();

        if (selectedLoan == null) {
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete loan record for \"" + selectedLoan.getItemName()
                        + "\"? This action cannot be undone.",
                ButtonType.YES,
                ButtonType.NO);

        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Loan");
        confirmation.initOwner(stage);

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            loanService.deleteLoan(selectedLoan.getId(), user.getId());

            refreshLoans();
            showMessage("Loan record deleted.");

        } catch (SQLException | IllegalArgumentException exception) {
            showMessage("Could not delete the loan record. Please try again.");
            exception.printStackTrace();
        }
    }

    private Loan requireSelectedLoan() {
        Loan selectedLoan = view.getSelectedLoan();

        if (selectedLoan == null) {
            showMessage("Select a loan record first.");
        }

        return selectedLoan;
    }

    private void showMessage(String message) {
        view.showMessage(message);
    }
}
