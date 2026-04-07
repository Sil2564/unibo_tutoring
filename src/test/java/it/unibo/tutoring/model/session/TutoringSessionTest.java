package it.unibo.tutoring.model.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import it.unibo.tutoring.model.chat.Message;

class TutoringSessionTest {

    private TutoringSession session;

    @BeforeEach
    void setUp() {
        session = new TutoringSessionImpl("Progettazione e Sviluppo del Software", LocalDateTime.now().plusDays(2), Duration.ofHours(2));
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

    @Test
    void testFacadeInviaMessaggio() {
        session.inviaMessaggio("Ciao, ti propongo Giovedì alle 15.", "studente_1");
        session.inviaMessaggio("Perfetto, confermo la sessione!", "tutor_2");

        List<Message> storico = session.getStoricoChat();

        assertEquals(2, storico.size(), "La sessione deve aver salvato 2 messaggi tramite la chat interna");
        assertEquals("studente_1", storico.get(0).getIdMittente());
        assertEquals("Perfetto, confermo la sessione!", storico.get(1).getTesto());
    }
}