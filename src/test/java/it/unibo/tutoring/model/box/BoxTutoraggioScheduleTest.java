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

    /*
     * Le date sono sempre relative a oggi: con date fisse il test smetterebbe
     * di passare appena superate, perche' la programmazione non accetta
     * date/ore gia' trascorse.
     */
    private static final LocalDate INITIAL_DATE = LocalDate.now().plusDays(3);
    private static final LocalDate NEW_DATE = LocalDate.now().plusDays(5);

    @Test
    void shouldAllowScheduleChangesWithoutCandidates() {
        final BoxTutoraggio box = createBox();

        assertTrue(box.puoModificareProgrammazione());

        box.aggiornaProgrammazione(
                AUTHOR,
                NEW_DATE,
                LocalTime.of(16, 30),
                3);

        assertEquals(NEW_DATE, box.getData());
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
                        NEW_DATE,
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
                        NEW_DATE,
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
                () -> box.aggiornaProgrammazione(AUTHOR, NEW_DATE, null, 2));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, NEW_DATE, LocalTime.NOON, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(AUTHOR, NEW_DATE, LocalTime.NOON, 9));
        assertThrows(
                IllegalArgumentException.class,
                () -> box.aggiornaProgrammazione(
                        AUTHOR, LocalDate.now().minusDays(1), LocalTime.NOON, 2));
    }

    @Test
    void shouldRejectScheduleChangesFromAnotherUser() {
        final BoxTutoraggio box = createBox();

        assertThrows(
                SecurityException.class,
                () -> box.aggiornaProgrammazione(
                        CANDIDATE,
                        NEW_DATE,
                        LocalTime.of(16, 30),
                        3));
    }

    @Test
    void constructorShouldApplyTheSameRulesAsTheCreationForm() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createBoxWithDuration(0));
        assertThrows(
                IllegalArgumentException.class,
                () -> createBoxWithDuration(9));
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoxTutoraggioImpl(
                        "Titolo", "Ingegneria e scienze informatiche", "PSS", "MVC",
                        null, LocalTime.NOON, 2, AUTHOR, BoxType.OFFER));
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoxTutoraggioImpl(
                        "Titolo", " ", "PSS", "MVC",
                        INITIAL_DATE, LocalTime.NOON, 2, AUTHOR, BoxType.OFFER));
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoxTutoraggioImpl(
                        "Titolo", "Ingegneria e scienze informatiche", "PSS", "MVC",
                        INITIAL_DATE, LocalTime.NOON, 2, AUTHOR, null));
    }

    @Test
    void constructorShouldTrimTextFields() {
        final BoxTutoraggio box = new BoxTutoraggioImpl(
                "Ripetizioni di PSS (Tutor)",
                "  Ingegneria e scienze informatiche ",
                "  PSS ",
                " Design pattern  ",
                INITIAL_DATE,
                LocalTime.of(15, 0),
                2,
                AUTHOR,
                BoxType.OFFER,
                "  Porta il portatile \n");

        assertEquals("Ingegneria e scienze informatiche", box.getCorso());
        assertEquals("PSS", box.getMateria());
        assertEquals("Design pattern", box.getArgomento());
        assertEquals("Porta il portatile", box.getNote());
    }

    @Test
    void expirationShouldDependOnScheduledStart() {
        final BoxTutoraggio box = createBox();
        final var start = INITIAL_DATE.atTime(15, 0);

        assertFalse(box.isScaduto(start.minusSeconds(1)));
        assertFalse(box.isScaduto(start));
        assertTrue(box.isScaduto(start.plusSeconds(1)));
    }

    @Test
    void expiredAnnouncementShouldNotAcceptCandidates() {
        final BoxTutoraggio box = createExpiredBox();

        assertTrue(box.isScaduto());
        box.aggiungiCandidato(CANDIDATE);

        assertFalse(box.isCandidato(CANDIDATE));
    }

    @Test
    void expiredAnnouncementShouldNotConfirmPendingCandidates() {
        // Candidatura arrivata prima della scadenza, conferma tentata dopo.
        final BoxTutoraggio box = new BoxTutoraggioImpl(
                java.util.UUID.randomUUID(),
                "Ripetizioni di PSS (Tutor)",
                "Ingegneria e scienze informatiche",
                "PSS",
                "Design pattern",
                LocalDate.now().minusDays(1),
                LocalTime.of(15, 0),
                2,
                AUTHOR,
                BoxType.OFFER,
                java.util.List.of(CANDIDATE),
                null,
                java.util.List.of(),
                "");

        box.confermaCandidato(CANDIDATE);

        assertEquals(null, box.getConfermato());
        assertTrue(box.isCandidato(CANDIDATE));
    }

    @Test
    void deletionWithoutConfirmationShouldBeImmediate() {
        final BoxTutoraggio box = createBox();
        box.aggiungiCandidato(CANDIDATE);

        assertThrows(SecurityException.class, () -> box.eliminaAnnuncio(CANDIDATE));
        assertTrue(box.eliminaAnnuncio(AUTHOR));
    }

    @Test
    void deletionAfterConfirmationShouldBeSoftAndBlockTheAnnouncement() {
        final BoxTutoraggio box = createBox();
        box.aggiungiCandidato(CANDIDATE);
        box.confermaCandidato(CANDIDATE);

        assertFalse(box.eliminaAnnuncio(AUTHOR));
        assertTrue(box.isCancellato());
        assertTrue(box.getCancellatoAt() != null);
        assertFalse(box.puoModificareProgrammazione());
        assertFalse(box.eliminaAnnuncio(AUTHOR), "Una seconda eliminazione non deve avere effetto");

        box.aggiungiCandidato("1000000003");
        assertFalse(box.isCandidato("1000000003"));
    }

    private static BoxTutoraggio createBox() {
        return createBoxWithDuration(2);
    }

    private static BoxTutoraggio createBoxWithDuration(final int durataOre) {
        return new BoxTutoraggioImpl(
                "Ripasso PSS",
                "Ingegneria e scienze informatiche",
                "PSS",
                "Design pattern",
                INITIAL_DATE,
                LocalTime.of(15, 0),
                durataOre,
                AUTHOR,
                BoxType.OFFER);
    }

    private static BoxTutoraggio createExpiredBox() {
        return new BoxTutoraggioImpl(
                "Ripasso PSS",
                "Ingegneria e scienze informatiche",
                "PSS",
                "Design pattern",
                LocalDate.now().minusDays(1),
                LocalTime.of(15, 0),
                2,
                AUTHOR,
                BoxType.OFFER);
    }
}
