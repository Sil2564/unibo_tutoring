package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.model.credit.CompletedSessionRepository;
import it.unibo.tutoring.model.credit.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private final Path expiredSessionPath = Path.of(
            "data", "sessions", "SESS_ExpiredTest_0001_9999.csv");
    private final Path unreadChatPath = Path.of(
            "data", "sessions", "SESS_CHAT_UNREAD_0000000001_0000000002.csv");
    private final Path tutorFirstPath = Path.of(
            "data", "sessions", "SESS_TUTOR_FIRST_FLOW_FLOW_TUTOR_FLOW_STUDENT.csv");
    private static final Path CREDITS_PATH = Path.of("data", "credits.csv");
    private static final Path COMPLETED_SESSIONS_PATH = Path.of("data", "completed_sessions.csv");
    private static final Path REVIEWS_PATH = Path.of("data", "reviews.csv");

    private byte[] originalCredits;
    private byte[] originalCompletedSessions;
    private byte[] originalReviews;

    @BeforeEach
    void preserveSharedDataFiles() throws IOException {
        this.originalCredits = readIfPresent(CREDITS_PATH);
        this.originalCompletedSessions = readIfPresent(COMPLETED_SESSIONS_PATH);
        this.originalReviews = readIfPresent(REVIEWS_PATH);
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.deleteIfExists(firstConversationPath);
        Files.deleteIfExists(secondConversationPath);
        Files.deleteIfExists(expiredSessionPath);
        Files.deleteIfExists(unreadChatPath);
        Files.deleteIfExists(tutorFirstPath);
        restoreFile(CREDITS_PATH, this.originalCredits);
        restoreFile(COMPLETED_SESSIONS_PATH, this.originalCompletedSessions);
        restoreFile(REVIEWS_PATH, this.originalReviews);
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

        assertTrue(Files.exists(firstConversationPath));
        assertTrue(Files.exists(secondConversationPath));
        assertNotEquals(firstConversationPath, secondConversationPath);
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
                "Test Studente",
                false,
                "9999",
                "0001",
                start,
                Duration.ofHours(2));
        controller.confermaSessione();

        assertEquals(LocalDateTime.of(2026, 8, 5, 13, 0), controller.getFinePrevista());
        assertFalse(controller.puoSegnalareCompletamento(
                LocalDateTime.of(2026, 8, 5, 12, 59, 59)));
        assertTrue(controller.puoSegnalareCompletamento(
                LocalDateTime.of(2026, 8, 5, 13, 0)));
    }

    @Test
    void tutorMustCompleteFirstAndCompletionEventMustBePublishedOnce() {
        final LocalDateTime start = LocalDateTime.now().minusHours(2);
        final Duration duration = Duration.ofHours(1);

        final TutoringSessionController studentController = new TutoringSessionController(
                "Tutor First Flow",
                "Test Tutor",
                true,
                "FLOW_TUTOR",
                "FLOW_STUDENT",
                start,
                duration,
                "TUTOR_FIRST_FLOW");
        studentController.confermaSessione();

        final IllegalStateException studentFirstError = assertThrows(
                IllegalStateException.class,
                studentController::segnalaCompletamento);
        assertTrue(studentFirstError.getMessage().contains("tutor deve confermare per primo"));
        assertFalse(studentController.isCompletatoDaStudente());
        assertFalse(studentController.shouldAskForReview());

        final TutoringSessionController tutorController = new TutoringSessionController(
                "Tutor First Flow",
                "Test Studente",
                false,
                "FLOW_STUDENT",
                "FLOW_TUTOR",
                start,
                duration,
                "TUTOR_FIRST_FLOW");
        tutorController.segnalaCompletamento();
        assertTrue(tutorController.isCompletatoDaTutor());
        assertTrue(tutorController.isConfermata());

        final TutoringSessionController studentAfterTutor = new TutoringSessionController(
                "Tutor First Flow",
                "Test Tutor",
                true,
                "FLOW_TUTOR",
                "FLOW_STUDENT",
                start,
                duration,
                "TUTOR_FIRST_FLOW");
        assertTrue(studentAfterTutor.puoSegnalareCompletamento());

        studentAfterTutor.segnalaCompletamento();

        assertTrue(studentAfterTutor.isCompletataDaEntrambi());
        assertTrue(studentAfterTutor.shouldAskForReview());
        assertEquals(
                1,
                it.unibo.tutoring.AppConfig.getInstance()
                        .getCreditService()
                        .getCreditRecord("FLOW_TUTOR")
                        .getTotalHours(),
                "L'evento di completamento deve essere pubblicato una sola volta.");
    }

    @Test
    void shouldPersistUnreadChatUntilReceiverOpensConversation() throws IOException {
        final LocalDateTime dataOra = LocalDateTime.now().plusDays(1);
        final Duration durata = Duration.ofHours(1);
        final TutoringSessionController studentController = new TutoringSessionController(
                "OOP",
                "Tutor Uno",
                true,
                "0000000001",
                "0000000002",
                dataOra,
                durata,
                "CHAT_UNREAD");

        studentController.inviaMessaggio("Hai ricevuto questo messaggio");
        assertFalse(studentController.haMessaggiChatNonLetti());
        assertTrue(Files.readAllLines(unreadChatPath).contains(
                "CHAT_VISTA_DA;0000000002"));

        final TutoringSessionController tutorController = new TutoringSessionController(
                "OOP",
                "Studente Uno",
                false,
                "0000000002",
                "0000000001",
                dataOra,
                durata,
                "CHAT_UNREAD");
        assertTrue(tutorController.haMessaggiChatNonLetti());

        tutorController.segnaChatComeLetta();
        final TutoringSessionController reloadedTutor = new TutoringSessionController(
                "OOP",
                "Studente Uno",
                false,
                "0000000002",
                "0000000001",
                dataOra,
                durata,
                "CHAT_UNREAD");
        assertFalse(reloadedTutor.haMessaggiChatNonLetti());

        reloadedTutor.inviaMessaggio("Risposta del tutor");
        final TutoringSessionController reloadedStudent = new TutoringSessionController(
                "OOP",
                "Tutor Uno",
                true,
                "0000000001",
                "0000000002",
                dataOra,
                durata,
                "CHAT_UNREAD");
        assertTrue(reloadedStudent.haMessaggiChatNonLetti());
    }

    @Test
    void cancellationShouldStopBeingAvailableExactlyAtScheduledEnd() {
        final LocalDateTime start = LocalDateTime.of(2030, 8, 5, 11, 0);
        final TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                start,
                Duration.ofHours(2));
        controller.confermaSessione();

        assertTrue(controller.puoCancellareSessione(
                LocalDateTime.of(2030, 8, 5, 12, 59, 59)));
        assertFalse(controller.puoCancellareSessione(
                LocalDateTime.of(2030, 8, 5, 13, 0)));
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

    @Test
    void shouldCancelConfirmedSessionAndPersistAuditChatAndUnreadNotification() throws IOException {
        final LocalDateTime start = LocalDateTime.now().plusDays(2).withNano(0);
        final TutoringSessionController studentController = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                start,
                Duration.ofHours(2));

        studentController.confermaSessione();
        studentController.cancellaSessione("Imprevisto personale\nurgente");

        assertTrue(studentController.isAnnullata());
        assertFalse(studentController.puoCancellareSessione());
        assertEquals("9999", studentController.getCancellataDa());
        assertNotNull(studentController.getCancellataAt());
        assertEquals("Imprevisto personale urgente", studentController.getMotivoCancellazione());
        assertTrue(studentController.haVistoCancellazione());
        assertFalse(studentController.shouldAskForReview());
        assertThrows(IllegalStateException.class, studentController::segnalaCompletamento);

        final var systemMessage = studentController.getModel().getStoricoChat().get(0);
        assertEquals(TutoringSessionController.SYSTEM_SENDER_ID, systemMessage.getIdMittente());
        assertTrue(systemMessage.getTesto().contains("Imprevisto personale urgente"));
        assertEquals(studentController.getCancellataAt(), systemMessage.getTimestamp());

        final List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        assertTrue(lines.contains("STATO;Cancelled"));
        assertTrue(lines.contains("CANCELLATA_DA;9999"));
        assertTrue(lines.contains("MOTIVO_CANCELLAZIONE;Imprevisto personale urgente"));
        assertTrue(lines.contains("CANCELLAZIONE_VISTA_DA;9999"));

        // La stessa sessione viene ricostruita dal punto di vista del tutor:
        // il file, la chat e i metadati sono condivisi, mentre la notifica e'
        // ancora non letta per la controparte.
        final TutoringSessionController tutorController = new TutoringSessionController(
                "Test Materia",
                "Test Studente",
                false,
                "9999",
                "0001",
                start,
                Duration.ofHours(2));

        assertTrue(tutorController.isAnnullata());
        assertEquals("9999", tutorController.getCancellataDa());
        assertEquals("Imprevisto personale urgente", tutorController.getMotivoCancellazione());
        assertEquals(1, tutorController.getModel().getStoricoChat().size());
        assertFalse(tutorController.haVistoCancellazione());

        tutorController.segnaCancellazioneVista();
        final TutoringSessionController reloadedTutorController = new TutoringSessionController(
                "Test Materia",
                "Test Studente",
                false,
                "9999",
                "0001",
                start,
                Duration.ofHours(2));
        assertTrue(reloadedTutorController.haVistoCancellazione());
    }

    @Test
    void cancelledSessionShouldRemainVisibleForTwentyFourHours() {
        final LocalDateTime start = LocalDateTime.now().plusDays(2).withNano(0);
        final TutoringSessionController controller = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                start,
                Duration.ofHours(2));

        controller.confermaSessione();
        controller.cancellaSessione("");
        final LocalDateTime cancelledAt = controller.getCancellataAt();

        assertTrue(controller.isCancellazioneVisibile(cancelledAt.plusHours(23)));
        assertTrue(controller.isCancellazioneVisibile(cancelledAt.plusHours(24)));
        assertFalse(controller.isCancellazioneVisibile(
                cancelledAt.plusHours(24).plusNanos(1)));
    }

    @Test
    void completedSessionAppendShouldRepairMissingFinalNewLine() throws IOException {
        Files.writeString(
                COMPLETED_SESSIONS_PATH,
                "studentName;subject;date;hours;creditsGiven;tutorMatricola\n"
                        + "Studente Esistente;OOP;01-08-2026;2;0;TUTOR_TEST",
                StandardCharsets.UTF_8);

        CompletedSessionRepository.saveCompletedSession(
                "Nuovo Studente",
                "Programmazione",
                "14-08-2026",
                2,
                1,
                "TUTOR_TEST");

        final List<String> lines = Files.readAllLines(COMPLETED_SESSIONS_PATH, StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
        assertEquals(
                "Nuovo Studente;Programmazione;14-08-2026;2;1;TUTOR_TEST",
                lines.get(2));
        assertTrue(fileEndsWithNewLine(COMPLETED_SESSIONS_PATH));
        assertEquals(2, CompletedSessionRepository.loadCompletedSessionsForTutor("TUTOR_TEST").size());
    }

    @Test
    void reviewAppendShouldRepairMissingFinalNewLine() throws IOException {
        Files.writeString(
                REVIEWS_PATH,
                "reviewerName;subject;date;stars;comment;tutorMatricola\n"
                        + "Studente Esistente;OOP;01-08-2026;4;Chiara;TUTOR_TEST",
                StandardCharsets.UTF_8);

        ReviewRepository.saveReview(
                "Nuovo Studente",
                "Programmazione",
                "14-08-2026",
                5,
                "Recensione separata",
                "TUTOR_TEST");

        final List<String> lines = Files.readAllLines(REVIEWS_PATH, StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
        assertEquals(
                "Nuovo Studente;Programmazione;14-08-2026;5;Recensione separata;TUTOR_TEST",
                lines.get(2));
        assertTrue(fileEndsWithNewLine(REVIEWS_PATH));
        final List<ReviewRepository.Review> reviews =
                ReviewRepository.loadReviewsForRecipient("TUTOR_TEST");
        assertEquals(2, reviews.size());
        assertEquals("Recensione separata", reviews.get(1).comment());
    }

    @Test
    void shouldRejectCancellationOfUnconfirmedOrAlreadyEndedSession() {
        final TutoringSessionController proposedController = new TutoringSessionController(
                "Test Materia",
                "Test Tutor",
                true,
                "0001",
                "9999",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(1));

        assertFalse(proposedController.puoCancellareSessione());
        assertThrows(
                IllegalStateException.class,
                () -> proposedController.cancellaSessione("Non confermata"));

        final TutoringSessionController expiredController = new TutoringSessionController(
                "Expired Test",
                "Test Tutor",
                true,
                "0001",
                "9999",
                LocalDateTime.now().minusHours(2),
                Duration.ofHours(1));
        expiredController.confermaSessione();

        assertFalse(expiredController.puoCancellareSessione());
        final IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> expiredController.cancellaSessione("Troppo tardi"));
        assertTrue(error.getMessage().contains("dopo la fine prevista"));
        assertTrue(expiredController.isConfermata());
    }

    private static byte[] readIfPresent(final Path path) throws IOException {
        return Files.exists(path) ? Files.readAllBytes(path) : null;
    }

    private static void restoreFile(final Path path, final byte[] originalContent) throws IOException {
        if (originalContent == null) {
            Files.deleteIfExists(path);
        } else {
            Files.write(path, originalContent);
        }
    }

    private static boolean fileEndsWithNewLine(final Path path) throws IOException {
        final byte[] content = Files.readAllBytes(path);
        return content.length > 0
                && (content[content.length - 1] == '\n' || content[content.length - 1] == '\r');
    }
}
