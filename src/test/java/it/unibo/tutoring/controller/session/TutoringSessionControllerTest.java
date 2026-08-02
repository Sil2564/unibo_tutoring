package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.model.credit.ReviewRepository;
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

        List<ReviewRepository.Review> savedReviews = ReviewRepository.loadReviewsForRecipient("0001");
        assertTrue(!savedReviews.isEmpty());
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

}
