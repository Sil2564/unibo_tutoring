package it.unibo.tutoring.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainEventBus {

    private final Map<Class<? extends DomainEvent>, List<EventSubscriber>> subscribers = new HashMap<>();

    public void subscribe(final Class<? extends DomainEvent> eventType, final EventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    public void publish(final DomainEvent event) {
        final List<EventSubscriber> eventSubscribers = subscribers.get(event.getClass());
        if (eventSubscribers != null) {
            for (final EventSubscriber subscriber : eventSubscribers) {
                subscriber.onEvent(event);
            }
        }
    }
}
