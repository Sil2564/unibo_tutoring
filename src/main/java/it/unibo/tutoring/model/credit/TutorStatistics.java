package it.unibo.tutoring.model.credit;

import java.util.List;
import java.util.Objects;

import it.unibo.tutoring.model.credit.CompletedSessionRepository.CompletedSession;

/**
 * Riepilogo delle statistiche del tutor. Ore e crediti provengono dal record
 * cumulativo; lo storico delle sessioni viene usato soltanto per il conteggio.
 */
public record TutorStatistics(int totalHours, int totalCredits, int totalSessions) {

    public static TutorStatistics from(
            final CreditRecord creditRecord,
            final List<CompletedSession> completedSessions
    ) {
        Objects.requireNonNull(creditRecord, "creditRecord");
        Objects.requireNonNull(completedSessions, "completedSessions");

        return new TutorStatistics(
                creditRecord.getTotalHours(),
                creditRecord.getTotalCredits(),
                completedSessions.size()
        );
    }
}
