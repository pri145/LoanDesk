
package au.edu.rmit.loandesk.controller;

import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.AuthService;
import au.edu.rmit.loandesk.view.LoginView;

import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

public class LoginController {

    private final LoginView loginView;
    private final AuthService authService;
    private final Stage stage;
    private final Runnable onSignUp;
    private final Consumer<User> onLoginSuccess;

    public LoginController(
            Stage stage,
            LoginView loginView,
            AuthService authService,
            Runnable onSignUp,
            Consumer<User> onLoginSuccess
    ) {
        this.stage = stage;
        this.loginView = loginView;
        this.authService = authService;
        this.onSignUp = onSignUp;
        this.onLoginSuccess = onLoginSuccess;

        configureActions();
    }

    private void configureActions() {
        loginView.getLoginButton()
                .setOnAction(event -> handleLogin());

        loginView.getPasswordField()
                .setOnAction(event -> handleLogin());

        loginView.getSignUpButton()
                .setOnAction(event -> onSignUp.run());
    }

    private void handleLogin() {
        String username = loginView.getUsernameField().getText();
        String password = loginView.getPasswordField().getText();

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            showMessage("Please enter both your username and password.");
            return;
        }

        try {
            Optional<User> authenticatedUser =
                    authService.logIn(username, password);

            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();

                loginView.getPasswordField().clear();

                // Notify App that authentication succeeded.
                onLoginSuccess.accept(user);

            } else {
                showMessage("Incorrect username or password.");
                loginView.getPasswordField().clear();
            }

        } catch (SQLException exception) {
            showMessage(
                    "Unable to access your account. Please try again."
            );
            exception.printStackTrace();
        }
    }

    private void showMessage(String message) {
        loginView.getMessageLabel().setText(message);
    }

    public void show() {
        loginView.show(stage);
    }
}
