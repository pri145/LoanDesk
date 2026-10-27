package au.edu.rmit.loandesk.service;

public final class InputValidator {

    private InputValidator() {
        // Prevent objects from being created from this utility class.
    }

    public static String validateUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required.");
        }

        String cleanedUsername = username.trim();

        if (cleanedUsername.length() < 3 || cleanedUsername.length() > 20) {
            throw new IllegalArgumentException(
                    "Username must be between 3 and 20 characters."
            );
        }

        if (!cleanedUsername.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException(
                    "Username can contain only letters, numbers and underscores."
            );
        }

        return cleanedUsername;
    }

    public static String validateName(String name, String fieldName) {
        if (name == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String cleanedName = name.trim();

        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        if (cleanedName.length() > 50) {
            throw new IllegalArgumentException(
                    fieldName + " cannot exceed 50 characters."
            );
        }

        return cleanedName;
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long."
            );
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasRequiredSymbol = false;

        String allowedSymbols = "!@#$%^&*?._-";

        for (char character : password.toCharArray()) {
            if (character >= 'A' && character <= 'Z') {
                hasUppercase = true;
            } else if (character >= 'a' && character <= 'z') {
                hasLowercase = true;
            } else if (character >= '0' && character <= '9') {
                hasDigit = true;
            } else if (allowedSymbols.indexOf(character) >= 0) {
                hasRequiredSymbol = true;
            } else {
                throw new IllegalArgumentException(
                        "Password contains an invalid character."
                );
            }
        }

        if (!hasUppercase || !hasLowercase || !hasDigit || !hasRequiredSymbol) {
            throw new IllegalArgumentException(
                    "Password must include an uppercase letter, a lowercase "
                    + "letter, a digit and at least one permitted symbol "
                    + "(! @ # $ % ^ & * ? . _ -)."
            );
        }
    }
}