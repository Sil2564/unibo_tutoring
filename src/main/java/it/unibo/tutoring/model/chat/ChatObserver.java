package it.unibo.tutoring.model.chat;

public interface ChatObserver {
    void onNewMessage(Message message);
}