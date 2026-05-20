package it.unibo.tutoring.controller.session;

import java.time.Duration;
import java.time.LocalDateTime;

import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.model.session.TutoringSessionImpl;
import it.unibo.tutoring.model.credit.CreditService;

public class TutoringSessionController {

    private final TutoringSession model;
    private final String nomeInserzionista;
    private final boolean tutorOffer;
    private final String tutorMatricola;

    public TutoringSessionController() {
        this("Progettazione e Sviluppo del Software", "Mario Rossi", true, "0000000");
    }

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String tutorMatricola) {

        this.model = new TutoringSessionImpl(
                materia,
                LocalDateTime.now(),
                Duration.ofHours(2),
                tutorMatricola);

        this.nomeInserzionista = nomeInserzionista;
        this.tutorOffer = tutorOffer;
        this.tutorMatricola = tutorMatricola;
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