package it.unibo.tutoring.model.session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.tutoring.model.chat.Message;

class TutoringSessionTest {

    private TutoringSession session;

    @BeforeEach
    void setUp() {
        session = new TutoringSessionImpl("Progettazione e Sviluppo del Software", LocalDateTime.now().plusDays(2), Duration.ofHours(2), "MAT12345");
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

    @Test
    void testSessioneConfermataPuoEssereAnnullataMaNonCompletata() {
        session.conferma();
        session.annulla();

        assertTrue(session.getStatoCorrente() instanceof CancelledState);
        assertThrows(IllegalStateException.class, session::completa);
        assertThrows(IllegalStateException.class, session::annulla);
    }

    @Test
    void testObserverChatAttraversoSessione() {
        final TutoringSession session = new TutoringSessionImpl(
                "OOP",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(1),
                "1234567890"
        );

        final List<Message> ricevuti = new ArrayList<>();

        session.addChatObserver(ricevuti::add);
        session.inviaMessaggio("Ciao", "0987654321");

        assertEquals(1, ricevuti.size());
        assertEquals("Ciao", ricevuti.getFirst().getTesto());
    }
}