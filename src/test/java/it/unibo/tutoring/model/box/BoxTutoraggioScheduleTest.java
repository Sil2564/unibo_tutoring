package it.unibo.tutoring.model.box;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class BoxTutoraggioScheduleTest {

    private static final String AUTHOR = "1000000001";
    private static final String CANDIDATE = "1000000002";

    @Test
    void shouldAllowScheduleChangesWithoutCandidates() {
        final BoxTutoraggio box = createBox();

        assertTrue(box.puoModificareProgrammazione());

        box.aggiornaProgrammazione(
                AUTHOR,
                LocalDate.of(2026, 10, 12),
                LocalTime.of(16, 30),
                3);

        assertEquals(LocalDate.of(2026, 10, 12), box.getData());
        assertEquals(LocalTime.of(16, 30), box.getOra());
        assertEquals(3, box.getDurataOre());
    }

    @Test
    void aChatContactShouldNotLockTheSchedule() {
        final BoxTutoraggio box = createBox();

        box.aggiungiContatto(CANDIDATE);

        assertTrue(box.puoModificareProgrammazione());
    }

    @Test
    void shouldLockScheduleFromTheFirstActiveCandidacy() {
        final BoxTutoraggio box = createBox();
        box.aggiungiCandidato(CANDIDATE);

        assertFalse(box.puoModificareProgrammazione());
        assertThrows(
                IllegalStateException.class,
                () -> box.aggiornaProgrammazione(
                        AUTHOR,
                        LocalDate.of(2026, 10, 12),
                        LocalTime.of(16, 30),
                        3));

        box.rimuoviCandidato(CANDIDATE);
        assertTrue(box.puoModificareProgrammazione());
    }

    @Test
    void confirmedSessionShouldRemainLockedWhenCandidateLeavesPendingList() {
        final BoxTutoraggio box = createBox();
        box.aggiungiCandidato(CANDIDATE);
        box.confermaCandidato(CANDIDATE);

        assertTrue(box.getCandidati().isEmpty());
        assertFalse(box.puoModificareProgrammazione());
        assertThrows(
                IllegalStateException.class,
                () -> box.aggiornaProgrammazione(
                        AUTHOR,
                        LocalDate.of(2026, 10, 12),
                        LocalTime.of(16, 30),
                        3));
    }

    @Test
    void shouldRejectInvalidScheduleValues() {
        final BoxTutoraggio box = createBox();

        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, null, LocalTime.NOON, 2));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, LocalDate.now(), null, 2));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, LocalDate.now(), LocalTime.NOON, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, LocalDate.now(), LocalTime.NOON, 9));
    }

    @Test
    void shouldRejectScheduleChangesFromAnotherUser() {
        final BoxTutoraggio box = createBox();

        assertThrows(
                SecurityException.class,
                () -> box.aggiornaProgrammazione(
                        CANDIDATE,
                        LocalDate.of(2026, 10, 12),
                        LocalTime.of(16, 30),
                        3));
    }

    private static BoxTutoraggio createBox() {
        return new BoxTutoraggioImpl(
                "Ripasso PSS",
                "Informatica",
                "PSS",
                "Design pattern",
                LocalDate.of(2026, 10, 10),
                LocalTime.of(15, 0),
                2,
                AUTHOR,
                BoxType.OFFER);
    }
}
