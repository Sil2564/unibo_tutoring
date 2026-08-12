package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
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
     * Matricole di chi ha cliccato "Candidati" su questo annuncio e non e' ancora
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
     * Indica se data, ora e durata possono ancora essere modificate.
     * L'autore puo' modificare la programmazione in qualsiasi momento, anche
     * dopo che sono arrivate candidature o dopo la conferma di un
     * candidato: l'unico caso in cui non e' piu' possibile e' quando
     * l'annuncio e' stato eliminato ({@link #isCancellato()}).
     *
     * @return true se l'autore puo' modificare la programmazione
     */
    boolean puoModificareProgrammazione();

    /**
     * Aggiorna data, ora e durata dell'annuncio. Puo' essere invocato anche
     * quando esistono gia' candidature o un candidato confermato: in tal
     * caso chi era coinvolto (candidati in attesa e/o candidato confermato)
     * viene automaticamente marcato come "in attesa di riconferma" tramite
     * {@link #getInAttesaDiRiconferma()}, cosi' che la UI possa notificarlo e
     * chiedergli di confermare nuovamente la propria disponibilita' alla
     * nuova data/ora.
     *
     * @param richiedenteMatricola matricola dell'utente che richiede la modifica
     * @param data nuova data della sessione
     * @param ora nuovo orario di inizio
     * @param durataOre nuova durata, compresa tra 1 e 8 ore
     * @throws IllegalStateException se l'annuncio e' stato eliminato
     * @throws SecurityException se la modifica non e' richiesta dall'autore
     * @throws IllegalArgumentException se i nuovi valori non sono validi
     */
    void aggiornaProgrammazione(
            String richiedenteMatricola,
            LocalDate data,
            LocalTime ora,
            int durataOre);

    /**
     * Matricole di chi (candidato in attesa e/o candidato confermato) deve
     * riconfermare la propria disponibilita' a seguito di una modifica di
     * data/ora effettuata dall'autore dopo che si era gia' candidato o era
     * gia' stato confermato.
     */
    Set<String> getInAttesaDiRiconferma();

    /**
     * @param matricola matricola da controllare
     * @return true se {@code matricola} deve riconfermare la propria
     *      disponibilita' alla programmazione corrente
     */
    boolean isInAttesaDiRiconferma(String matricola);

    /**
     * Riconferma la disponibilita' di {@code matricola} alla programmazione
     * corrente (data/ora/durata), rimuovendolo dall'elenco di chi e' in
     * attesa di riconferma. Non ha alcun effetto se la matricola non era in
     * attesa.
     */
    void riconfermaProgrammazione(String matricola);

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

    /**
     * Nota libera facoltativa scritta dall'autore al momento della
     * pubblicazione dell'annuncio (es. preferenze, materiale da portare).
     * Vuota se non specificata. Non modificabile dopo la pubblicazione.
     */
    String getNote();

    /**
     * @return true se l'autore ha eliminato l'annuncio ma questo e' ancora
     *      in fase di "grazia" di 24 ore (perche' esisteva gia' una sessione
     *      confermata al momento dell'eliminazione)
     */
    boolean isCancellato();

    /**
     * @return l'istante in cui l'annuncio e' stato eliminato, oppure null se
     *      non e' mai stato eliminato
     */
    LocalDateTime getCancellatoAt();

    /**
     * Richiede l'eliminazione dell'annuncio da parte dell'autore.
     * <p>
     * Se non esisteva ancora nessun candidato confermato, l'eliminazione e'
     * definitiva e immediata: il metodo restituisce {@code true} e sta al
     * chiamante rimuovere fisicamente l'annuncio dal repository, dato che il
     * modello da solo non puo' farlo sparire dalle liste.
     * <p>
     * Se invece esisteva gia' un candidato confermato, l'annuncio viene
     * marcato come "cancellato" ma resta visibile (con notifica) a chi era
     * coinvolto per 24 ore, dopodiche' andra' rimosso definitivamente: in
     * questo caso il metodo restituisce {@code false}.
     *
     * @param richiedenteMatricola matricola dell'utente che richiede l'eliminazione
     * @return true se il chiamante deve rimuovere definitivamente l'annuncio subito,
     *      false se e' stata invece applicata una cancellazione "soft" con preavviso
     * @throws SecurityException se la richiesta non arriva dall'autore
     */
    boolean eliminaAnnuncio(String richiedenteMatricola);

    /**
     * @param matricola matricola da controllare
     * @return true se {@code matricola} ha gia' preso visione della
     *      notifica di eliminazione di questo annuncio
     */
    boolean isCancellazioneVista(String matricola);

    /**
     * Segna che {@code matricola} ha preso visione della notifica di
     * eliminazione di questo annuncio (usato per far sparire il simbolo di
     * notifica una volta aperto l'annuncio).
     */
    void segnaCancellazioneVista(String matricola);
}