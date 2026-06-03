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
                final String date = parts[parts.length - 3].trim();
                final int stars = Integer.parseInt(parts[parts.length - 2].trim());
                final StringBuilder subjectBuilder = new StringBuilder();
                for (int i = 1; i <= parts.length - 4; i++) {
                    if (subjectBuilder.length() > 0) {
                        subjectBuilder.append(SEP);
                    }
                    subjectBuilder.append(parts[i].trim());
                }
                final String subject = subjectBuilder.toString();
                final String comment = parts.length == 5 ? "" : String.join(SEP, java.util.Arrays.copyOfRange(parts, 4, parts.length - 1)).trim();

                reviews.add(new Review(reviewerName, subject, date, stars, comment));
            }
        } catch (final IOException | RuntimeException e) {
        }

        return reviews;
    }
}
