package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * Indica se l'inizio programmato dell'annuncio e' gia' passato rispetto
     * all'istante indicato. Un annuncio senza data o ora non scade mai.
     *
     * @param adesso istante di riferimento
     * @return true se data e ora di inizio sono precedenti a {@code adesso}
     */
    boolean isScaduto(LocalDateTime adesso);

    /**
     * @return true se l'annuncio e' scaduto rispetto all'istante corrente
     * @see #isScaduto(LocalDateTime)
     */
    default boolean isScaduto() {
        return isScaduto(LocalDateTime.now());
    }

    /**
     * Indica se data, ora e durata possono ancora essere modificate.
     * La programmazione si blocca appena l'annuncio e' stato eliminato
     * ({@link #isCancellato()}), esiste almeno un candidato attivo oppure e'
     * stato confermato un candidato.
     *
     * @return true se l'autore puo' modificare la programmazione
     */
    boolean puoModificareProgrammazione();

    /**
     * Aggiorna data, ora e durata dell'annuncio. E' consentito solo finche'
     * {@link #puoModificareProgrammazione()} e' true, cioe' quando l'annuncio
     * non e' stato eliminato e non ha candidati ne' un candidato confermato.
     *
     * @param richiedenteMatricola matricola dell'utente che richiede la modifica
     * @param data nuova data della sessione
     * @param ora nuovo orario di inizio
     * @param durataOre nuova durata, compresa tra 1 e 8 ore
     * @throws IllegalStateException se la programmazione non e' modificabile
     *      (annuncio eliminato, con candidature o con un candidato confermato)
     * @throws SecurityException se la modifica non e' richiesta dall'autore
     * @throws IllegalArgumentException se i nuovi valori non sono validi
     */
    void aggiornaProgrammazione(
            String richiedenteMatricola,
            LocalDate data,
            LocalTime ora,
            int durataOre);

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
     * assegnato, eliminato o e' scaduto, se la matricola coincide con
     * l'autore, o se la matricola e' gia' candidata.
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
     * Non ha effetto se la matricola non e' tra i candidati, oppure se
     * l'annuncio e' eliminato o scaduto.
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