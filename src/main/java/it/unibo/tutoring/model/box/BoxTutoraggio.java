package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BoxTutoraggio {

    UUID getId();

    String getTitolo();

    String getCorso();

    String getMateria();

    String getArgomento();

    LocalDate getData();

    LocalTime getOra();

    int getDurataOre();

    String getAutoreMatricola();

    BoxType getTipo();

    /**
     * Matricole di chi ha cliccato "Accetta" su questo annuncio e non e' ancora
     * stato confermato ne' rifiutato/ritirato.
     */
    List<String> getCandidati();

    /**
     * Matricola del candidato confermato dall'autore, oppure null se l'annuncio
     * e' ancora aperto a nuove candidature.
     */
    String getConfermato();

    boolean isCandidato(String matricola);

    /**
     * Matricole degli utenti che hanno aperto una conversazione con l'autore.
     * Ogni contatto identifica una chat distinta per questo annuncio.
     */
    List<String> getContatti();

    /**
     * Registra un contatto senza trasformarlo automaticamente in candidatura.
     * L'autore dell'annuncio non puo' essere aggiunto come proprio contatto.
     */
    void aggiungiContatto(String matricola);

    /**
     * Aggiunge una candidatura. Non ha effetto se l'annuncio e' gia' stato
     * assegnato, se la matricola coincide con l'autore, o se la matricola e'
     * gia' candidata.
     */
    void aggiungiCandidato(String matricola);

    /**
     * Rimuove una candidatura in attesa (usato sia per "Ritira candidatura" da
     * parte del candidato, sia per "Rifiuta" da parte dell'autore).
     */
    void rimuoviCandidato(String matricola);

    /**
     * Conferma definitivamente un candidato tra quelli in attesa: le altre
     * candidature pendenti restano a carico del chiamante da annullare.
     */
    void confermaCandidato(String matricola);
}