package it.unibo.tutoring.model.credit;

public final class CreditRecord {

    private final int totalHours;
    private final int totalCredits;
    private final Badge badge;
    public CreditRecord(
        final int totalHours,
        final int totalCredits,
        final Badge badge
    ) {
        this.totalHours = totalHours;
        this.totalCredits = totalCredits;
        this.badge = badge;
    }

    public int getTotalHours() {
        return this.totalHours;
    }

    public int getTotalCredits() {
        return this.totalCredits;
    }

    public Badge getBadge() {
        return this.badge;
    }
}