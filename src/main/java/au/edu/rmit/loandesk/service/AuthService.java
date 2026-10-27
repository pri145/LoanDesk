package au.edu.rmit.loandesk.service;

import au.edu.rmit.loandesk.dao.UserDao;
import au.edu.rmit.loandesk.model.PasswordCredentials;
import au.edu.rmit.loandesk.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final UserDao userDao;
    private final PasswordService passwordService;

    public AuthService() {
        this.userDao = new UserDao();
        this.passwordService = new PasswordService();
    }

    public AuthService(UserDao userDao, PasswordService passwordService) {
        this.userDao = userDao;
        this.passwordService = passwordService;
    }

    public User signUp(
            String username,
            String firstName,
            String lastName,
            String password) throws SQLException {

        String validUsername = InputValidator.validateUsername(username);
        String validFirstName =
                InputValidator.validateName(firstName, "First name");
        String validLastName =
                InputValidator.validateName(lastName, "Last name");

        // Passwords must not be trimmed or otherwise modified.
        InputValidator.validatePassword(password);

        if (userDao.usernameExists(validUsername)) {
            throw new IllegalArgumentException(
                    "That username is already taken.");
        }

        PasswordService.PasswordHash passwordHash =
                passwordService.hashPassword(password);

        int userId = userDao.createUser(
                validUsername,
                validFirstName,
                validLastName,
                passwordHash.hash(),
                passwordHash.salt(),
                passwordHash.algorithm(),
                passwordHash.iterations(),
                passwordHash.keyLength());

        return new User(
                userId,
                validUsername,
                validFirstName,
                validLastName);
    }

    public Optional<User> logIn(String username, String password)
            throws SQLException {

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        String cleanedUsername = username.trim();

        Optional<PasswordCredentials> credentials =
                userDao.findCredentialsByUsername(cleanedUsername);

        if (credentials.isEmpty()) {
            return Optional.empty();
        }

        PasswordCredentials storedCredentials = credentials.get();

        boolean passwordMatches = passwordService.verifyPassword(
                password,
                storedCredentials.passwordHash(),
                storedCredentials.passwordSalt(),
                storedCredentials.passwordAlgorithm(),
                storedCredentials.passwordIterations(),
                storedCredentials.passwordKeyLength());

        if (!passwordMatches) {
            return Optional.empty();
        }

        return userDao.findByUsername(cleanedUsername);
    }

    public void changePassword(
            int userId,
            String username,
            String currentPassword,
            String newPassword,
            String confirmNewPassword) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user account.");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException(
                    "Enter your current password.");
        }

        if (newPassword == null || confirmNewPassword == null) {
            throw new IllegalArgumentException(
                    "Enter and confirm your new password.");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException(
                    "The new password and confirmation do not match.");
        }

        // Apply exactly the same password rules as signup.
        InputValidator.validatePassword(newPassword);

        Optional<PasswordCredentials> credentials =
                userDao.findCredentialsByUsername(username.trim());

        if (credentials.isEmpty()
                || credentials.get().userId() != userId) {
            throw new IllegalArgumentException(
                    "Unable to verify the current account.");
        }

        PasswordCredentials stored = credentials.get();

        boolean currentPasswordMatches = passwordService.verifyPassword(
                currentPassword,
                stored.passwordHash(),
                stored.passwordSalt(),
                stored.passwordAlgorithm(),
                stored.passwordIterations(),
                stored.passwordKeyLength());

        if (!currentPasswordMatches) {
            throw new IllegalArgumentException(
                    "The current password is incorrect.");
        }

        boolean passwordIsUnchanged = passwordService.verifyPassword(
                newPassword,
                stored.passwordHash(),
                stored.passwordSalt(),
                stored.passwordAlgorithm(),
                stored.passwordIterations(),
                stored.passwordKeyLength());

        if (passwordIsUnchanged) {
            throw new IllegalArgumentException(
                    "Your new password must differ from your current password.");
        }

        // hashPassword generates a fresh random salt.
        PasswordService.PasswordHash newCredentials =
                passwordService.hashPassword(newPassword);

        boolean updated = userDao.updatePasswordCredentials(
                userId,
                newCredentials.hash(),
                newCredentials.salt(),
                newCredentials.algorithm(),
                newCredentials.iterations(),
                newCredentials.keyLength());

        if (!updated) {
            throw new SQLException(
                    "The password could not be updated. No account was changed.");
        }
    }
}
