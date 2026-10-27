
package au.edu.rmit.loandesk.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public record PasswordHash(
            String hash,
            String salt,
            String algorithm,
            int iterations,
            int keyLength) {
    }

    public PasswordHash hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] derivedKey = deriveKey(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return new PasswordHash(
                Base64.getEncoder().encodeToString(derivedKey),
                Base64.getEncoder().encodeToString(salt),
                ALGORITHM,
                ITERATIONS,
                KEY_LENGTH);
    }

    public boolean verifyPassword(
            String password,
            String storedHash,
            String storedSalt,
            String algorithm,
            int iterations,
            int keyLength) {

        if (!ALGORITHM.equals(algorithm)
                || iterations <= 0
                || keyLength <= 0) {
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(storedSalt);
            byte[] expectedHash = Base64.getDecoder().decode(storedHash);

            byte[] actualHash = deriveKey(
                    password.toCharArray(), salt, iterations, keyLength);

            return java.security.MessageDigest.isEqual(
                    expectedHash, actualHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] deriveKey(
            char[] password, byte[] salt, int iterations, int keyLength) {

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, iterations, keyLength);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Password hashing failed.", e);

        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }
}
