package it.unibo.tutoring.controller.session;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import it.unibo.tutoring.model.session.SessionRepository;

class SessionOverlapTest {

    private static final Path SESSION_FOLDER = Path.of("data", "sessions");
    private final List<Path> generatedFiles = new ArrayList<>();

    @AfterEach
    void cleanup() throws IOException {
        for (final Path path : this.generatedFiles) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void shouldRejectPartialOverlapForTutorWithoutPersistingProposedSession() {
        final LocalDateTime start = LocalDateTime.of(2035, 2, 10, 10, 0);
        final TutoringSessionController existing = createController(
                "OVT1", "OVTUTOR", "OVSTUDENTA", start, Duration.ofHours(2));
        existing.confermaSessione();

        final TutoringSessionController overlapping = createController(
                "OVT2", "OVTUTOR", "OVSTUDENTB", start.plusHours(1), Duration.ofHours(2));
        final IllegalStateException error = assertThrows(
                IllegalStateException.class,
                overlapping::confermaSessione);

        assertTrue(error.getMessage().contains("Il tutor"));
        assertTrue(error.getMessage().contains("Materia OVT1"));
        assertTrue(overlapping.isProposta());
        assertFalse(Files.exists(pathFor("OVT2", "OVTUTOR", "OVSTUDENTB")));
    }

    @Test
    void shouldRejectContainingOverlapForStudent() {
        final LocalDateTime start = LocalDateTime.of(2035, 3, 12, 12, 0);
        final TutoringSessionController existing = createController(
                "OVS1", "OVTUTORA", "OVSTUDENT", start, Duration.ofHours(1));
        existing.confermaSessione();

        final TutoringSessionController containing = createController(
                "OVS2", "OVTUTORB", "OVSTUDENT", start.minusHours(1), Duration.ofHours(3));
        final IllegalStateException error = assertThrows(
                IllegalStateException.class,
                containing::confermaSessione);

        assertTrue(error.getMessage().contains("Lo studente"));
        assertTrue(containing.isProposta());
    }

    @Test
    void shouldAllowSessionsThatTouchAtTheirBoundary() {
        final LocalDateTime start = LocalDateTime.of(2035, 4, 8, 10, 0);
        final TutoringSessionController first = createController(
                "BOUND1", "BOUNDTUTOR", "BOUNDSTUDENTA", start, Duration.ofHours(2));
        first.confermaSessione();

        final TutoringSessionController second = createController(
                "BOUND2", "BOUNDTUTOR", "BOUNDSTUDENTB", start.plusHours(2), Duration.ofHours(1));
        second.confermaSessione();

        assertTrue(first.isConfermata());
        assertTrue(second.isConfermata());
    }

    @Test
    void shouldIgnoreProposedCancelledAndUnrelatedSessions() {
        final LocalDateTime start = LocalDateTime.of(2035, 5, 15, 15, 0);

        final TutoringSessionController proposed = createController(
                "IGNPROP", "IGNTUTOR", "IGNSTUDENTA", start, Duration.ofHours(2));
        proposed.proponi();

        final TutoringSessionController cancelled = createController(
                "IGNCANCEL", "IGNTUTOR", "IGNSTUDENTB", start, Duration.ofHours(2));
        cancelled.confermaSessione();
        cancelled.cancellaSessione("Test sovrapposizione");

        final TutoringSessionController unrelated = createController(
                "IGNOTHER", "OTHERONE", "OTHERTWO", start, Duration.ofHours(2));
        unrelated.confermaSessione();

        final TutoringSessionController available = createController(
                "IGNOK", "IGNTUTOR", "IGNSTUDENTC", start.plusMinutes(30), Duration.ofHours(1));
        available.confermaSessione();

        assertTrue(available.isConfermata());
    }

    @Test
    void shouldDetectAnAlreadyStartedSessionThatHasNotEnded() {
        final LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        final TutoringSessionController ongoing = createController(
                "ONGOING1", "ONGOINGTUTOR", "ONGOINGSTUDENTA",
                now.minusMinutes(30), Duration.ofHours(1));
        ongoing.confermaSessione();

        final TutoringSessionController overlapping = createController(
                "ONGOING2", "ONGOINGTUTOR", "ONGOINGSTUDENTB",
                now, Duration.ofHours(1));

        assertThrows(IllegalStateException.class, overlapping::confermaSessione);
        assertTrue(overlapping.isProposta());
    }

    @Test
    void repositoryShouldExcludeTheCurrentSessionFile() {
        final LocalDateTime start = LocalDateTime.of(2035, 6, 20, 9, 0);
        final TutoringSessionController controller = createController(
                "SELFEXCLUDE", "SELFTUTOR", "SELFSTUDENT", start, Duration.ofHours(2));
        controller.confermaSessione();

        final SessionRepository repository = new SessionRepository();
        assertTrue(repository.findOverlappingConfirmedSession(
                "SELFTUTOR",
                start,
                Duration.ofHours(2),
                pathFor("SELFEXCLUDE", "SELFTUTOR", "SELFSTUDENT")).isEmpty());
    }

    private TutoringSessionController createController(
            final String conversationId,
            final String tutor,
            final String student,
            final LocalDateTime start,
            final Duration duration) {
        this.generatedFiles.add(pathFor(conversationId, tutor, student));
        return new TutoringSessionController(
                "Materia " + conversationId,
                "Tutor test",
                true,
                tutor,
                student,
                start,
                duration,
                conversationId);
    }

    private static Path pathFor(
            final String conversationId,
            final String tutor,
            final String student) {
        return SESSION_FOLDER.resolve(
                "SESS_" + conversationId + "_" + tutor + "_" + student + ".csv");
    }
}
