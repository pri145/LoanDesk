package au.edu.rmit.loandesk.controller;

import au.edu.rmit.loandesk.dao.UserDao;
import au.edu.rmit.loandesk.model.User;
import au.edu.rmit.loandesk.service.AuthService;
import au.edu.rmit.loandesk.service.InputValidator;
import au.edu.rmit.loandesk.view.ProfileView;

import java.sql.SQLException;

import javafx.stage.Stage;

public class ProfileController {

    private final Stage stage;
    private final User user;
    private final UserDao userDao;
    private final AuthService authService;
    private final ProfileView profileView;
    private final Runnable onReturnToDashboard;
    private final Runnable onLogout;

    public ProfileController(
            Stage stage,
            User user,
            Runnable onReturnToDashboard) {

        this(stage, user, onReturnToDashboard, onReturnToDashboard);
    }

    public ProfileController(
            Stage stage,
            User user,
            Runnable onReturnToDashboard,
            Runnable onLogout) {

        this.stage = stage;
        this.user = user;
        this.userDao = new UserDao();
        this.authService = new AuthService();
        this.profileView = new ProfileView(user);
        this.onReturnToDashboard = onReturnToDashboard;
        this.onLogout = onLogout;

        configureActions();
    }

    private void configureActions() {
        profileView.getSaveButton().setOnAction(
                event -> handleSave());

        profileView.getCancelButton().setOnAction(
                event -> onReturnToDashboard.run());

        profileView.getChangePasswordButton().setOnAction(
                event -> handleChangePassword());

        profileView.getLogoutButton().setOnAction(
                event -> onLogout.run());
    }

    private void handleSave() {
        try {
            String firstName = InputValidator.validateName(
                    profileView.getFirstName(), "First name");

            String lastName = InputValidator.validateName(
                    profileView.getLastName(), "Last name");

            boolean updated = userDao.updateProfile(
                    user.getId(), firstName, lastName);

            if (!updated) {
                profileView.showMessage(
                        "Your profile could not be found. No changes were saved.");
                return;
            }

            // Update the logged-in session only after the database succeeds.
            user.setFirstName(firstName);
            user.setLastName(lastName);

            onReturnToDashboard.run();

        } catch (IllegalArgumentException exception) {
            profileView.showMessage(exception.getMessage());

        } catch (SQLException exception) {
            profileView.showMessage(
                    "Unable to save your profile. Please try again.");
            exception.printStackTrace();
        }
    }

    private void handleChangePassword() {
        try {
            authService.changePassword(
                    user.getId(),
                    user.getUsername(),
                    profileView.getCurrentPassword(),
                    profileView.getNewPassword(),
                    profileView.getConfirmPassword());

            profileView.clearPasswordFields();
            profileView.showPasswordMessage(
                    "Password changed successfully.", true);

        } catch (IllegalArgumentException exception) {
            profileView.showPasswordMessage(
                    exception.getMessage(), false);

        } catch (SQLException exception) {
            profileView.showPasswordMessage(
                    "Unable to change your password. Please try again.",
                    false);
            exception.printStackTrace();
        }
    }

    public void show() {
        profileView.show(stage);
    }
}
