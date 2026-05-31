package it.unibo.tutoring.model.credit;


import java.util.Optional;

public final class CreditService {

    /**
     * Service che espone operazioni sui crediti. Refactoring effettuato:
     * - La persistenza dei crediti è stata estratta in `CreditRepository`.
     * - `getCreditRecord` ora legge dal repository; se il record non esiste
     *   crea uno nuovo con valori di default.
     * - `addCompletedHours` aggiorna il record sul repository (calcola i
     *   crediti e il badge in base alle ore) invece di usare una mappa in-memory.
     */

    private static final int HOURS_PER_CREDIT = 4;
    

    private CreditService() {
    }

    public static CreditRecord getCreditRecord(
        final String matricola
    ) {
        // try repository first
        final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
        if (stored.isPresent()) {
            return stored.get();
        }

        // default new record
        final int totalHours = 0;
        final int totalCredits = totalHours / HOURS_PER_CREDIT;
        final Badge badge = calculateBadge(totalHours);
        final double rating = 0.0;

        final CreditRecord record = new CreditRecord(
            totalHours,
            totalCredits,
            badge,
            rating
        );

        CreditRepository.saveRecord(matricola, record);
        return record;
    }

    public static void addCompletedHours(
    final String matricola,
    final int hours
) {
    // carica il record esistente (se presente), calcola i nuovi valori e salva
    final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
    final int currentHours = stored.map(CreditRecord::getTotalHours).orElse(0);
    final int newTotal = currentHours + hours;
    final int newCredits = newTotal / HOURS_PER_CREDIT;
    final Badge newBadge = calculateBadge(newTotal);
    final double rating = stored.map(CreditRecord::getRating).orElse(0.0);

    final CreditRecord updated = new CreditRecord(
        newTotal,
        newCredits,
        newBadge,
        rating
    );

    CreditRepository.saveRecord(matricola, updated);
}

    private static Badge calculateBadge(
        final int totalHours
    ) {

        if (totalHours >= 50) {
            return Badge.EXPERT;
        }

        if (totalHours >= 20) {
            return Badge.INTERMEDIATE;
        }

        return Badge.BEGINNER;
    }

    private static int getUserHours(
    final String matricola
) {
    final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
    return stored.map(CreditRecord::getTotalHours).orElse(0);
}
}