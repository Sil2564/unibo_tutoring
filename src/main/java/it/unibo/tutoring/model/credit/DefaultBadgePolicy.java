package it.unibo.tutoring.model.credit;

/**
 * Aggiunto da Niki: Implementazione concreta della BadgePolicy (Strategy Pattern).
 * Definisce le regole di business fisse per l'assegnazione dei badge 
 * in base al monte ore accumulato.
 */
public class DefaultBadgePolicy implements BadgePolicy {

    // Soglie orarie per scattare di livello (20 ore per Intermediate, 50 per Expert)
    private static final int EXPERT_THRESHOLD = 50;
    private static final int INTERMEDIATE_THRESHOLD = 20;

    /**
     * @calculateBadge restituisce il badge appropriato calcolato sulle ore attuali.
     */
    @Override
    public Badge calculateBadge(final int totalHours) {
        if (totalHours >= EXPERT_THRESHOLD) {
            return Badge.EXPERT;
        }
        if (totalHours >= INTERMEDIATE_THRESHOLD) {
            return Badge.INTERMEDIATE;
        }
        return Badge.BEGINNER;
    }

    /**
     * @getNextThreshold restituisce le ore totali necessarie per il prossimo scatto.
     * Utilizzato dalla vista Statistiche/Profilo per mostrare la barra di progresso.
     */
    @Override
    public int getNextThreshold(final int totalHours) {
        if (totalHours < INTERMEDIATE_THRESHOLD) {
            return INTERMEDIATE_THRESHOLD;
        }
        if (totalHours < EXPERT_THRESHOLD) {
            return EXPERT_THRESHOLD;
        }
        /*
         * Se l'utente è già al livello massimo (Expert), restituiamo le ore correnti
         * affinché la progress bar mostri il 100% di completamento matematico.
         */
        return totalHours;
    }
}
