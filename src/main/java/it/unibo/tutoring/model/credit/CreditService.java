package it.unibo.tutoring.model.credit;


import java.util.HashMap;
import java.util.Map;

public final class CreditService {

    private static final int HOURS_PER_CREDIT = 4;
    private static final Map<String, Integer>
    USER_HOURS = new HashMap<>();

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
       final int totalHours =
    getUserHours(matricola);

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

    public static void addCompletedHours(
    final String matricola,
    final int hours
) {

    final int currentHours =
        getUserHours(matricola);

    USER_HOURS.put(
        matricola,
        currentHours + hours
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

    private static int getUserHours(
    final String matricola
) {

    return USER_HOURS.getOrDefault(
        matricola,
        0
    );
}
}