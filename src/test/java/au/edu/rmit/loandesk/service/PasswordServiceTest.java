package au.edu.rmit.loandesk.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private final PasswordService service = new PasswordService();

    private boolean verify(String password,
            PasswordService.PasswordHash h) {
        return service.verifyPassword(password, h.hash(), h.salt(),
                h.algorithm(), h.iterations(), h.keyLength());
    }

    @Test
    void correctPasswordVerifiesAndWrongPasswordFails() {
        PasswordService.PasswordHash hash =
                service.hashPassword("Secret#123");

        assertTrue(verify("Secret#123", hash));
        assertFalse(verify("secret#123", hash));
    }

    @Test
    void samePasswordGetsDifferentSaltAndHash() {
        PasswordService.PasswordHash first = service.hashPassword("Same#1234");
        PasswordService.PasswordHash second = service.hashPassword("Same#1234");

        assertNotEquals(first.salt(), second.salt());
        assertNotEquals(first.hash(), second.hash());
    }

    @Test
    void storedHashDoesNotContainThePlainPassword() {
        PasswordService.PasswordHash hash = service.hashPassword("Plain#9999");

        assertFalse(hash.hash().contains("Plain#9999"));
    }

    @Test
    void corruptedStoredValuesFailSafely() {
        assertFalse(service.verifyPassword("x", "!!", "!!",
                "PBKDF2WithHmacSHA256", 1000, 256));
        assertFalse(service.verifyPassword("x", "AA==", "AA==",
                "UNKNOWN", 1000, 256));
    }
}
