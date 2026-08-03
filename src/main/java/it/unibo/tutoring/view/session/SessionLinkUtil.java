package it.unibo.tutoring.view.session;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.UserAccount;
import it.unibo.tutoring.controller.session.TutoringSessionController;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.box.BoxType;

/**
 * Helper condiviso tra Dashboard, Dettaglio Annuncio e Dettaglio Sessione per
 * costruire in modo coerente il {@link TutoringSessionController} legato ad
 * una specifica coppia (autore annuncio, controparte), indipendentemente da
 * chi dei due sta effettivamente guardando la pagina in quel momento.
 */
public final class SessionLinkUtil {

    private SessionLinkUtil() {
    }

    /**
     * Restituisce la matricola della "controparte" rispetto a chi sta
     * guardando: se chi guarda e' l'autore dell'annuncio, la controparte e'
     * il candidato confermato (puo' essere null se non ancora confermato);
     * altrimenti la controparte e' sempre l'autore dell'annuncio.
     */
    public static String controparteDi(final BoxTutoraggio box, final String viewerMatricola) {
        if (viewerMatricola != null && viewerMatricola.equals(box.getAutoreMatricola())) {
            return box.getConfermato();
        }
        return box.getAutoreMatricola();
    }

    /**
     * Costruisce il controller per la coppia (box, controparte), valido sia
     * che chi guarda sia l'autore dell'annuncio sia che sia la controparte
     * stessa (candidato/confermato). Tutte e due le costruzioni per una
     * stessa coppia risolvono sempre allo stesso file di sessione condiviso.
     */
    public static TutoringSessionController buildController(
        final BoxTutoraggio box,
        final String counterpartyMatricola,
        final String viewerMatricola
    ) {
        final boolean counterpartyIsAuthor = counterpartyMatricola.equals(box.getAutoreMatricola());

        // tutorOfferParam indica se la CONTROPARTE (non chi guarda) offre tutoraggio.
        final boolean tutorOfferParam = counterpartyIsAuthor
            ? box.getTipo() == BoxType.OFFER
            : box.getTipo() == BoxType.REQUEST;

        return new TutoringSessionController(
            box.getMateria(),
            nomeCompleto(counterpartyMatricola),
            tutorOfferParam,
            counterpartyMatricola,
            viewerMatricola
        );
    }

    public static String nomeCompleto(final String matricola) {
        if (matricola == null || matricola.isBlank()) {
            return "Utente sconosciuto";
        }
        final UserAccount user = AuthService.getInstance().getUser(matricola);
        return user != null ? user.getName() + " " + user.getSurname() : matricola;
    }
}