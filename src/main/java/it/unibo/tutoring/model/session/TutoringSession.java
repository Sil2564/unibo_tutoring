package it.unibo.tutoring.model.session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TutoringSession {
    UUID getId();
    String getMateria();
    LocalDateTime getDataOra();
    Duration getDurata();

    SessionState getStatoCorrente();


    void conferma();
    void annulla();
    void completa();
}