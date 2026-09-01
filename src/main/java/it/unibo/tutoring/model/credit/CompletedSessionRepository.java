package it.unibo.tutoring.model.credit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight repository per caricamento delle sessioni completate.
 * Legge il file data/completed_sessions.csv e fornisce accesso ai record.
 */
public final class CompletedSessionRepository {

    private static final Path DB = Paths.get("data", "completed_sessions.csv");
    private static final String SEP = ";";

    private CompletedSessionRepository() {
    }

    /**
     * Record semplice che rappresenta una sessione completata.
     */
    public record CompletedSession(
        String studentName,
        String subject,
        String date,
        int hours,
        int creditsGiven
    ) {}

    /**
     * Carica tutte le sessioni completate per il tutor fornito.
     *
     * @param tutorMatricola id del tutor che ha completato le sessioni
     * @return lista di CompletedSession per quel tutor
     */
    public static List<CompletedSession> loadCompletedSessionsForTutor(final String tutorMatricola) {
        final List<CompletedSession> sessions = new ArrayList<>();  //inizializza la lista vuota

        try {
            if (!Files.exists(DB)) {    //se il file non esiste, restituisce la lista vuota
                return sessions;
            }
            // legge tutte le righe del file CSV e le memorizza in una lista di stringhe
            final List<String> lines = Files.readAllLines(DB);
            for (final String line : lines) {   //itera su ogni riga del file CSV
                // ignora righe vuote o intestazioni
                if (line == null || line.trim().isEmpty() || line.startsWith("studentName")) {
                    continue; // header
                }
                // divide la riga in parti usando il separatore SEP
                final String[] parts = line.split(SEP, -1);
                if (parts.length < 5) {
                    continue;
                }

                // verifica se la matricola del tutor corrisponde a quella fornita come parametro
                final String tutor = parts[parts.length - 1].trim();
                if (!tutor.equals(tutorMatricola)) {    //se non corrisponde, passa alla riga successiva
                    continue;
                }

                // determina se la riga contiene informazioni sui crediti (almeno 6 parti)
                final boolean hasCredits = parts.length >= 6;
                // Calcola dinamicamente l'indice della colonna "ore" procedendo a ritroso dalla fine:
                // se i crediti sono presenti è a terzultima posizione (length - 3), altrimenti a penultima (length - 2).
                final int hoursIndex = parts.length - (hasCredits ? 3 : 2); //
                final int dateIndex = hoursIndex - 1;   // L'indice della data si trova immediatamente a sinistra rispetto a quello delle ore
                final String studentName = parts[0].trim();
                final String date = parts[dateIndex].trim();
                final int hours = Integer.parseInt(parts[hoursIndex].trim());
                //se i crediti sono presenti e la penultima parte non è vuota, li converte in intero, altrimenti li imposta a 0
                final int creditsGiven = hasCredits && !parts[parts.length - 2].trim().isEmpty()
                    ? Integer.parseInt(parts[parts.length - 2].trim())
                    : 0;

                //crea un oggetto StringBuilder per il nome della materia se al suo interno ci sono più parti, 
                // le unisce con il separatore SEP e le aggiunge alla lista delle sessioni completate
                final StringBuilder subjectBuilder = new StringBuilder();
                for (int i = 1; i < dateIndex; i++) {
                    if (subjectBuilder.length() > 0) {
                        subjectBuilder.append(SEP);
                    }
                    subjectBuilder.append(parts[i].trim());
                }
                final String subject = subjectBuilder.toString();

                sessions.add(new CompletedSession(studentName, subject, date, hours, creditsGiven));
            }
        } catch (final IOException | RuntimeException e) {
            
        }

        return sessions;
    }

    public static synchronized void saveCompletedSession(
        final String studentName,
        final String subject,
        final String date,
        final int hours,
        final int creditsGiven,
        final String tutorMatricola
    ) {
        try {
            if (DB.getParent() != null) {
                Files.createDirectories(DB.getParent());
            }

            final String header = "studentName;subject;date;hours;creditsGiven;tutorMatricola";
            if (!Files.exists(DB)) {
                Files.writeString(DB, header + System.lineSeparator(), StandardOpenOption.CREATE_NEW);
            }

            // crea la riga da aggiungere al file CSV, unendo i valori con il separatore SEP e sanitizzando eventuali valori problematici
            final String line = String.join(";",
                sanitizeCsvValue(studentName),
                sanitizeCsvValue(subject),
                sanitizeCsvValue(date),
                Integer.toString(hours),
                Integer.toString(creditsGiven),
                sanitizeCsvValue(tutorMatricola)
            );
            appendLine(line);
        } catch (final IOException e) {
            // ignora errori di scrittura per non bloccare l'app
        }
    }

    /**
     * Aggiunge una riga anche quando il file esistente non termina con un
     * carattere di fine riga. In questo modo due record CSV non possono
     * essere concatenati accidentalmente.
     */
    private static void appendLine(final String line) throws IOException {
        final boolean needsLeadingNewLine = Files.exists(DB)
                && Files.size(DB) > 0
                && !endsWithNewLine(DB);
        final String prefix = needsLeadingNewLine ? System.lineSeparator() : "";
        Files.writeString(
                DB,
                prefix + line + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    private static boolean endsWithNewLine(final Path path) throws IOException {
        try (var channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            final var lastByte = java.nio.ByteBuffer.allocate(1);
            channel.position(channel.size() - 1);
            channel.read(lastByte);
            final byte value = lastByte.array()[0];
            return value == '\n' || value == '\r';
        }
    }

    //sanitizza i valori CSV sostituendo eventuali punti e virgola con virgole e rimuovendo spazi bianchi iniziali e finali
    private static String sanitizeCsvValue(final String input) {
        return input == null ? "" : input.replace(";", ",").trim();
    }
}
