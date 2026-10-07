package it.unibo.tutoring.model.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreditRepositoryTest {

    private static final Path CREDITS_PATH = Path.of("data", "credits.csv");
    private static final String MATRICOLA = "9999999990";

    private byte[] originalCredits;

    @BeforeEach
    void preserveCredits() throws IOException {
        this.originalCredits = Files.exists(CREDITS_PATH) ? Files.readAllBytes(CREDITS_PATH) : null;
    }

    @AfterEach
    void restoreCredits() throws IOException {
        if (this.originalCredits == null) {
            Files.deleteIfExists(CREDITS_PATH);
        } else {
            Files.write(CREDITS_PATH, this.originalCredits);
        }
    }

    @Test
    void unknownBadgeShouldNotResetTheHoursOfTheTutor() throws IOException {
        Files.createDirectories(CREDITS_PATH.getParent());
        if (!Files.exists(CREDITS_PATH)) {
            Files.writeString(CREDITS_PATH,
                    "matricola;firstName;lastName;totalHours;totalCredits;badge;rating" + System.lineSeparator());
        }
        Files.writeString(CREDITS_PATH,
                System.lineSeparator() + MATRICOLA + ";Test;Tutor;30;1;ADVANCED;4.5" + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        assertTrue(CreditRepository.loadRecord(MATRICOLA).isPresent());

        final CreditRecord record = new CreditService(new DefaultBadgePolicy()).getCreditRecord(MATRICOLA);

        assertEquals(30, record.getTotalHours());
        assertEquals(1, record.getTotalCredits());
        assertEquals(Badge.INTERMEDIATE, record.getBadge());
    }
}
