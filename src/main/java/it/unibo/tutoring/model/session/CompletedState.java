package it.unibo.tutoring.model.session;

public class CompletedState
    implements SessionState {

    @Override
    public void conferma(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "La sessione e' gia' completata."
        );
    }

    @Override
    public void annulla(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "Impossibile annullare una sessione completata."
        );
    }

    @Override
    public void completa(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "La sessione e' gia' completata."
        );
    }
}