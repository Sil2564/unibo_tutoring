package it.unibo.tutoring.model.session;

public class ConfirmedState implements SessionState {

    @Override
    public void conferma(TutoringSession session) {
        throw new IllegalStateException("La sessione è già confermata.");
    }

  @Override
public void annulla(
    final TutoringSession session
) {

    if (
        session instanceof
        TutoringSessionImpl tutoringSession
    ) {

        tutoringSession.setStatoCorrente(
            new CancelledState()
        );
    }
}

   @Override
public void completa(
    final TutoringSession session
) {

    if (
        session instanceof
        TutoringSessionImpl tutoringSession
    ) {

        final int completedHours =
            (int) tutoringSession
                .getDurata()
                .toHours();

        it.unibo.tutoring.model.credit
            .CreditService
            .addCompletedHours(
                tutoringSession
                    .getTutorMatricola(),
                completedHours
            );

        tutoringSession.setStatoCorrente(
            new CompletedState()
        );
    }
}
}