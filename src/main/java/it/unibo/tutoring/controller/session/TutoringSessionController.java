package it.unibo.tutoring.controller.session;

import java.time.Duration;
import java.time.LocalDateTime;

import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.model.session.TutoringSessionImpl;

public class TutoringSessionController {

    // Il controller possiede un riferimento al Modello
    private final TutoringSession model;
    private final String tutorDisplayName;

    public TutoringSessionController() {
        this("Progettazione e Sviluppo del Software", "Mario Rossi");
    }

    public TutoringSessionController(final String materia, final String tutorDisplayName) {
        this.model = new TutoringSessionImpl(
                materia,
                LocalDateTime.now(),
                Duration.ofHours(2),
                "0000000");
        this.tutorDisplayName = tutorDisplayName;
    }

    public String getTutorDisplayName() {
        return this.tutorDisplayName;
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