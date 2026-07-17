package it.unibo.tutoring.model.credit;

import java.util.Optional;

import it.unibo.tutoring.event.DomainEvent;
import it.unibo.tutoring.event.EventSubscriber;
import it.unibo.tutoring.event.SessionCompletedEvent;

public final class CreditService implements EventSubscriber {

    private static final int HOURS_PER_CREDIT = 2;
    private final BadgePolicy badgePolicy;

    public CreditService(final BadgePolicy badgePolicy) {
        this.badgePolicy = badgePolicy;
    }

    public CreditRecord getCreditRecord(final String matricola) {
        // try repository first
        final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
        if (stored.isPresent()) {
            final CreditRecord rec = stored.get();
            final int nextLevel = badgePolicy.getNextThreshold(rec.getTotalHours());
            return new CreditRecord(
                rec.getTotalHours(),
                rec.getTotalCredits(),
                badgePolicy.calculateBadge(rec.getTotalHours()),
                rec.getRating(),
                nextLevel
            );
        }

        // default new record
        final int totalHours = 0;
        final int totalCredits = totalHours / HOURS_PER_CREDIT;
        final Badge badge = badgePolicy.calculateBadge(totalHours);
        final int nextLevel = badgePolicy.getNextThreshold(totalHours);
        final double rating = 0.0;

        final CreditRecord record = new CreditRecord(
            totalHours,
            totalCredits,
            badge,
            rating,
            nextLevel
        );

        CreditRepository.saveRecord(matricola, record);
        return record;
    }

    public void addCompletedHours(final String matricola, final int hours) {
        // carica il record esistente (se presente), calcola i nuovi valori e salva
        final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
        final int currentHours = stored.map(CreditRecord::getTotalHours).orElse(0);
        final int newTotal = currentHours + hours;
        final int newCredits = newTotal / HOURS_PER_CREDIT;
        final Badge newBadge = badgePolicy.calculateBadge(newTotal);
        final int nextLevel = badgePolicy.getNextThreshold(newTotal);
        final double rating = stored.map(CreditRecord::getRating).orElse(0.0);

        final CreditRecord updated = new CreditRecord(
            newTotal,
            newCredits,
            newBadge,
            rating,
            nextLevel
        );

        CreditRepository.saveRecord(matricola, updated);
    }

    @Override
    public void onEvent(final DomainEvent event) {
        if (event instanceof SessionCompletedEvent) {
            final SessionCompletedEvent e = (SessionCompletedEvent) event;
            addCompletedHours(e.getTutorMatricola(), e.getCompletedHours());
        }
    }
}