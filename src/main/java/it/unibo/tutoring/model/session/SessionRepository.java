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
import java.util.Objects;
import java.util.Optional;
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
            paths.filter(SessionRepository::isSessionFile)
                    .map(this::parseSessionFile)
                    .filter(Objects::nonNull)
                    .filter(SessionFileData::isConfirmed)
                    .filter(session -> session.involves(matricola))
                    .map(SessionRepository::toTutoringSession)
                    .filter(session -> !session.getDataOra().isBefore(now))
                    .sorted(Comparator.comparing(TutoringSession::getDataOra))
                    .forEach(sessions::add);
        } catch (IOException e) {
            System.err.println("Errore nella lettura della cartella sessioni: " + e.getMessage());
        }

        return sessions;
    }

    /**
     * Cerca una sessione confermata che si sovrappone all'intervallo proposto
     * per l'utente indicato. Gli intervalli sono considerati semiaperti:
     * una sessione che termina esattamente quando ne inizia un'altra non crea
     * conflitto.
     *
     * @param matricola utente di cui verificare gli impegni
     * @param nuovoInizio inizio della sessione da confermare
     * @param nuovaDurata durata della sessione da confermare
     * @param sessioneDaEscludere file della sessione corrente, oppure null
     * @return il primo conflitto in ordine cronologico, se presente
     */
    public Optional<SessionConflict> findOverlappingConfirmedSession(
            final String matricola,
            final LocalDateTime nuovoInizio,
            final Duration nuovaDurata,
            final Path sessioneDaEscludere) {
        if (matricola == null || matricola.isBlank()) {
            throw new IllegalArgumentException("La matricola e' obbligatoria.");
        }
        if (nuovoInizio == null) {
            throw new IllegalArgumentException("L'inizio della sessione e' obbligatorio.");
        }
        if (nuovaDurata == null || nuovaDurata.isZero() || nuovaDurata.isNegative()) {
            throw new IllegalArgumentException("La durata della sessione deve essere positiva.");
        }
        if (!Files.isDirectory(SESSION_FOLDER)) {
            return Optional.empty();
        }

        final LocalDateTime nuovaFine = nuovoInizio.plus(nuovaDurata);
        try (Stream<Path> paths = Files.list(SESSION_FOLDER)) {
            return paths.filter(SessionRepository::isSessionFile)
                    .filter(path -> !samePath(path, sessioneDaEscludere))
                    .map(this::parseSessionFile)
                    .filter(Objects::nonNull)
                    .filter(SessionFileData::isConfirmed)
                    .filter(session -> session.involves(matricola))
                    .filter(session -> overlaps(
                            nuovoInizio,
                            nuovaFine,
                            session.dataOra(),
                            session.fine()))
                    .sorted(Comparator.comparing(SessionFileData::dataOra))
                    .map(session -> new SessionConflict(
                            session.materia(),
                            session.dataOra(),
                            session.fine()))
                    .findFirst();
        } catch (final IOException e) {
            throw new IllegalStateException(
                    "Impossibile verificare la disponibilita' delle sessioni.", e);
        }
    }

    private SessionFileData parseSessionFile(final Path filePath) {
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

            final String[] participants = participantsFromFileName(filePath);
            if (tutor.isBlank()) {
                tutor = participants[0];
            }
            if (studente.isBlank()) {
                studente = participants[1];
            }
            if (materia.isBlank() || dataOra.isBlank() || durata.isBlank() || tutor.isBlank()) {
                return null;
            }

            final Duration parsedDuration = Duration.parse(durata);
            if (parsedDuration.isZero() || parsedDuration.isNegative()) {
                return null;
            }
            return new SessionFileData(
                    stato,
                    materia,
                    LocalDateTime.parse(dataOra),
                    parsedDuration,
                    tutor,
                    studente);
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Errore nella lettura del file " + filePath.getFileName() + ": " + e.getMessage());
            return null;
        }
    }

    private static TutoringSession toTutoringSession(final SessionFileData data) {
        final TutoringSession session = new TutoringSessionImpl(
                data.materia(),
                data.dataOra(),
                data.durata(),
                data.tutor());
        session.conferma();
        return session;
    }

    private static boolean isSessionFile(final Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        final String fileName = path.getFileName().toString();
        return fileName.startsWith("SESS_") && fileName.endsWith(".csv");
    }

    private static boolean samePath(final Path first, final Path second) {
        return second != null
                && first.toAbsolutePath().normalize()
                .equals(second.toAbsolutePath().normalize());
    }

    private static boolean overlaps(
            final LocalDateTime firstStart,
            final LocalDateTime firstEnd,
            final LocalDateTime secondStart,
            final LocalDateTime secondEnd) {
        return firstStart.isBefore(secondEnd) && secondStart.isBefore(firstEnd);
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

    // Informazioni sulla sessione che occupa l'intervallo richiesto.
    public record SessionConflict(
            String materia,
            LocalDateTime inizio,
            LocalDateTime fine) {
    }

    private record SessionFileData(
            String stato,
            String materia,
            LocalDateTime dataOra,
            Duration durata,
            String tutor,
            String studente) {

        private boolean isConfirmed() {
            return "Confirmed".equals(this.stato);
        }

        private boolean involves(final String matricola) {
            return matricola != null
                    && (matricola.equals(this.tutor) || matricola.equals(this.studente));
        }

        private LocalDateTime fine() {
            return this.dataOra.plus(this.durata);
        }
    }
}