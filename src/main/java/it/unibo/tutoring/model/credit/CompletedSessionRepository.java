package it.unibo.tutoring.model.credit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight repository per caricamento delle sessioni completate.
 * Legge il file data/completed_sessions.csv e fornisce accesso ai record.
 */
public final class CompletedSessionRepository {

    private static final Path DB = Paths.get("data", "completed_sessions.csv");
    private static final String SEP = ";";

    private CompletedSessionRepository() {
    }

    /**
     * Record semplice che rappresenta una sessione completata.
     */
    public record CompletedSession(
        String studentName,
        String subject,
        String date,
        int hours,
        int creditsGiven
    ) {}

    /**
     * Carica tutte le sessioni completate per il tutor fornito.
     *
     * @param tutorMatricola id del tutor che ha completato le sessioni
     * @return lista di CompletedSession per quel tutor
     */
    public static List<CompletedSession> loadCompletedSessionsForTutor(final String tutorMatricola) {
        final List<CompletedSession> sessions = new ArrayList<>();

        try {
            if (!Files.exists(DB)) {
                return sessions;
            }

            final List<String> lines = Files.readAllLines(DB);
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty() || line.startsWith("studentName")) {
                    continue; // header
                }

                final String[] parts = line.split(SEP, -1);
                if (parts.length < 5) {
                    continue;
                }

                // last column is tutor matricola
                final String tutor = parts[parts.length - 1].trim();
                if (!tutor.equals(tutorMatricola)) {
                    continue;
                }

                // determine if creditsGiven is present
                final boolean hasCredits = parts.length >= 6;
                final int hoursIndex = parts.length - (hasCredits ? 3 : 2);
                final int dateIndex = hoursIndex - 1;

                final String studentName = parts[0].trim();
                final String date = parts[dateIndex].trim();
                final int hours = Integer.parseInt(parts[hoursIndex].trim());
                final int creditsGiven = hasCredits && !parts[parts.length - 2].trim().isEmpty()
                    ? Integer.parseInt(parts[parts.length - 2].trim())
                    : 0;

                final StringBuilder subjectBuilder = new StringBuilder();
                for (int i = 1; i < dateIndex; i++) {
                    if (subjectBuilder.length() > 0) {
                        subjectBuilder.append(SEP);
                    }
                    subjectBuilder.append(parts[i].trim());
                }
                final String subject = subjectBuilder.toString();

                sessions.add(new CompletedSession(studentName, subject, date, hours, creditsGiven));
            }
        } catch (final IOException | RuntimeException e) {
            
        }

        return sessions;
    }
}
