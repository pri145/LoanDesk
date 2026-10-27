package au.edu.rmit.loandesk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainLayout {

    private final BorderPane root;
    private final Label pageTitle;
    private final Label pageDescription;
    private final VBox pageContent;

    private final Button profileButton;
    private final Button logoutButton;

    public MainLayout(
            String title,
            String description,
            Node content) {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6fa;");

        // Header branding
        Label logoLabel = new Label("LoanDesk");
        logoLabel.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #2457a7;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        profileButton = new Button("Profile");
        profileButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #2457a7;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: #d9e2f0;"
                + "-fx-border-radius: 7;"
        );

        logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: #2457a7;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 7;"
        );

        HBox header = new HBox(
                18,
                logoLabel,
                spacer,
                profileButton,
                logoutButton
        );

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 36, 18, 36));
        header.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: transparent transparent #e3e8f0 transparent;"
                + "-fx-border-width: 0 0 1 0;"
        );

        root.setTop(header);

        // Page heading
        pageTitle = new Label(title);
        pageTitle.setStyle(
                "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #1f2937;"
        );

        pageDescription = new Label(description);
        pageDescription.setWrapText(true);
        pageDescription.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-text-fill: #64748b;"
        );

        pageContent = new VBox(24);
        pageContent.setAlignment(Pos.TOP_LEFT);
        pageContent.setPadding(new Insets(40, 48, 40, 48));

        pageContent.getChildren().addAll(
                pageTitle,
                pageDescription,
                content
        );

        root.setCenter(pageContent);

        // Footer
        Label footerLabel = new Label(
                "LoanDesk  |  Personal Loan Management"
        );
        footerLabel.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #94a3b8;"
        );

        HBox footer = new HBox(footerLabel);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(16, 36, 16, 36));
        footer.setStyle(
                "-fx-border-color: #e3e8f0 transparent transparent transparent;"
                + "-fx-border-width: 1 0 0 0;"
        );

        root.setBottom(footer);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getProfileButton() {
        return profileButton;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    public void setPageTitle(String title) {
        pageTitle.setText(title);
    }

    public void setPageDescription(String description) {
        pageDescription.setText(description);
    }

    public void setContent(Node content) {
        pageContent.getChildren().setAll(
                pageTitle,
                pageDescription,
                content
        );
    }
}
