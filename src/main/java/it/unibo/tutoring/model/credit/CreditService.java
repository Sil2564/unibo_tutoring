package it.unibo.tutoring.model.credit;

import java.util.Optional;

import it.unibo.tutoring.event.DomainEvent;
import it.unibo.tutoring.event.EventSubscriber;
import it.unibo.tutoring.event.SessionCompletedEvent;

/**
 * Aggiunto da Niki: Servizio di dominio per la gestione dei crediti e dei badge dei tutor.
 * Implementa l'Observer Pattern iscrivendosi al DomainEventBus per ascoltare
 * gli eventi di completamento delle sessioni di tutoraggio.
 */
public final class CreditService implements EventSubscriber {

    // Costante che definisce il tasso di conversione ore -> CFU (1 CFU = 25 ore)
    private static final int HOURS_PER_CREDIT = 25;
    
    // Strategia (Strategy Pattern) per il calcolo dei badge in base alle ore
    private final BadgePolicy badgePolicy;

    /**
     * Inizializza il servizio iniettando la policy dei badge.
     * @param badgePolicy la strategia per calcolare i livelli di badge.
     */
    public CreditService(final BadgePolicy badgePolicy) {
        this.badgePolicy = badgePolicy;
    }

    /**
     * Recupera il record dei crediti di un tutor. Se non esiste, ne crea uno di default.
     * Ricalcola sempre dinamicamente i crediti e il badge in base alle ore totali
     * per garantire consistenza.
     * 
     * @param matricola la matricola del tutor.
     * @return il CreditRecord aggiornato.
     */
    public CreditRecord getCreditRecord(final String matricola) {
        // Tenta il caricamento dal repository persistente
        final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
        if (stored.isPresent()) {
            final CreditRecord rec = stored.get();
            
            /* Ricalcola dinamicamente la soglia successiva e i crediti,
             * applicando la formula ore / 25
             */
            final int nextLevel = badgePolicy.getNextThreshold(rec.getTotalHours());
            final int recalculatedCredits = rec.getTotalHours() / HOURS_PER_CREDIT;
            
            return new CreditRecord(
                rec.getTotalHours(),
                recalculatedCredits,
                badgePolicy.calculateBadge(rec.getTotalHours()),
                rec.getRating(),
                nextLevel
            );
        }

        // Se non esiste, crea un record vergine (0 ore, 0 CFU, badge iniziale)
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

        // Salva il nuovo record di default nel file CSV
        CreditRepository.saveRecord(matricola, record);
        return record;
    }

    /**
     * Aggiunge le ore di tutoraggio completate al monte ore del tutor,
     * aggiornando crediti e badge.
     * 
     * @param matricola la matricola del tutor.
     * @param hours le ore della sessione appena completata.
     */
    public void addCompletedHours(final String matricola, final int hours) {
        // Carica il record esistente o usa 0 come default
        final Optional<CreditRecord> stored = CreditRepository.loadRecord(matricola);
        final int currentHours = stored.map(CreditRecord::getTotalHours).orElse(0);
        
        // Calcola i nuovi totali
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

        // Salva il record aggiornato nel file CSV
        CreditRepository.saveRecord(matricola, updated);
    }

    /**
     * Implementazione del metodo Subscriber per l'Observer Pattern.
     * Ascolta gli eventi lanciati nel DomainEventBus.
     * 
     * @param event l'evento generato dal dominio.
     */
    @Override
    public void onEvent(final DomainEvent event) {
        // Se l'evento è un completamento di sessione, accredita le ore
        if (event instanceof SessionCompletedEvent) {
            final SessionCompletedEvent e = (SessionCompletedEvent) event;
            addCompletedHours(e.getTutorMatricola(), e.getCompletedHours());
        }
    }
}