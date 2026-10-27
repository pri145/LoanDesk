package au.edu.rmit.loandesk.view;

import au.edu.rmit.loandesk.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileView {

    private final Scene scene;
    private final MainLayout mainLayout;

    private final TextField firstNameField;
    private final TextField lastNameField;
    private final Button saveButton;
    private final Button cancelButton;
    private final Label messageLabel;

    private final PasswordField currentPasswordField;
    private final PasswordField newPasswordField;
    private final PasswordField confirmPasswordField;
    private final Button changePasswordButton;
    private final Label passwordMessageLabel;

    public ProfileView(User user) {

        // Personal details
        Label usernameLabel = createFieldLabel("Username");

        TextField usernameField = new TextField(user.getUsername());
        usernameField.setEditable(false);
        usernameField.setFocusTraversable(false);
        styleTextField(usernameField);
        usernameField.setStyle(
                usernameField.getStyle()
                + "-fx-background-color: #eef1f6;"
                + "-fx-text-fill: #64748b;"
        );

        firstNameField = new TextField(user.getFirstName());
        firstNameField.setPromptText("Enter your first name");
        styleTextField(firstNameField);

        lastNameField = new TextField(user.getLastName());
        lastNameField.setPromptText("Enter your last name");
        styleTextField(lastNameField);

        saveButton = new Button("Save Changes");
        stylePrimaryButton(saveButton);

        cancelButton = new Button("Cancel");
        cancelButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #2457a7;"
                + "-fx-font-size: 14px;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: #d9e2f0;"
                + "-fx-border-radius: 7;"
        );

        HBox profileActions = new HBox(12, cancelButton, saveButton);
        profileActions.setAlignment(Pos.CENTER_RIGHT);
        profileActions.setPadding(new Insets(12, 0, 0, 0));

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        styleMessage(messageLabel, false);

        VBox profileCard = new VBox(
                12,
                createSectionTitle("Personal Details"),
                usernameLabel,
                usernameField,
                createFieldLabel("First name"),
                firstNameField,
                createFieldLabel("Last name"),
                lastNameField,
                messageLabel,
                profileActions
        );
        styleCard(profileCard);

        // Password change
        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        styleTextField(currentPasswordField);

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        styleTextField(newPasswordField);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter new password");
        styleTextField(confirmPasswordField);

        Label passwordHint = new Label(
                "At least 8 characters, with uppercase and lowercase letters, "
                + "a digit and an allowed symbol."
        );
        passwordHint.setWrapText(true);
        passwordHint.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #64748b;"
        );

        changePasswordButton = new Button("Change Password");
        stylePrimaryButton(changePasswordButton);
        changePasswordButton.setMaxWidth(Double.MAX_VALUE);

        passwordMessageLabel = new Label();
        passwordMessageLabel.setWrapText(true);
        passwordMessageLabel.setMaxWidth(Double.MAX_VALUE);
        styleMessage(passwordMessageLabel, false);

        VBox passwordCard = new VBox(
                12,
                createSectionTitle("Change Password"),
                createFieldLabel("Current password"),
                currentPasswordField,
                createFieldLabel("New password"),
                newPasswordField,
                createFieldLabel("Confirm new password"),
                confirmPasswordField,
                passwordHint,
                passwordMessageLabel,
                changePasswordButton
        );
        styleCard(passwordCard);

        // Display the two cards side by side.
        HBox cards = new HBox(20, profileCard, passwordCard);
        cards.setAlignment(Pos.TOP_LEFT);
        cards.setFillHeight(true);

        HBox.setHgrow(profileCard, Priority.ALWAYS);
        HBox.setHgrow(passwordCard, Priority.ALWAYS);
        profileCard.setMaxWidth(Double.MAX_VALUE);
        passwordCard.setMaxWidth(Double.MAX_VALUE);
        profileCard.setMinWidth(0);
        passwordCard.setMinWidth(0);

        ScrollPane scrollPane = new ScrollPane(cards);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
                "-fx-background: transparent;"
                + "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox content = new VBox(16, scrollPane);
        content.setPadding(new Insets(4, 0, 12, 0));
        content.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainLayout = new MainLayout(
                "Edit Profile",
                "Manage your personal details and account security.",
                content
        );

        mainLayout.getProfileButton().setVisible(false);
        mainLayout.getProfileButton().setManaged(false);
    
        scene = new Scene(mainLayout.getRoot(), 1180, 760);
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #1f2937;"
        );
        return label;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #374151;"
        );
        return label;
    }

    private void styleTextField(TextField field) {
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #1f2937;"
                + "-fx-background-radius: 6;"
                + "-fx-border-color: #cbd5e1;"
                + "-fx-border-radius: 6;"
                + "-fx-padding: 10;"
        );
    }

    private void stylePrimaryButton(Button button) {
        button.setStyle(
                "-fx-background-color: #2457a7;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 11 20;"
                + "-fx-background-radius: 7;"
        );
    }

    private void styleMessage(Label label, boolean success) {
        label.setStyle(
                "-fx-font-size: 13px;"
                + (success
                    ? "-fx-text-fill: #15803d;"
                    : "-fx-text-fill: #b42318;")
        );
    }

    private void styleCard(VBox card) {
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #e3e8f0;"
                + "-fx-border-radius: 12;"
        );
    }

    public void show(Stage stage) {
        stage.setTitle("LoanDesk - Edit Profile");
        stage.setScene(scene);
        WindowSettings.apply(stage);
        stage.show();
    }

    public String getFirstName() {
        return firstNameField.getText();
    }

    public String getLastName() {
        return lastNameField.getText();
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public String getCurrentPassword() {
        return currentPasswordField.getText();
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    public Button getChangePasswordButton() {
        return changePasswordButton;
    }

    public Button getLogoutButton() {
        return mainLayout.getLogoutButton();
    }

    public void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    public void showMessage(String message) {
        styleMessage(messageLabel, false);
        messageLabel.setText(message);
    }

    public void showPasswordMessage(String message, boolean success) {
        styleMessage(passwordMessageLabel, success);
        passwordMessageLabel.setText(message);
    }
}
