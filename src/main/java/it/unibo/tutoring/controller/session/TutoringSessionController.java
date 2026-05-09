package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.model.session.TutoringSessionImpl;

import java.time.Duration;
import java.time.LocalDateTime;

public class TutoringSessionController {

    // Il controller possiede un riferimento al Modello
    private final TutoringSession model;

    public TutoringSessionController() {
        this.model = new TutoringSessionImpl("Progettazione e Sviluppo del Software", LocalDateTime.now(), Duration.ofHours(2));
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