package it.unibo.tutoring.model.chat;

import java.time.LocalDateTime;

public class MessageImpl implements Message {

    private final String testo;
    private final String idMittente;
    private final LocalDateTime timestamp;

    public MessageImpl(String testo, String idMittente) {
        this.testo = testo;
        this.idMittente = idMittente;
        // Salva l'ora esatta in cui viene creato
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String getTesto() { return this.testo; }

    @Override
    public String getIdMittente() { return this.idMittente; }

    @Override
    public LocalDateTime getTimestamp() { return this.timestamp; }
}