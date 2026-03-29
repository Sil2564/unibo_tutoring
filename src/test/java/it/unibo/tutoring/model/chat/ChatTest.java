package it.unibo.tutoring.model.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private Chat chat;
    private List<Message> messaggiRicevutiDallObserver;

    @BeforeEach
    void setUp() {
        chat = new ChatImpl();
        messaggiRicevutiDallObserver = new ArrayList<>();

        // Quando la chat lo avvisa di un nuovo messaggio, lui lo salva nella nostra lista.
        ChatObserver spyObserver = nuovoMessaggio -> messaggiRicevutiDallObserver.add(nuovoMessaggio);

        chat.addObserver(spyObserver);
    }

    @Test
    void testAggiuntaMessaggio() {
        Message msg = new MessageImpl("Messaggio di testt", "studente_123");
        chat.aggiungiMessaggio(msg);

        assertEquals(1, chat.getStoricoMessaggi().size(), "La chat deve contenere 1 messaggio");
        assertEquals("Messaggio di testt", chat.getStoricoMessaggi().get(0).getTesto());
    }

    @Test
    void testObserverVieneNotificato() {
        Message msg1 = new MessageImpl("Primo messaggio", "tutor_456");
        chat.aggiungiMessaggio(msg1);

        // Verifichiamo che l'Observer spia sia stato avvisato
        assertEquals(1, messaggiRicevutiDallObserver.size(), "L'observer doveva ricevere 1 notifica");
        assertEquals("Primo messaggio", messaggiRicevutiDallObserver.get(0).getTesto());

        Message msg2 = new MessageImpl("Secondo messaggio", "studente_123");
        chat.aggiungiMessaggio(msg2);

        // Verifichiamo che venga avvisato anche per i messaggi dopo
        assertEquals(2, messaggiRicevutiDallObserver.size(), "L'observer doveva ricevere 2 notifiche");
        assertEquals("Secondo messaggio", messaggiRicevutiDallObserver.get(1).getTesto());
    }
}