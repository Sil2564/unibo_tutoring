package it.unibo.tutoring.model.chat;

import java.time.LocalDateTime;

public interface Message {
    String getTesto();
    String getIdMittente();
    LocalDateTime getTimestamp();
}
