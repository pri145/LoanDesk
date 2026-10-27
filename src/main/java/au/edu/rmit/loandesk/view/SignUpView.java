
package au.edu.rmit.loandesk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignUpView {

    private final TextField usernameField;
    private final TextField firstNameField;
    private final TextField lastNameField;
    private final PasswordField passwordField;
    private final PasswordField confirmPasswordField;
    private final Button createAccountButton;
    private final Button backToLoginButton;
    private final Label messageLabel;
    private final Scene scene;

    public SignUpView() {
        usernameField = new TextField();
        usernameField.setPromptText("3–20 characters: letters, numbers, _");

        firstNameField = new TextField();
        firstNameField.setPromptText("First name");

        lastNameField = new TextField();
        lastNameField.setPromptText("Last name");

        passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter your password");

        createAccountButton = new Button("Create Account");
        backToLoginButton = new Button("Back to Log In");

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);

        Label titleLabel = new Label("Create your LoanDesk account");
        titleLabel.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        Label passwordHint = new Label(
                "Use 8+ characters with uppercase, lowercase, a digit "
                + "and a permitted symbol (! @ # $ % ^ & * ? . _ -)."
        );
        passwordHint.setWrapText(true);
        passwordHint.setMaxWidth(340);
        
        Label usernameLabel = new Label("Username");
        Label firstNameLabel = new Label("First name");
        Label lastNameLabel = new Label("Last name");
        Label passwordLabel = new Label("Password");
        Label confirmPasswordLabel = new Label("Confirm password");

        Label[] fieldLabels = {
            usernameLabel,
            firstNameLabel,
            lastNameLabel,
            passwordLabel,
            confirmPasswordLabel
        };

        for (Label label : fieldLabels) {
            label.setMaxWidth(340);
            label.setAlignment(Pos.CENTER_LEFT);
        }

        VBox layout = new VBox(
                10,
                titleLabel,
                usernameLabel,
                usernameField,
                firstNameLabel,
                firstNameField,
                lastNameLabel,
                lastNameField,
                passwordLabel,
                passwordField,
                confirmPasswordLabel,
                confirmPasswordField,
                passwordHint,
                createAccountButton,
                messageLabel,
                backToLoginButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setFillWidth(true);
        layout.setStyle("-fx-background-color: #f4f6f9;");

        usernameField.setMaxWidth(340);
        firstNameField.setMaxWidth(340);
        lastNameField.setMaxWidth(340);
        passwordField.setMaxWidth(340);
        confirmPasswordField.setMaxWidth(340);
        createAccountButton.setMaxWidth(340);
        backToLoginButton.setMaxWidth(340);

        createAccountButton.setDefaultButton(true);
        createAccountButton.setStyle(
                "-fx-background-color: #2457a7;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
        );

        backToLoginButton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: #2457a7;"
        );

        scene = new Scene(layout, 500, 760);
    }

    public void show(Stage stage) {
        stage.setTitle("LoanDesk - Sign Up");
        stage.setScene(scene);
        stage.setMinWidth(450);
        stage.setMinHeight(650);
        stage.show();
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getFirstNameField() {
        return firstNameField;
    }

    public TextField getLastNameField() {
        return lastNameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public PasswordField getConfirmPasswordField() {
        return confirmPasswordField;
    }

    public Button getCreateAccountButton() {
        return createAccountButton;
    }

    public Button getBackToLoginButton() {
        return backToLoginButton;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }
}
