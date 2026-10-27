
package au.edu.rmit.loandesk.controller;

import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.AuthService;
import au.edu.rmit.loandesk.view.SignUpView;

import javafx.stage.Stage;

import java.sql.SQLException;

public class SignUpController {

    private final Stage stage;
    private final SignUpView signUpView;
    private final AuthService authService;
    private final Runnable onBackToLogin;

    public SignUpController(
            Stage stage,
            SignUpView signUpView,
            AuthService authService,
            Runnable onBackToLogin
    ) {
        this.stage = stage;
        this.signUpView = signUpView;
        this.authService = authService;
        this.onBackToLogin = onBackToLogin;

        configureActions();
    }

    private void configureActions() {
        signUpView.getCreateAccountButton()
                .setOnAction(event -> handleSignUp());

        signUpView.getConfirmPasswordField()
                .setOnAction(event -> handleSignUp());

        signUpView.getBackToLoginButton()
                .setOnAction(event -> onBackToLogin.run());
    }

    private void handleSignUp() {
        String username = signUpView.getUsernameField().getText();
        String firstName = signUpView.getFirstNameField().getText();
        String lastName = signUpView.getLastNameField().getText();
        String password = signUpView.getPasswordField().getText();
        String confirmPassword =
                signUpView.getConfirmPasswordField().getText();

        if (password == null || !password.equals(confirmPassword)) {
            showMessage("The passwords do not match.");
            signUpView.getConfirmPasswordField().clear();
            return;
        }

        try {
            User createdUser = authService.signUp(
                    username,
                    firstName,
                    lastName,
                    password
            );

            showMessage(
                    "Account created successfully for "
                            + createdUser.getFullName()
                            + ". You can now log in."
            );

            signUpView.getPasswordField().clear();
            signUpView.getConfirmPasswordField().clear();

        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());

        } catch (SQLException exception) {
            showMessage(
                    "Unable to save your account. Please try again."
            );
            exception.printStackTrace();
        }
    }

    private void showMessage(String message) {
        signUpView.getMessageLabel().setText(message);
    }

    public void show() {
        signUpView.show(stage);
    }
}
