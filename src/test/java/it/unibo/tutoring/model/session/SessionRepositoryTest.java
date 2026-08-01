package it.unibo.tutoring.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SessionRepositoryTest {

    private static final String TUTOR = "CAL_TUTOR";
    private static final String STUDENT = "CAL_STUDENT";
    private static final Path FILE = Path.of(
            "data", "sessions", "SESS_AnalisiMatematica_" + TUTOR + "_" + STUDENT + ".csv");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(FILE);
    }

    @Test
    void shouldShowOnlyConfirmedUpcomingSessions() throws IOException {
        final LocalDateTime date = LocalDateTime.now().plusDays(3).withNano(0);
        writeSession("Confirmed", date);

        final SessionRepository repository = new SessionRepository();
        final List<TutoringSession> sessions = repository.getConfirmedSessionsForUser(STUDENT);

        assertEquals(1, sessions.size());
        assertEquals("Analisi Matematica", sessions.get(0).getMateria());
        assertEquals(date, sessions.get(0).getDataOra());
        assertEquals(Duration.ofHours(2), sessions.get(0).getDurata());
        assertEquals(TUTOR, sessions.get(0).getTutorMatricola());

        writeSession("Completed", date);
        assertTrue(repository.getConfirmedSessionsForUser(STUDENT).isEmpty());
    }

    private static void writeSession(final String state, final LocalDateTime date) throws IOException {
        Files.createDirectories(FILE.getParent());
        Files.write(
                FILE,
                List.of(
                        "MATERIA;Analisi Matematica",
                        "DATA_ORA;" + date,
                        "DURATA;PT2H",
                        "TUTOR;" + TUTOR,
                        "STUDENTE;" + STUDENT,
                        "STATO;" + state),
                StandardCharsets.UTF_8);
    }
}
