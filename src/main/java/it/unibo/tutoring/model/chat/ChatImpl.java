package it.unibo.tutoring.model.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatImpl implements Chat {

    private final List<Message> storicoMessaggi;
    private final List<ChatObserver> observers;

    public ChatImpl() {
        this.storicoMessaggi = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public void aggiungiMessaggio(Message msg) {
        this.storicoMessaggi.add(msg);
        // Appena aggiungo un messaggio, avviso utenti nella lista
        notifyObservers(msg);
    }

    @Override
    public List<Message> getStoricoMessaggi() {
        // Restituisco una COPIA della lista per sicurezza
        return new ArrayList<>(this.storicoMessaggi);
    }

    @Override
    public void addObserver(ChatObserver observer) {
        this.observers.add(observer);
    }

    // Metodo per avvisare gli observer
    private void notifyObservers(Message msg) {
        for (ChatObserver obs : this.observers) {
            // Chiama il metodo sull'interfaccia grafica o sul test
            obs.onNewMessage(msg);
        }
    }
}