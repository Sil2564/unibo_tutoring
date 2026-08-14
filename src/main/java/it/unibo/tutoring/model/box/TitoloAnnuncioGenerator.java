package it.unibo.tutoring.model.box;

/**
 * Genera il titolo standard di un annuncio in base al tipo e alla materia,
 * cosi' che la regola sia definita in un unico posto e resti sempre coerente
 * sia per i nuovi annunci sia per la normalizzazione di quelli gia' esistenti:
 * - "Ripetizioni di [materia] (Tutor)" per chi offre tutoraggio;
 * - "Aiuto con [materia] (Studente)" per chi lo cerca.
 */
public final class TitoloAnnuncioGenerator {

    private TitoloAnnuncioGenerator() {
    }

    public static String generaTitolo(final BoxType tipo, final String materia) {
        final String materiaSicura = materia == null || materia.isBlank() ? "materia da definire" : materia.trim();
        if (tipo == BoxType.OFFER) {
            return "Ripetizioni di " + materiaSicura + " (Tutor)";
        }
        return "Aiuto con " + materiaSicura + " (Studente)";
    }
}