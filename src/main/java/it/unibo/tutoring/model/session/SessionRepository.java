package it.unibo.tutoring.model.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SessionRepository {

    private static final Path SESSION_FOLDER = Paths.get("data", "sessions");

    /**
     * Cerca e restituisce tutte le sessioni in stato "Confirmed" a cui partecipa l'utente.
     */
    public List<TutoringSession> getConfirmedSessionsForUser(String matricola) {
        List<TutoringSession> confirmedSessions = new ArrayList<>();

        if (!Files.exists(SESSION_FOLDER) || !Files.isDirectory(SESSION_FOLDER)) {
            return confirmedSessions;
        }

        try (Stream<Path> paths = Files.list(SESSION_FOLDER)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("SESS_"))
                    .filter(path -> path.getFileName().toString().contains(matricola))
                    .forEach(path -> {
                        TutoringSession session = parseSessionFile(path);
                        if (session != null) {
                            confirmedSessions.add(session);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Errore nella lettura della cartella sessioni: " + e.getMessage());
        }

        return confirmedSessions;
    }

    /**
     * Legge il file CSV e ricostruisce l'oggetto TutoringSession se lo stato è Confirmed.
     */
    private TutoringSession parseSessionFile(Path filePath) {
        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            String stato = "";
            String tutorInfo = "";

            while ((line = br.readLine()) != null) {
                if (line.startsWith("STATO;")) stato = line.split(";", 2)[1];
                else if (line.startsWith("TUTOR;")) tutorInfo = line.split(";", 2)[1];
                else if (line.startsWith("MSG;")) break;
            }

            if ("Confirmed".equals(stato)) {
                // Ricava la materia dal nome del file
                String fileName = filePath.getFileName().toString();
                String[] nameParts = fileName.replace(".csv", "").split("_");
                String materia = nameParts.length > 1 ? nameParts[1].replace("-", " ") : "Materia";
                String matricolaTutor = tutorInfo.split("\\|")[0];

                TutoringSession session = new TutoringSessionImpl(
                        materia,
                        LocalDateTime.now(),// TODO temporaneo
                        Duration.ofHours(2),
                        matricolaTutor
                );

                // Impostiamo lo stato interno su Confirmed
                session.conferma();

                return session;
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file " + filePath.getFileName() + ": " + e.getMessage());
        }
        return null;
    }
}