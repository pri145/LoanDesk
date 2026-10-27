
package au.edu.rmit.loandesk.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void acceptsValidUsername() {
        assertEquals(
                "Priya_123",
                InputValidator.validateUsername(" Priya_123 ")
        );
    }

    @Test
    void rejectsUsernameBelowMinimumLength() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validateUsername("ab")
        );
    }

    @Test
    void acceptsUsernameAtMaximumLength() {
        assertEquals(
                "abcdefghijklmnopqrst",
                InputValidator.validateUsername("abcdefghijklmnopqrst")
        );
    }

    @Test
    void rejectsUsernameWithInvalidCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validateUsername("Priya@123")
        );
    }

    @Test
    void acceptsNameWithHyphenAndApostrophe() {
        assertEquals(
                "Mary-Jane",
                InputValidator.validateName(" Mary-Jane ", "First name")
        );

        assertEquals(
                "O'Brien",
                InputValidator.validateName("O'Brien", "Last name")
        );
    }

    @Test
    void rejectsBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validateName("   ", "First name")
        );
    }

    @Test
    void acceptsPasswordWithAllRequiredCharacterTypes() {
        assertDoesNotThrow(
                () -> InputValidator.validatePassword("Secure1!pass")
        );
    }

    @Test
    void rejectsPasswordShorterThanEightCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("Ab1!")
        );
    }

    @Test
    void rejectsPasswordMissingRequiredCharacterType() {
        // Missing uppercase letter.
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("secure1!pass")
        );

        // Missing lowercase letter.
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("SECURE1!PASS")
        );

        // Missing digit.
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("Secure!pass")
        );

        // Missing permitted symbol.
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("Secure123pass")
        );
    }

    @Test
    void rejectsPasswordContainingSpaces() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("Secure 1!pass")
        );
    }

    @Test
    void rejectsPasswordContainingUnapprovedSymbols() {
        assertThrows(
                IllegalArgumentException.class,
                () -> InputValidator.validatePassword("Secure1!pass+")
        );
    }
}
