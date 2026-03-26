package it.unibo.tutoring.model.session;

public interface SessionState {
    void conferma(TutoringSession session);
    void annulla(TutoringSession session);
    void completa(TutoringSession session);
}