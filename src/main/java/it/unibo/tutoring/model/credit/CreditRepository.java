package it.unibo.tutoring.model.credit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class CreditRepository {

    private static final Path DB = Paths.get("data", "credits.csv");
    private static final String SEP = ";";

    private CreditRepository() {
    }

    /**
     * Carica il record associato alla matricola, se presente.
     *
     * @param matricola id utente
     * @return Optional con il CreditRecord, vuoto se non presente o in caso di errori
     */
    public static Optional<CreditRecord> loadRecord(final String matricola) {
        try {
            if (!Files.exists(DB)) {
                return Optional.empty();
            }

            final List<String> lines = Files.readAllLines(DB);
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                if (line.startsWith("matricola")) {
                    continue; // header
                }
                //ignora le righe malformate, ma non interrompe la lettura
                final String[] parts = line.split(SEP, -1);
                if (parts.length < 7) {
                    continue;
                }

                if (parts[0].equals(matricola)) {
                    final int totalHours = Integer.parseInt(parts[3]);
                    final int totalCredits = Integer.parseInt(parts[4]);
                    final Badge badge = Badge.valueOf(parts[5]);    //coverte i valori in enum
                    final double rating = Double.parseDouble(parts[6]); //converte la stringa della valutazioni in valore decimale
                    //crea un nuovo CreditRecord con i valori letti dal file e lo restituisce
                    return Optional.of(new CreditRecord(
                        totalHours,
                        totalCredits,
                        badge,
                        rating,
                        0 // nextLevelHours recalculated by Service
                    ));
                }
            }
            //cattura e ignora eventuali eccezioni di I/O o di runtime, restituendo un Optional vuoto in caso di errore
        } catch (final IOException | RuntimeException e) {
        }

        return Optional.empty();
    }

    /**
     * Salva o aggiorna il record per la matricola fornita. Se esiste una riga
     * precedente per la matricola, ne preserva i campi `firstName`/`lastName`.
     * L'implementazione riscrive l'intero file con header.
     * @param matricola id utente
     * @param record dati da salvare
     * 
     */
    public static synchronized void saveRecord(
        final String matricola,
        final CreditRecord record   //parametro in lettura, rappresenta il record di credito da salvare o aggiornare per la matricola specificata
    ) {
        try {
            /*  Operatore ternario: controlla se il file puntato da DB esiste sul disco.
                Se esiste (Files.exists(DB) == true), legge tutte le righe con Files.readAllLines(DB) 
                e le inserisce in un'ArrayList modificabile.
                Se non esiste, inizializza una nuova ArrayList vuota.*/
            final List<String> lines = Files.exists(DB)
                ? new ArrayList<>(Files.readAllLines(DB))
                : new ArrayList<>();

                // crea una mappa per memorizzare le righe esistenti, con la matricola come chiave e la riga completa come valore
            final Map<String, String> map = new LinkedHashMap<>();
            // preserve existing order and names
            for (final String line : lines) {
                if (line == null || line.trim().isEmpty()) {    //se riga vuota, passa alla prossima
                    continue;
                }
                if (line.startsWith("matricola")) { //controlla se la riga è l'header del file, se sì, passa alla prossima riga
                    continue;
                }   //separa la riga in parti usando il separatore definito (SEP) e aggiunge la riga alla mappa solo se contiene almeno una parte
                final String[] parts = line.split(SEP, -1);
                if (parts.length >= 1) {
                    map.put(parts[0], line);
                }
            }
            // inizializza le variabili first e last (nome e cognome) come stringhe vuote
            String first = "";
            String last = "";
            // Cerca nella mappa se esiste già una riga CSV memorizzata per questa matricola.
            final String existing = map.get(matricola);
            if (existing != null) {
                //se esiste una riga esistente per la matricola, la divide in parti usando il separatore 
                // e assegna i valori di first e last dalle posizioni appropriate dell'array risultante
                final String[] p = existing.split(SEP, -1);
                if (p.length >= 3) {
                    first = p[1];
                    last = p[2];
                }
            }
            // unisce tutti i singoli campi in un'unica stringa separata dal carattere SEP per formare la riga CSV
            final String newLine = String.join(SEP,
                matricola,
                first,
                last,
                Integer.toString(record.getTotalHours()),
                Integer.toString(record.getTotalCredits()),
                record.getBadge().name(),
                Double.toString(record.getRating())
            );
            // inserisce o aggiorna la riga associata alla matricola all'interno della LinkedHashMap
            map.put(matricola, newLine);
            //crea una nuova lista di stringhe per memorizzare le righe da scrivere nel file CSV, 
            //aggiungendo prima l'header e poi tutte le righe presenti nella mappa
            final List<String> out = new ArrayList<>();
            out.add("matricola;firstName;lastName;totalHours;totalCredits;badge;rating");
            out.addAll(map.values());
            //se la directory padre del file DB non esiste, la crea prima di scrivere il file. 
            //Infine, scrive tutte le righe nella lista out nel file CSV specificato da DB
            if (DB.getParent() != null) {
                Files.createDirectories(DB.getParent());
            }
            //scrive tutte le righe nella lista out nel file CSV specificato da DB
            Files.write(DB, out);
            //cattura e ignora eventuali eccezioni di I/O o di runtime, senza interrompere l'esecuzione del programma
        } catch (final IOException e) {
        }
    }
}
