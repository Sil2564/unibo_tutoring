package it.unibo.tutoring.controller.session;

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

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
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

        controller.confermaSessione();
        controller.completaSessione();
        controller.registraRecensione(4, "Esperienza positiva");

        assertTrue(Files.exists(filePath));

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        assertTrue(lines.stream().anyMatch(line -> line.equals("REVIEW;4|Esperienza positiva")));

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
}
