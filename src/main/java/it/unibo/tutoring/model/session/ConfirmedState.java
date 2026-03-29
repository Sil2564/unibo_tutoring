package it.unibo.tutoring.model.session;

public class ConfirmedState implements SessionState {

    @Override
    public void conferma(TutoringSession session) {
        throw new IllegalStateException("La sessione è già confermata.");
    }

    @Override
    public void annulla(TutoringSession session) {
        // TODO
    }

    @Override
    public void completa(TutoringSession session) {
        // TODO
    }
}