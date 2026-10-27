
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

public class LoginView {

    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Button signUpButton;
    private final Label messageLabel;

    public LoginView() {
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(300);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(300);

        loginButton = new Button("Log In");
        loginButton.setDefaultButton(true);
        loginButton.setMaxWidth(300);

        signUpButton = new Button("Create an account");
        signUpButton.setMaxWidth(300);

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        Label titleLabel = new Label("LoanDesk");
        titleLabel.setStyle(
                "-fx-font-size: 30px; -fx-font-weight: bold;"
        );

        Label subtitleLabel = new Label(
                "Your personal equipment-lending register"
        );

        Label usernameLabel = new Label("Username");
        Label passwordLabel = new Label("Password");

        usernameLabel.setMaxWidth(300);
        passwordLabel.setMaxWidth(300);

        usernameLabel.setAlignment(Pos.CENTER_LEFT);
        passwordLabel.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(
                12,
                titleLabel,
                subtitleLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                loginButton,
                messageLabel,
                signUpButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Scene scene = new Scene(layout, 500, 550);

        loginButton.setStyle(
                "-fx-background-color: #2457a7;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
        );

        signUpButton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: #2457a7;"
        );

        layout.setFillWidth(true);

        loginButton.setMaxWidth(300);
        signUpButton.setMaxWidth(300);

        this.scene = scene;
    }

    private final Scene scene;

    public void show(Stage stage) {
        stage.setTitle("LoanDesk - Log In");
        stage.setScene(scene);
        stage.setMinWidth(450);
        stage.setMinHeight(500);
        stage.show();
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getSignUpButton() {
        return signUpButton;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }
}
