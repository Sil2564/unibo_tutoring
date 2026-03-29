package it.unibo.tutoring.model.chat;

import java.util.List;

public interface Chat {

    void aggiungiMessaggio(Message msg);

    List<Message> getStoricoMessaggi();

    // Metodo del pattern Observer
    void addObserver(ChatObserver observer);
}