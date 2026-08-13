package it.unibo.tutoring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuthServiceTest {

    @Test
    void passwordWithEnoughLengthAndNumberIsAccepted() {
        assertTrue(AuthService.isPasswordValid("Password1!"));
    }

    @Test
    void passwordWithoutNumberOrTooShortIsRejected() {
        assertFalse(AuthService.isPasswordValid("pass"));
        assertFalse(AuthService.isPasswordValid("password1!")); 
        assertFalse(AuthService.isPasswordValid("Password!"));  
        assertFalse(AuthService.isPasswordValid("Password1"));  
        }

    @Test
    void registrationRejectsDuplicateMatricolaAndEmail() {
        final String suffix = String.valueOf(System.nanoTime()).substring(0, 6);
        final String matricola = "1234" + suffix;
        final String email = "student" + suffix + "@studio.unibo.it";

        final AuthService authService = AuthService.getInstance();
        final AuthService.RegistrationResult first = authService.register(
            "Mario",
            "Rossi",
            matricola,
            email,
            "Password1!"
        );
        assertTrue(first.isSuccess(), first.getMessage());

        final AuthService.RegistrationResult duplicateMatricola = authService.register(
            "Luigi",
            "Verdi",
            matricola,
            "another" + suffix + "@studio.unibo.it",
            "Password2!"
        );
        assertFalse(duplicateMatricola.isSuccess());
        assertTrue(duplicateMatricola.getMessage().contains("matricola"));

        final AuthService.RegistrationResult duplicateEmail = authService.register(
            "Giulia",
            "Bianchi",
            "5432" + suffix,
            email,
            "Password3!"
        );
        assertFalse(duplicateEmail.isSuccess());
        assertTrue(duplicateEmail.getMessage().contains("email"));
    }
}
