package au.edu.rmit.loandesk;

import au.edu.rmit.loandesk.controller.DashboardController;
import au.edu.rmit.loandesk.controller.LoginController;
import au.edu.rmit.loandesk.controller.ProfileController;
import au.edu.rmit.loandesk.controller.SignUpController;
import au.edu.rmit.loandesk.dao.Database;
import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.AuthService;
import au.edu.rmit.loandesk.view.LoginView;
import au.edu.rmit.loandesk.view.SignUpView;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private Stage primaryStage;
    private AuthService authService;
    private User loggedInUser;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            Database.initializeDatabase();
            authService = new AuthService();
            showLoginScreen();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    private void showLoginScreen() {
        // Clear the previous user's session before displaying login.
        loggedInUser = null;

        LoginView loginView = new LoginView();

        LoginController loginController = new LoginController(
                primaryStage,
                loginView,
                authService,
                this::showSignUpScreen,
                this::showDashboard
        );

        loginController.show();
    }

    private void showSignUpScreen() {
        SignUpView signUpView = new SignUpView();

        SignUpController signUpController = new SignUpController(
                primaryStage,
                signUpView,
                authService,
                this::showLoginScreen
        );

        signUpController.show();
    }

    private void showDashboard(User user) {
        loggedInUser = user;

        DashboardController dashboardController = new DashboardController(
                primaryStage,
                loggedInUser,
                this::showLoginScreen,
                this::showProfileScreen
        );

        dashboardController.show();
    }

    private void showProfileScreen() {
        if (loggedInUser == null) {
            showLoginScreen();
            return;
        }

        ProfileController profileController = new ProfileController(
                primaryStage,
                loggedInUser,
                () -> showDashboard(loggedInUser),
                this::showLoginScreen
        );

        profileController.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
