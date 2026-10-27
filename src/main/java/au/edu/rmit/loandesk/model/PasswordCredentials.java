
package au.edu.rmit.loandesk.model;

public record PasswordCredentials(
        int userId,
        String username,
        String passwordHash,
        String passwordSalt,
        String passwordAlgorithm,
        int passwordIterations,
        int passwordKeyLength) {
}
