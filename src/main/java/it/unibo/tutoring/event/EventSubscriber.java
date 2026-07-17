package it.unibo.tutoring.event;

public interface EventSubscriber {
    void onEvent(DomainEvent event);
}
