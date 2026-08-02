package it.unibo.tutoring.model.chat;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageImpl implements Message {

    private final String testo;
    private final String idMittente;
    private final LocalDateTime timestamp;

    public MessageImpl(final String testo, final String idMittente) {
        this(testo, idMittente, LocalDateTime.now());
    }

    public MessageImpl(final String testo, final String idMittente,
            final LocalDateTime timestamp) {
        this.testo = Objects.requireNonNull(testo);
        this.idMittente = Objects.requireNonNull(idMittente);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    @Override
    public String getTesto() { return this.testo; }

    @Override
    public String getIdMittente() { return this.idMittente; }

    @Override
    public LocalDateTime getTimestamp() { return this.timestamp; }
}