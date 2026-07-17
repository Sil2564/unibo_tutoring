package it.unibo.tutoring.model.credit;


import java.util.HashMap;
import java.util.Map;

import it.unibo.tutoring.event.DomainEvent;
import it.unibo.tutoring.event.EventSubscriber;
import it.unibo.tutoring.event.SessionCompletedEvent;

public final class CreditService implements EventSubscriber {

    private static final int HOURS_PER_CREDIT = 4;
    private final CreditDao creditDao;
    private final BadgePolicy badgePolicy;

    public CreditService(final CreditDao creditDao, final BadgePolicy badgePolicy) {
        this.creditDao = creditDao;
        this.badgePolicy = badgePolicy;
    }

    public CreditRecord getCreditRecord(final String matricola) {
        final int totalHours = getUserHours(matricola);
        final int totalCredits = totalHours / HOURS_PER_CREDIT;
        final Badge badge = badgePolicy.calculateBadge(totalHours);
        final int nextLevel = badgePolicy.getNextThreshold(totalHours);

        return new CreditRecord(
            totalHours,
            totalCredits,
            badge,
            nextLevel
        );
    }

    public void addCompletedHours(final String matricola, final int hours) {
        final int currentHours = getUserHours(matricola);
        creditDao.saveUserHours(matricola, currentHours + hours);
    }

    @Override
    public void onEvent(final DomainEvent event) {
        if (event instanceof SessionCompletedEvent) {
            final SessionCompletedEvent e = (SessionCompletedEvent) event;
            addCompletedHours(e.getTutorMatricola(), e.getCompletedHours());
        }
    }

    private int getUserHours(final String matricola) {
        return creditDao.getUserHours(matricola);
    }
}