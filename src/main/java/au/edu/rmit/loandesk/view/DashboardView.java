
package au.edu.rmit.loandesk.view;

import au.edu.rmit.loandesk.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView {

    private final Scene scene;
    private final MainLayout mainLayout;

    private final Label welcomeLabel;
    private final Button manageLoansButton;
    private final Button editProfileButton;
    private final Button exportLoansButton;
    private final Button logoutButton;

    public DashboardView(User user) {

        Label sectionLabel = new Label("DASHBOARD");
        sectionLabel.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #718096;"
        );

        welcomeLabel = new Label();
        welcomeLabel.setStyle(
                "-fx-font-size: 30px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #1f2937;"
        );
        updateWelcomeMessage(user);

        Label subtitleLabel = new Label(
                "Manage your loans and keep track of borrowed items."
        );
        subtitleLabel.setWrapText(true);
        subtitleLabel.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-text-fill: #64748b;"
        );

        VBox welcomeSection = new VBox(
                10, sectionLabel, welcomeLabel, subtitleLabel
        );
        welcomeSection.setAlignment(Pos.CENTER_LEFT);
        welcomeSection.setPadding(new Insets(0, 0, 8, 0));

        // Manage Loans card
        Label manageTitle = createCardTitle("Manage Loans");
        Label manageDescription = createCardDescription(
                "Create, view, update, and manage your loan records."
        );

        manageLoansButton = createPrimaryButton("Manage Loans  >");

        VBox manageCard = createCard(
                manageTitle, manageDescription, manageLoansButton
        );

        // Export Loans card
        Label exportTitle = createCardTitle("Export Loans");
        Label exportDescription = createCardDescription(
                "Export loan records to a text file for easy reference."
        );

        exportLoansButton = createPrimaryButton("Export Loans  >");

        VBox exportCard = createCard(
                exportTitle, exportDescription, exportLoansButton
        );

        HBox cards = new HBox(24, manageCard, exportCard);
        cards.setAlignment(Pos.TOP_LEFT);

        HBox.setHgrow(manageCard, Priority.ALWAYS);
        HBox.setHgrow(exportCard, Priority.ALWAYS);

        manageCard.setMaxWidth(Double.MAX_VALUE);
        exportCard.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(28, welcomeSection, cards);
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout = new MainLayout(
                "Dashboard",
                "Manage your account and loan records.",
                content
        );

        editProfileButton = mainLayout.getProfileButton();
        logoutButton = mainLayout.getLogoutButton();

        scene = new Scene(
                mainLayout.getRoot(), 1000, 650
        );
    }

    private Label createCardTitle(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #1f2937;"
        );
        return label;
    }

    private Label createCardDescription(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinHeight(48);
        label.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-text-fill: #64748b;"
        );
        return label;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
                "-fx-background-color: #2457a7;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12;"
                + "-fx-background-radius: 7;"
        );
        return button;
    }

    private VBox createCard(
            Label title,
            Label description,
            Button actionButton) {

        VBox card = new VBox(
                18, title, description, actionButton
        );

        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(28));
        card.setPrefWidth(380);
        card.setMinWidth(250);
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #e3e8f0;"
                + "-fx-border-radius: 12;"
        );

        return card;
    }

    public void updateWelcomeMessage(User user) {
        welcomeLabel.setText(
                "Welcome, " + user.getFullName() + "!"
        );
    }

    public void show(Stage stage) {
        stage.setTitle("LoanDesk - Dashboard");
        stage.setScene(scene);
        WindowSettings.apply(stage);
        stage.show();
    }

    public Button getManageLoansButton() {
        return manageLoansButton;
    }

    public Button getEditProfileButton() {
        return editProfileButton;
    }

    public Button getExportLoansButton() {
        return exportLoansButton;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }
}
