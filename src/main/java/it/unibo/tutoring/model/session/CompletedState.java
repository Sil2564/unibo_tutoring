package it.unibo.tutoring.model.session;

public class CompletedState implements SessionState {

    @Override
    public void conferma(TutoringSession session) {

        throw new IllegalStateException(
            "La sessione è già confermata."
        );
    }

    @Override
    public void annulla(TutoringSession session) {

        // TODO non l'ho inserito io il cancel per non rovinare logica lato sessioni/chat che penso possa essere cosa  correlata.
    }

    @Override
    public void completa(TutoringSession session) {

        if (session instanceof TutoringSessionImpl) {

            ((TutoringSessionImpl) session)
                .setStatoCorrente(
                    new CompletedState()
                );
        }
    }
}