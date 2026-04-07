package it.unibo.tutoring.model.session;

import it.unibo.tutoring.model.chat.Message;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public interface TutoringSession {
    UUID getId();
    String getMateria();
    LocalDateTime getDataOra();
    Duration getDurata();

    SessionState getStatoCorrente();

    void conferma();
    void annulla();
    void completa();

    // PATTERN FACADE: Metodi delegati alla Chat
    void inviaMessaggio(String testo, String idMittente);

    List<Message> getStoricoChat();
}