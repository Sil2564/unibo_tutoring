package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.model.credit.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TutoringSessionControllerTest {

    private final Path filePath = Path.of("data", "sessions", "SESS_TestMateria_0001_9999.csv");
    private final Path firstConversationPath = Path.of(
            "data", "sessions", "SESS_BOX_A_0000000002_0000000001_0000000002.csv");
    private final Path secondConversationPath = Path.of(
            "data", "sessions", "SESS_BOX_B_0000000002_0000000001_0000000002.csv");

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.deleteIfExists(firstConversationPath);
        Files.deleteIfExists(secondConversationPath);
    }

    @Test
    void shouldPersistReviewWhenSaved() throws IOException {
        // Verifica che la recensione venga salvata su file e ricaricata dal controller.
        TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999");

        controller.registraRecensione(4, "Esperienza positiva");

        assertTrue(Files.exists(filePath));

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        assertTrue(lines.stream().anyMatch(line -> line.equals("REVIEW;4|Esperienza positiva")));

        List<ReviewRepository.Review> savedReviews = ReviewRepository.loadReviewsForRecipient("0001");
        assertFalse(savedReviews.isEmpty());
        ReviewRepository.Review savedReview = savedReviews.get(savedReviews.size() - 1);
        assertEquals("Esperienza positiva", savedReview.comment());
        assertEquals(4, savedReview.stars());

        TutoringSessionController loadedController = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999");

        assertTrue(loadedController.isReviewSaved());
        assertEquals(4, loadedController.getReviewStars());
        assertEquals("Esperienza positiva", loadedController.getReviewComment());
    }

    @Test
    void shouldPersistCalendarDataAndReadableMessages() throws IOException {
        final LocalDateTime dataOra = LocalDateTime.of(2026, 9, 15, 14, 30);
        final Duration durata = Duration.ofHours(2);
        final TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                dataOra,
                durata);

        controller.confermaSessione();
        controller.inviaMessaggio("Ciao; ci vediamo?\nPerfetto");

        final List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        assertTrue(lines.contains("MATERIA;Test Materia"));
        assertTrue(lines.contains("DATA_ORA;2026-09-15T14:30"));
        assertTrue(lines.contains("DURATA;PT2H"));
        assertTrue(lines.contains("TUTOR;0001"));
        assertTrue(lines.contains("STUDENTE;9999"));
        assertTrue(lines.stream().anyMatch(line ->
                line.startsWith("MSG2;")
                        && line.endsWith(";9999;Ciao; ci vediamo? Perfetto")));

        final var originalMessage = controller.getModel().getStoricoChat().get(0);
        final TutoringSessionController loadedController = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                dataOra,
                durata);
        final var loadedMessage = loadedController.getModel().getStoricoChat().get(0);

        assertEquals(originalMessage.getTimestamp(), loadedMessage.getTimestamp());
        assertEquals("Ciao; ci vediamo? Perfetto", loadedMessage.getTesto());
    }

    @Test
    void shouldSeparateChatsForDifferentAnnouncementsWithSameParticipants() {
        final LocalDateTime dataOra = LocalDateTime.of(2026, 9, 15, 14, 30);
        final Duration durata = Duration.ofHours(1);
        final TutoringSessionController firstConversation = new TutoringSessionController(
                "OOP",
                "Tutor Uno",
                true,
                "0000000001",
                "0000000002",
                dataOra,
                durata,
                "BOX_A_0000000002");
        final TutoringSessionController secondConversation = new TutoringSessionController(
                "OOP",
                "Tutor Uno",
                true,
                "0000000001",
                "0000000002",
                dataOra,
                durata,
                "BOX_B_0000000002");

        firstConversation.inviaMessaggio("Messaggio del primo annuncio");
        secondConversation.inviaMessaggio("Messaggio del secondo annuncio");

        assertEquals(1, firstConversation.getModel().getStoricoChat().size());
        assertEquals(1, secondConversation.getModel().getStoricoChat().size());
        assertEquals(
                "Messaggio del primo annuncio",
                firstConversation.getModel().getStoricoChat().get(0).getTesto());
        assertEquals(
                "Messaggio del secondo annuncio",
                secondConversation.getModel().getStoricoChat().get(0).getTesto());
    }

    @Test
    void completionShouldBecomeAvailableExactlyAtScheduledEnd() {
        final LocalDateTime start = LocalDateTime.of(2026, 8, 5, 11, 0);
        final TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                start,
                Duration.ofHours(2));

        assertEquals(LocalDateTime.of(2026, 8, 5, 13, 0), controller.getFinePrevista());
        assertFalse(controller.puoSegnalareCompletamento(
                LocalDateTime.of(2026, 8, 5, 12, 59, 59)));
        assertTrue(controller.puoSegnalareCompletamento(
                LocalDateTime.of(2026, 8, 5, 13, 0)));
    }

    @Test
    void shouldRejectCompletionBeforeScheduledEnd() {
        final TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(2));

        controller.confermaSessione();

        final IllegalStateException error = assertThrows(
                IllegalStateException.class,
                controller::segnalaCompletamento);
        assertTrue(error.getMessage().contains("solo dopo la fine prevista"));
        assertFalse(controller.isCompletatoDaTutor());
        assertFalse(controller.isCompletatoDaStudente());
    }
}
