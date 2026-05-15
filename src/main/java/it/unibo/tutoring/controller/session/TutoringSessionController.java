package it.unibo.tutoring.controller.session;

import java.time.Duration;
import java.time.LocalDateTime;

import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.model.session.TutoringSessionImpl;

public class TutoringSessionController {

    private final TutoringSession model;
    private final String nomeInserzionista;
    private final boolean tutorOffer;

    public TutoringSessionController() {
        this("Progettazione e Sviluppo del Software", "Mario Rossi", true);
    }

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer) {
        this.model = new TutoringSessionImpl(
                materia,
                LocalDateTime.now(),
                Duration.ofHours(2),
                "0000000");
        this.nomeInserzionista = nomeInserzionista;
        this.tutorOffer = tutorOffer;
    }

    public String getRuoloInserzionista() {
        return this.tutorOffer ? "Tutor:" : "Studente:";
    }

    public String getNomeInserzionista() {
        return this.nomeInserzionista;
    }

    public TutoringSession getModel() {
        return this.model;
    }

    public void confermaSessione() {
        this.model.conferma();
    }

    public void completaSessione() {
        this.model.completa();
    }

    public void inviaMessaggio(String testo, String mittente) {
        if (testo != null && !testo.trim().isEmpty()) {
            this.model.inviaMessaggio(testo, mittente);
        }
    }
}