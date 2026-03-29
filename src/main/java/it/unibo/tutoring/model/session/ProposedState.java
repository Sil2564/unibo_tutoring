package it.unibo.tutoring.model.session;

public class ProposedState implements SessionState {

    @Override
    public void conferma(TutoringSession session) {
        // Se la sessione è proposta e viene confermata, passa allo stato Confirmed
        if (session instanceof TutoringSessionImpl) {
            ((TutoringSessionImpl) session).setStatoCorrente(new ConfirmedState());
        }
    }

    @Override
    public void annulla(TutoringSession session) {
        // TODO
    }

    @Override
    public void completa(TutoringSession session) {
        throw new IllegalStateException("Impossibile completare una sessione che è solo proposta.");
    }
}