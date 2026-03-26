package it.unibo.samplejavafx.unibotutoring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AuthService {

    static final class RegistrationResult {
        private final boolean success;
        private final String message;

        RegistrationResult(final boolean success, final String message) {
            this.success = success;
            this.message = message;
        }

        boolean isSuccess() {
            return this.success;
        }

        String getMessage() {
            return this.message;
        }
    }

    private static final Path STORAGE_PATH = Path.of("data", "users.csv");
    private static final String FIELD_SEPARATOR = ";";
    private static final AuthService INSTANCE = new AuthService();

    private final Map<String, UserAccount> usersByMatricola = new HashMap<>();

    private AuthService() {
        this.loadUsers();
    }

    static AuthService getInstance() {
        return INSTANCE;
    }

    synchronized RegistrationResult register(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String password
    ) {
        final String cleanName = name.trim();
        final String cleanSurname = surname.trim();
        final String cleanMatricola = matricola.trim();
        final String cleanEmail = email.trim().toLowerCase();

        if (!cleanMatricola.matches("\\d{7}")) {
            return new RegistrationResult(false, "La matricola deve contenere 7 cifre.");
        }
        if (this.usersByMatricola.containsKey(cleanMatricola)) {
            return new RegistrationResult(false, "Matricola gia registrata.");
        }

        final UserAccount user = new UserAccount(
            cleanName,
            cleanSurname,
            cleanMatricola,
            cleanEmail,
            hashPassword(password)
        );

        this.usersByMatricola.put(cleanMatricola, user);
        try {
            this.persistUsers();
            return new RegistrationResult(true, "Registrazione completata.");
        } catch (final IOException ex) {
            this.usersByMatricola.remove(cleanMatricola);
            return new RegistrationResult(false, "Errore nel salvataggio dei dati.");
        }
    }

    synchronized boolean authenticate(final String matricola, final String password) {
        final String cleanMatricola = matricola.trim();
        final UserAccount user = this.usersByMatricola.get(cleanMatricola);
        if (user == null) {
            return false;
        }
        return user.getPasswordHash().equals(hashPassword(password));
    }

    private void loadUsers() {
        try {
            if (!Files.exists(STORAGE_PATH)) {
                return;
            }
            final List<String> lines = Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8);
            for (final String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                final String[] fields = line.split(FIELD_SEPARATOR, -1);
                if (fields.length != 5) {
                    continue;
                }
                final UserAccount user = new UserAccount(fields[0], fields[1], fields[2], fields[3], fields[4]);
                this.usersByMatricola.put(user.getMatricola(), user);
            }
        } catch (final IOException ex) {
            this.usersByMatricola.clear();
        }
    }

    private void persistUsers() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        final List<String> rows = this.usersByMatricola.values().stream()
            .map(user -> String.join(
                FIELD_SEPARATOR,
                user.getName(),
                user.getSurname(),
                user.getMatricola(),
                user.getEmail(),
                user.getPasswordHash()
            ))
            .sorted()
            .toList();
        Files.write(STORAGE_PATH, rows, StandardCharsets.UTF_8);
    }

    private static String hashPassword(final String password) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            final StringBuilder builder = new StringBuilder(hash.length * 2);
            for (final byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (final NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 non disponibile", ex);
        }
    }
}
