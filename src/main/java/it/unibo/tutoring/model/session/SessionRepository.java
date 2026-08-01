package it.unibo.tutoring.model.session;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SessionRepository {

    private static final Path SESSION_FOLDER = Path.of("data", "sessions");

    /**
     * Restituisce le sessioni in stato "Confirmed" a cui partecipa l'utente.
     */
    public List<TutoringSession> getConfirmedSessionsForUser(final String matricola) {
        final List<TutoringSession> sessions = new ArrayList<>();

        if (!Files.isDirectory(SESSION_FOLDER)) {
            return sessions;
        }

        final LocalDateTime now = LocalDateTime.now();
        try (Stream<Path> paths = Files.list(SESSION_FOLDER)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("SESS_"))
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .map(path -> parseSessionFile(path, matricola))
                    .filter(session -> session != null)
                    .filter(session -> !session.getDataOra().isBefore(now))
                    .sorted(Comparator.comparing(TutoringSession::getDataOra))
                    .forEach(sessions::add);
        } catch (IOException e) {
            System.err.println("Errore nella lettura della cartella sessioni: " + e.getMessage());
        }

        return sessions;
    }

    /**
     * Legge il file CSV e ricostruisce l'oggetto TutoringSession se lo stato è Confirmed.
     */
    private TutoringSession parseSessionFile(final Path filePath, final String matricola) {
        try {
            String stato = "";
            String materia = "";
            String dataOra = "";
            String durata = "";
            String tutor = "";
            String studente = "";

            for (final String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line.startsWith("STATO;")) {
                    stato = valueOf(line);
                } else if (line.startsWith("MATERIA;")) {
                    materia = valueOf(line);
                } else if (line.startsWith("DATA_ORA;")) {
                    dataOra = valueOf(line);
                } else if (line.startsWith("DURATA;")) {
                    durata = valueOf(line);
                } else if (line.startsWith("TUTOR;")) {
                    tutor = valueOf(line);
                } else if (line.startsWith("STUDENTE;")) {
                    studente = valueOf(line);
                }
            }

            if (!"Confirmed".equals(stato)) {
                return null;
            }

            final String[] participants = participantsFromFileName(filePath);
            if (tutor.isBlank()) {
                tutor = participants[0];
            }
            if (studente.isBlank()) {
                studente = participants[1];
            }
            if (!matricola.equals(tutor) && !matricola.equals(studente)) {
                return null;
            }

            if (materia.isBlank() || dataOra.isBlank() || durata.isBlank() || tutor.isBlank()) {
                return null;
            }

            final TutoringSession session = new TutoringSessionImpl(
                    materia,
                    LocalDateTime.parse(dataOra),
                    Duration.parse(durata),
                    tutor);
            session.conferma();
            return session;
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Errore nella lettura del file " + filePath.getFileName() + ": " + e.getMessage());
            return null;
        }
    }

    private static String valueOf(final String line) {
        return line.substring(line.indexOf(';') + 1).trim();
    }

    private static String[] participantsFromFileName(final Path filePath) {
        final String fileName = filePath.getFileName().toString();
        final String withoutExtension = fileName.substring(0, fileName.length() - ".csv".length());
        final String[] parts = withoutExtension.split("_");
        if (parts.length < 4) {
            return new String[] {"", ""};
        }
        return new String[] {parts[parts.length - 2], parts[parts.length - 1]};
    }
}