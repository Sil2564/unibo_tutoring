package it.unibo.tutoring.model.session;

public class CancelledState implements SessionState {

    @Override
    public void conferma(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "Impossibile confermare una sessione annullata."
        );
    }

    @Override
    public void annulla(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "La sessione è già annullata."
        );
    }

    @Override
    public void completa(
        final TutoringSession session
    ) {

        throw new IllegalStateException(
            "Impossibile completare una sessione annullata."
        );
    }
}