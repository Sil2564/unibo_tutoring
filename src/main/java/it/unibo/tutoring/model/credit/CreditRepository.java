package it.unibo.tutoring.model.credit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class CreditRepository {

    private static final Path DB = Paths.get("data", "credits.csv");
    private static final String SEP = ";";

    private CreditRepository() {
    }

    /**
     * Carica il record associato alla matricola, se presente.
     *
     * @param matricola id utente
     * @return Optional con il CreditRecord, vuoto se non presente o in caso di errori
     */
    public static Optional<CreditRecord> loadRecord(final String matricola) {
        try {
            if (!Files.exists(DB)) {
                return Optional.empty();
            }

            final List<String> lines = Files.readAllLines(DB);
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                if (line.startsWith("matricola")) {
                    continue; // header
                }

                final String[] parts = line.split(SEP, -1);
                if (parts.length < 7) {
                    continue;
                }

                if (parts[0].equals(matricola)) {
                    final int totalHours = Integer.parseInt(parts[3]);
                    final int totalCredits = Integer.parseInt(parts[4]);
                    final Badge badge = Badge.valueOf(parts[5]);
                    final double rating = Double.parseDouble(parts[6]);

                    return Optional.of(new CreditRecord(
                        totalHours,
                        totalCredits,
                        badge,
                        rating
                    ));
                }
            }
        } catch (final IOException | RuntimeException e) {
        }

        return Optional.empty();
    }

    /**
     * Salva o aggiorna il record per la matricola fornita. Se esiste una riga
     * precedente per la matricola, ne preserva i campi `firstName`/`lastName`.
     * L'implementazione riscrive l'intero file con header.
     * @param matricola id utente
     * @param record dati da salvare
     */
    public static synchronized void saveRecord(
        final String matricola,
        final CreditRecord record
    ) {
        try {
            final List<String> lines = Files.exists(DB)
                ? new ArrayList<>(Files.readAllLines(DB))
                : new ArrayList<>();

            final Map<String, String> map = new LinkedHashMap<>();
            // preserve existing order and names
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                if (line.startsWith("matricola")) {
                    continue;
                }
                final String[] parts = line.split(SEP, -1);
                if (parts.length >= 1) {
                    map.put(parts[0], line);
                }
            }

            String first = "";
            String last = "";
            final String existing = map.get(matricola);
            if (existing != null) {
                final String[] p = existing.split(SEP, -1);
                if (p.length >= 3) {
                    first = p[1];
                    last = p[2];
                }
            }

            final String newLine = String.join(SEP,
                matricola,
                first,
                last,
                Integer.toString(record.getTotalHours()),
                Integer.toString(record.getTotalCredits()),
                record.getBadge().name(),
                Double.toString(record.getRating())
            );

            map.put(matricola, newLine);

            final List<String> out = new ArrayList<>();
            out.add("matricola;firstName;lastName;totalHours;totalCredits;badge;rating");
            out.addAll(map.values());

            if (DB.getParent() != null) {
                Files.createDirectories(DB.getParent());
            }

            Files.write(DB, out);
        } catch (final IOException e) {
        }
    }
}
