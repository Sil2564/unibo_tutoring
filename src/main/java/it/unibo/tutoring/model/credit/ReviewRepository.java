package it.unibo.tutoring.model.credit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight repository per caricamento delle recensioni ricevute.
 * Legge il file data/reviews.csv e fornisce accesso ai record.
 */
public final class ReviewRepository {

    private static final Path DB = Paths.get("data", "reviews.csv");
    private static final String SEP = ";";

    private ReviewRepository() {
    }

    /**
     * Record semplice che rappresenta una recensione ricevuta.
     */
    public record Review(
        String reviewerName,
        String subject,
        String date,
        int stars,
        String comment
    ) {}

    /**
     * Carica tutte le recensioni ricevute per la matricola fornita.
     * @param matricola id del tutor che ha ricevuto la recensione
     * @return lista di Review per quella matricola
     */
    public static List<Review> loadReviewsForRecipient(final String matricola) {
        final List<Review> reviews = new ArrayList<>();

        try {
            if (!Files.exists(DB)) {
                return reviews;
            }

            final List<String> lines = Files.readAllLines(DB);
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty() || line.startsWith("reviewerName")) {
                    continue; // header
                }

                final String[] parts = line.split(SEP, -1);
                if (parts.length < 5) {
                    continue;
                }

                final String recipientMatricola = parts[parts.length - 1].trim();
                if (!recipientMatricola.equals(matricola)) {
                    continue;
                }

                final String reviewerName = parts[0].trim();
                final String subject = parts.length > 1 ? parts[1].trim() : "";
                final String date = parts.length > 2 ? parts[2].trim() : "";
                final int stars = parts.length > 3 && !parts[3].trim().isEmpty()
                    ? Integer.parseInt(parts[3].trim())
                    : 0;
                final String comment;
                if (parts.length <= 5) {
                    comment = "";
                } else {
                    comment = String.join(SEP, java.util.Arrays.copyOfRange(parts, 4, parts.length - 1)).trim();
                }

                reviews.add(new Review(reviewerName, subject, date, stars, comment));
            }
        } catch (final IOException e) {
            // ignora errori di lettura per non bloccare l'app
        }

        return reviews;
    }

    /**
     * Salva una recensione in coda al file data/reviews.csv, creando header e
     * cartella se necessario.
     *
     * @param reviewerName nome di chi lascia la recensione (lo studente)
     * @param subject materia della sessione
     * @param date data della sessione (stringa gia' formattata)
     * @param stars valutazione da 0 a 5
     * @param comment commento libero
     * @param recipientMatricola matricola del tutor che riceve la recensione
     */
    public static synchronized void saveReview(
        final String reviewerName,
        final String subject,
        final String date,
        final int stars,
        final String comment,
        final String recipientMatricola
    ) {
        try {
            if (DB.getParent() != null) {
                Files.createDirectories(DB.getParent());
            }

            final String header = "reviewerName;subject;date;stars;comment;tutorMatricola";
            if (!Files.exists(DB)) {
                Files.writeString(DB, header + System.lineSeparator(), java.nio.file.StandardOpenOption.CREATE_NEW);
            }

            final String line = String.join(SEP,
                sanitize(reviewerName),
                sanitize(subject),
                sanitize(date),
                Integer.toString(stars),
                sanitize(comment),
                sanitize(recipientMatricola)
            );
            Files.writeString(
                DB,
                line + System.lineSeparator(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            );
        } catch (final IOException e) {
            // ignora errori di scrittura per non bloccare l'app
        }
    }

    private static String sanitize(final String value) {
        return value == null ? "" : value.replace(SEP, ",").replace("\n", " ").replace("\r", " ").trim();
    }
}