package au.edu.rmit.loandesk.controller;

import au.edu.rmit.loandesk.model.Loan;
import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.LoanExportService;
import au.edu.rmit.loandesk.service.LoanService;
import au.edu.rmit.loandesk.view.DashboardView;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    private final Stage stage;
    private final User loggedInUser;
    private final Runnable onLogout;
    private final Runnable onEditProfile;
    private final DashboardView dashboardView;

    private final LoanService loanService = new LoanService();
    private final LoanExportService loanExportService =
            new LoanExportService();

    public DashboardController(
            Stage stage,
            User loggedInUser,
            Runnable onLogout,
            Runnable onEditProfile) {

        this.stage = stage;
        this.loggedInUser = loggedInUser;
        this.onLogout = onLogout;
        this.onEditProfile = onEditProfile;
        this.dashboardView = new DashboardView(loggedInUser);

        configureActions();
    }

    private void configureActions() {
        dashboardView.getLogoutButton().setOnAction(
                event -> onLogout.run());

        dashboardView.getEditProfileButton().setOnAction(
                event -> onEditProfile.run());

        dashboardView.getManageLoansButton().setOnAction(event -> {
            LoanManagementController loanController =
                    new LoanManagementController(
                            stage,
                            loggedInUser,
                            loanService,
                            this::show
                    );

            loanController.show();
        });

        dashboardView.getExportLoansButton().setOnAction(
                event -> exportLoans());
    }

    private void exportLoans() {
        // Fetch all loans directly from the service, not the filtered table.
        final List<Loan> loans;

        try {
            loans = loanService.getLoansForUser(loggedInUser.getId());
        } catch (SQLException | IllegalArgumentException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Export Failed",
                    "Could not retrieve loan records.",
                    exception.getMessage());
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Loan Register");
        fileChooser.setInitialFileName("LoanDesk_Export.txt");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Text Files (*.txt)", "*.txt"));

        Path selectedPath;

        try {
            java.io.File selectedFile =
                    fileChooser.showSaveDialog(stage);

            // Cancelling the dialog must not change anything.
            if (selectedFile == null) {
                return;
            }

            selectedPath = selectedFile.toPath();

            // Ensure the chosen file has the required .txt extension.
            if (!selectedPath.getFileName().toString()
                    .toLowerCase(java.util.Locale.ROOT)
                    .endsWith(".txt")) {
                selectedPath = selectedPath.resolveSibling(
                        selectedPath.getFileName() + ".txt");
            }

            String contents = loanExportService.generateExportContents(
                    loggedInUser, loans);

            Files.writeString(
                    selectedPath,
                    contents,
                    StandardCharsets.UTF_8);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Export Successful",
                    "Your loan register has been exported.",
                    "File saved to:\n" + selectedPath.toAbsolutePath());

        } catch (IOException | SecurityException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Export Failed",
                    "The loan register could not be saved.",
                    exception.getMessage() == null
                            ? "An unexpected file error occurred."
                            : exception.getMessage());
        }
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String message) {

        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        dashboardView.show(stage);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
