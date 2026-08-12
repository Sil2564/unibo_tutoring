package it.unibo.tutoring.model.box;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository degli annunci di tutoraggio. Mantiene gli annunci in memoria per
 * un accesso rapido, ma li persiste anche su {@code data/boxes.csv} (stesso
 * pattern gia' usato da CreditRepository/ReviewRepository/CompletedSessionRepository),
 * cosi' che sopravvivano al riavvio dell'applicazione.
 */
public final class BoxRepository {

    private static final Path DB = Paths.get("data", "boxes.csv");
    private static final String SEP = ";";
    private static final String LIST_SEP = ",";
    private static final String HEADER =
        "id;titolo;corso;materia;argomento;data;ora;durataOre;autoreMatricola;tipo;candidati;confermato;contatti;note"
        + ";cancellato;cancellatoAt;daRiconfermare;cancellazioneVistaDa";

    /** Ore dopo le quali un annuncio "cancellato" (soft-delete) sparisce definitivamente. */
    private static final long ORE_GRAZIA_CANCELLAZIONE = 24;

    private static final List<BoxTutoraggio> BOXES = new ArrayList<>();

    static {
        caricaDaFile();
        purgaAnnunciScaduti();
    }

    private BoxRepository() {
    }

    public static synchronized void addBox(
        final BoxTutoraggio box
    ) {
        BOXES.add(box);
        saveAll();
    }

    /**
     * Rimuove definitivamente un annuncio dal repository (usato sia per
     * l'eliminazione immediata di un annuncio senza conferme, sia per la
     * rimozione automatica trascorse le 24 ore di preavviso).
     */
    public static synchronized void removeBox(final BoxTutoraggio box) {
        if (box == null) {
            return;
        }
        BOXES.removeIf(b -> b.getId().equals(box.getId()));
        saveAll();
    }

    public static synchronized List<BoxTutoraggio>
        getAllBoxes() {

        purgaAnnunciScaduti();
        return new ArrayList<>(BOXES);
    }

    /**
     * Rimuove definitivamente gli annunci che sono stati eliminati
     * dall'autore (soft-delete) e per cui sono trascorse piu' di 24 ore
     * dalla cancellazione.
     */
    private static void purgaAnnunciScaduti() {
        final LocalDateTime ora = LocalDateTime.now();
        final boolean rimossoAlmenoUno = BOXES.removeIf(box ->
            box.isCancellato()
                && box.getCancellatoAt() != null
                && box.getCancellatoAt().plusHours(ORE_GRAZIA_CANCELLAZIONE).isBefore(ora));
        if (rimossoAlmenoUno) {
            saveAll();
        }
    }

    /**
     * Riscrive per intero data/boxes.csv con lo stato corrente di tutti gli
     * annunci in memoria. Va richiamato ogni volta che lo stato di un annuncio
     * cambia (nuova candidatura, ritiro, rifiuto, conferma) cosi' che la
     * modifica sopravviva al riavvio dell'applicazione.
     */
    public static synchronized void saveAll() {
        try {
            if (DB.getParent() != null) {
                Files.createDirectories(DB.getParent());
            }

            final List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (final BoxTutoraggio box : BOXES) {
                lines.add(toCsvLine(box));
            }

            Files.write(DB, lines, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            // ignora errori di scrittura per non bloccare l'app
        }
    }

    private static String toCsvLine(final BoxTutoraggio box) {
        return String.join(SEP,
            box.getId().toString(),
            sanitize(box.getTitolo()),
            sanitize(box.getCorso()),
            sanitize(box.getMateria()),
            sanitize(box.getArgomento()),
            box.getData() != null ? box.getData().toString() : "",
            box.getOra() != null ? box.getOra().toString() : "",
            Integer.toString(box.getDurataOre()),
            sanitize(box.getAutoreMatricola()),
            box.getTipo().name(),
            String.join(LIST_SEP, box.getCandidati()),
            box.getConfermato() != null ? box.getConfermato() : "",
            String.join(LIST_SEP, box.getContatti()),
            sanitize(box.getNote()),
            Boolean.toString(box.isCancellato()),
            box.getCancellatoAt() != null ? box.getCancellatoAt().toString() : "",
            String.join(LIST_SEP, box.getInAttesaDiRiconferma()),
            String.join(LIST_SEP, cancellazioneVistaDaVisibile(box))
        );
    }

    /**
     * Espone le matricole che hanno gia' visto la notifica di eliminazione,
     * cosi' da poterle serializzare senza esporre un metodo dedicato
     * nell'interfaccia del dominio per un solo utilizzo interno.
     */
    private static List<String> cancellazioneVistaDaVisibile(final BoxTutoraggio box) {
        final List<String> visti = new ArrayList<>();
        if (box.getAutoreMatricola() != null && box.isCancellazioneVista(box.getAutoreMatricola())) {
            visti.add(box.getAutoreMatricola());
        }
        if (box.getConfermato() != null && box.isCancellazioneVista(box.getConfermato())) {
            visti.add(box.getConfermato());
        }
        return visti;
    }

    private static void caricaDaFile() {
        try {
            if (!Files.exists(DB)) {
                return;
            }

            final List<String> lines = Files.readAllLines(DB, StandardCharsets.UTF_8);
            for (final String line : lines) {
                if (line == null || line.isBlank() || line.startsWith("id" + SEP)) {
                    continue; // header o riga vuota
                }

                final String[] parts = line.split(SEP, -1);
                if (parts.length < 12) {
                    continue;
                }

                try {
                    final UUID id = UUID.fromString(parts[0].trim());
                    final String titolo = parts[1];
                    final String corso = parts[2];
                    final String materia = parts[3];
                    final String argomento = parts[4];
                    final LocalDate data = parts[5].isBlank() ? null : LocalDate.parse(parts[5].trim());
                    final LocalTime ora = parts[6].isBlank() ? null : LocalTime.parse(parts[6].trim());
                    final int durataOre = Integer.parseInt(parts[7].trim());
                    final String autoreMatricola = parts[8];
                    final BoxType tipo = BoxType.valueOf(parts[9].trim());
                    final List<String> candidati = parseLista(parts[10]);
                    final String confermato = parts[11].isBlank() ? null : parts[11].trim();
                    // Colonne aggiunte in un secondo momento: file salvati prima
                    // di allora potrebbero non averle, le trattiamo come vuote.
                    final List<String> contatti = parts.length > 12 ? parseLista(parts[12]) : List.of();
                    final String note = parts.length > 13 ? unescapeFromCsv(parts[13]) : "";
                    final boolean cancellato = parts.length > 14 && Boolean.parseBoolean(parts[14].trim());
                    final LocalDateTime cancellatoAt = parts.length > 15 && !parts[15].isBlank()
                        ? LocalDateTime.parse(parts[15].trim())
                        : null;
                    final List<String> daRiconfermare = parts.length > 16 ? parseLista(parts[16]) : List.of();
                    final List<String> cancellazioneVistaDa = parts.length > 17 ? parseLista(parts[17]) : List.of();

                    BOXES.add(new BoxTutoraggioImpl(
                        id, titolo, corso, materia, argomento, data, ora, durataOre,
                        autoreMatricola, tipo, candidati, confermato, contatti, note,
                        cancellato, cancellatoAt, daRiconfermare, cancellazioneVistaDa
                    ));
                } catch (final IllegalArgumentException | java.time.format.DateTimeParseException ex) {
                    // riga corrotta: la saltiamo senza bloccare il caricamento delle altre
                }
            }
        } catch (final IOException e) {
            // se il file non e' leggibile si parte semplicemente senza annunci pregressi
        }
    }

    private static List<String> parseLista(final String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(LIST_SEP))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }

    private static String sanitize(final String value) {
        if (value == null) {
            return "";
        }
        // Non eliminiamo gli a-capo: li "escapiamo" cosi' una nota su piu'
        // righe si legge per intero anche dopo essere stata salvata su
        // un'unica riga di data/boxes.csv.
        return value.replace(SEP, ",")
            .replace("\r\n", "\n")
            .replace("\n", "\\n")
            .trim();
    }

    private static String unescapeFromCsv(final String value) {
        return value == null ? "" : value.replace("\\n", "\n");
    }
}