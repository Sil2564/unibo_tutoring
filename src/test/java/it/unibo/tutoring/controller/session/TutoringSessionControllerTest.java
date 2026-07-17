package it.unibo.tutoring.controller.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
