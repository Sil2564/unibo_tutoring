package it.unibo.tutoring.model.credit;

/**
 *
 * Modifiche effettuate: esteso con il campo {@code rating} (double) per
 * memorizzare la valutazione dell'account. Il costruttore e i relativi getter
 * sono stati aggiornati di conseguenza.
 */
public final class CreditRecord {

    private final int totalHours;
    private final int totalCredits;
    private final Badge badge;
    private final int nextLevelHours;
    private final double rating; // valutazione dell'account

    /**
     * Crea un nuovo CreditRecord.
     *
     * @param totalHours ore totali completate
     * @param totalCredits crediti totali calcolati
     * @param badge badge calcolato a seconda delle ore
     * @param rating rating dell'account (double)
     * @param nextLevelHours ore necessarie per il prossimo livello
     */
    public CreditRecord(
        final int totalHours,
        final int totalCredits,
        final Badge badge,
        final double rating,
        final int nextLevelHours
    ) {
        this.totalHours = totalHours;
        this.totalCredits = totalCredits;
        this.badge = badge;
        this.rating = rating;
        this.nextLevelHours = nextLevelHours;
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

    public int getNextLevelHours() {
        return this.nextLevelHours;
    }

    public double getRating() {
        return this.rating;
    }
}