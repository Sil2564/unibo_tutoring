package it.unibo.tutoring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private static final Path USERS_PATH = Path.of("data", "users.csv");
    private byte[] originalUsers;

    @BeforeEach
    void preserveUsersFile() throws IOException {
        this.originalUsers = Files.exists(USERS_PATH) ? Files.readAllBytes(USERS_PATH) : null;
    }

    @AfterEach
    void restoreUsersFile() throws IOException {
        if (this.originalUsers == null) {
            Files.deleteIfExists(USERS_PATH);
        } else {
            Files.write(USERS_PATH, this.originalUsers);
        }
    }

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
    void registrationRejectsDuplicateMatricolaAndEmail() throws IOException {
        final String suffix = String.valueOf(System.nanoTime()).substring(0, 6);
        final String matricola = "1234" + suffix;
        final String email = "student" + suffix + "@studio.unibo.it";

        final AuthService authService = AuthService.getInstance();
        final AuthService.RegistrationResult first = authService.register(
            "Mario",
            "Rossi",
            matricola,
            email,
            "Password1!",
            "01-01-2000",
            "Informatica"
        );
        assertTrue(first.isSuccess(), first.getMessage());
        final byte[] persistedUsers = Files.readAllBytes(USERS_PATH);
        assertTrue(persistedUsers.length > 0);
        assertTrue(persistedUsers[persistedUsers.length - 1] == '\n'
                || persistedUsers[persistedUsers.length - 1] == '\r');

        final AuthService.RegistrationResult duplicateMatricola = authService.register(
            "Luigi",
            "Verdi",
            matricola,
            "another" + suffix + "@studio.unibo.it",
            "Password2!",
            "01-01-2000",
            "Informatica"
        );
        assertFalse(duplicateMatricola.isSuccess());
        assertTrue(duplicateMatricola.getMessage().contains("matricola"));

        final AuthService.RegistrationResult duplicateEmail = authService.register(
            "Giulia",
            "Bianchi",
            "5432" + suffix,
            email,
            "Password3!",
            "01-01-2000",
            "Informatica"
        );
        assertFalse(duplicateEmail.isSuccess());
        assertTrue(duplicateEmail.getMessage().contains("email"));
    }
}
