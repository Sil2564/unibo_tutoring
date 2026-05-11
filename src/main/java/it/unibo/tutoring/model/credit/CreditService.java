package it.unibo.tutoring.model.credit;

public final class CreditService {

    private CreditService() {
    }

    public static CreditRecord getCreditRecord(final String matricola) {

        final int totalHours = 12;
        final int totalCredits = 3;

        final Badge badge;

        if (totalHours >= 50) {
            badge = Badge.EXPERT;
        } else if (totalHours >= 20) {
            badge = Badge.INTERMEDIATE;
        } else {
            badge = Badge.BEGINNER;
        }

        return new CreditRecord(
            totalHours,
            totalCredits,
            badge
        );
    }
}