package it.unibo.tutoring.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggiunto da Niki: Gestore centrale degli eventi di dominio (Observer Pattern).
 * Disaccoppia la logica di business (es. le sessioni) dalle conseguenze
 * che ne scaturiscono (es. l'assegnazione dei crediti).
 */
public class DomainEventBus {

    // Mappa che associa ad ogni tipo di evento la lista dei suoi ascoltatori (subscribers)
    private final Map<Class<? extends DomainEvent>, List<EventSubscriber>> subscribers = new HashMap<>();

    /**
     * @subscribe registra un nuovo subscriber per un determinato tipo di evento.
     * Se la lista non esiste ancora per quel tipo, la crea dinamicamente.
     */
    public void subscribe(final Class<? extends DomainEvent> eventType, final EventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    /**
     * @publish pubblica un evento sul bus.
     * Recupera tutti i subscriber registrati per quel tipo specifico di evento
     * e invoca il loro metodo onEvent() in modo sincrono.
     */
    public void publish(final DomainEvent event) {
        final List<EventSubscriber> eventSubscribers = subscribers.get(event.getClass());
        if (eventSubscribers != null) {
            for (final EventSubscriber subscriber : eventSubscribers) {
                subscriber.onEvent(event);
            }
        }
    }
}
