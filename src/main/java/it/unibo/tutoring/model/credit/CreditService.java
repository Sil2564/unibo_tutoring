package it.unibo.tutoring.model.credit;

public final class CreditService {

    private static final int HOURS_PER_CREDIT = 4;

    private CreditService() {
    }

    public static CreditRecord getCreditRecord(
        final String matricola
    ) {

        /*
         * MOCK TEMPORANEO
         * In futuro questi dati arriveranno
         * dalle sessioni completate.
         */
        final int totalHours = getMockHours(matricola);

        final int totalCredits =
            totalHours / HOURS_PER_CREDIT;

        final Badge badge =
            calculateBadge(totalHours);

        return new CreditRecord(
            totalHours,
            totalCredits,
            badge
        );
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

    private static int getMockHours(
        final String matricola
    ) {

        /*
         * Mock temporaneo variabile
         * per simulare utenti diversi.
         */

        return Math.abs(
            matricola.hashCode()
        ) % 60;
    }
}