package it.unibo.tutoring.model.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

class TutoringSessionTest {

    private TutoringSession session;

    @BeforeEach
    void setUp() {
        session = new TutoringSessionImpl("Programmazione ad Oggetti", LocalDateTime.now().plusDays(2), Duration.ofHours(2));
    }

    @Test
    void testStatoIniziale() {
        // Appena creata, una sessione è nello stato "Proposta"
        assertTrue(session.getStatoCorrente() instanceof ProposedState, "La sessione appena creata deve essere in ProposedState");
    }

    @Test
    void testTransizioneConferma() {
        session.conferma();
        assertTrue(session.getStatoCorrente() instanceof ConfirmedState, "Dopo la conferma deve essere in ConfirmedState");
    }

    @Test
    void testTransizioneIllegale() {
        // Non posso completare una sessione che è solo "Proposta" e non ancora confermata
        assertThrows(IllegalStateException.class, () -> {
            session.completa();
        });
    }
}