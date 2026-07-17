package it.unibo.tutoring;

import it.unibo.tutoring.event.DomainEventBus;
import it.unibo.tutoring.event.SessionCompletedEvent;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.credit.DefaultBadgePolicy;
import it.unibo.tutoring.model.credit.FileCreditDao;

public final class AppConfig {

    private static AppConfig instance;

    private final DomainEventBus eventBus;
    private final CreditService creditService;

    private AppConfig() {
        // Initialize Event Bus
        this.eventBus = new DomainEventBus();

        // Initialize Services and Daos
        final FileCreditDao creditDao = new FileCreditDao();
        final DefaultBadgePolicy badgePolicy = new DefaultBadgePolicy();
        this.creditService = new CreditService(creditDao, badgePolicy);

        // Wiring: Subscriptions
        this.eventBus.subscribe(SessionCompletedEvent.class, this.creditService);
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public DomainEventBus getEventBus() {
        return eventBus;
    }

    public CreditService getCreditService() {
        return creditService;
    }
}
