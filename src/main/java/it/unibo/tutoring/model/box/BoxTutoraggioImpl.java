package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BoxTutoraggioImpl
    implements BoxTutoraggio {

    private final UUID id;

    private final String titolo;

    private final String corso;

    private final String materia;

    private final String argomento;

    private LocalDate data;

    private LocalTime ora;

    private int durataOre;

    private final String autoreMatricola;

    private final BoxType tipo;

    private final String note;

    private final List<String> candidati = new ArrayList<>();

    private final List<String> contatti = new ArrayList<>();

    private String confermato;

    /** Matricole di chi deve riconfermare la propria disponibilita' dopo un cambio data/ora. */
    private final Set<String> daRiconfermare = new LinkedHashSet<>();

    private boolean cancellato;

    private LocalDateTime cancellatoAt;

    /** Matricole che hanno gia' visto la notifica di eliminazione dell'annuncio. */
    private final Set<String> cancellazioneVistaDa = new LinkedHashSet<>();

    public BoxTutoraggioImpl(
        final String titolo,
        final String corso,
        final String materia,
        final String argomento,
        final LocalDate data,
        final LocalTime ora,
        final int durataOre,
        final String autoreMatricola,
        final BoxType tipo
    ) {
        this(titolo, corso, materia, argomento, data, ora, durataOre, autoreMatricola, tipo, "");
    }

    public BoxTutoraggioImpl(
        final String titolo,
        final String corso,
        final String materia,
        final String argomento,
        final LocalDate data,
        final LocalTime ora,
        final int durataOre,
        final String autoreMatricola,
        final BoxType tipo,
        final String note
    ) {

        this.id = UUID.randomUUID();

        this.titolo = titolo;

        this.corso = corso;

        this.materia = materia;

        this.argomento = argomento;

        this.data = data;

        this.ora = ora;

        this.durataOre = durataOre;

        this.autoreMatricola = autoreMatricola;

        this.tipo = tipo;

        this.note = note == null ? "" : note;
    }

    /**
     * Costruttore "di ricostruzione" usato esclusivamente da {@link BoxRepository}
     * per ripristinare un annuncio gia' esistente da {@code data/boxes.csv}
     * (id, candidati e candidato confermato inclusi), senza passare per la
     * logica di validazione dei metodi pubblici aggiungiCandidato/confermaCandidato.
     */
    BoxTutoraggioImpl(
        final UUID id,
        final String titolo,
        final String corso,
        final String materia,
        final String argomento,
        final LocalDate data,
        final LocalTime ora,
        final int durataOre,
        final String autoreMatricola,
        final BoxType tipo,
        final List<String> candidatiIniziali,
        final String confermatoIniziale,
        final List<String> contattiIniziali,
        final String note
    ) {
        this(id, titolo, corso, materia, argomento, data, ora, durataOre, autoreMatricola, tipo,
                candidatiIniziali, confermatoIniziale, contattiIniziali, note,
                false, null, List.of(), List.of());
    }

    /**
     * Costruttore "di ricostruzione" completo, usato da {@link BoxRepository}
     * per ripristinare anche lo stato di eliminazione/riconferma da
     * {@code data/boxes.csv}.
     */
    BoxTutoraggioImpl(
        final UUID id,
        final String titolo,
        final String corso,
        final String materia,
        final String argomento,
        final LocalDate data,
        final LocalTime ora,
        final int durataOre,
        final String autoreMatricola,
        final BoxType tipo,
        final List<String> candidatiIniziali,
        final String confermatoIniziale,
        final List<String> contattiIniziali,
        final String note,
        final boolean cancellato,
        final LocalDateTime cancellatoAt,
        final List<String> daRiconfermareIniziali,
        final List<String> cancellazioneVistaDaIniziali
    ) {
        this.id = id;
        this.titolo = titolo;
        this.corso = corso;
        this.materia = materia;
        this.argomento = argomento;
        this.data = data;
        this.ora = ora;
        this.durataOre = durataOre;
        this.autoreMatricola = autoreMatricola;
        this.tipo = tipo;
        this.note = note == null ? "" : note;
        if (candidatiIniziali != null) {
            this.candidati.addAll(candidatiIniziali);
        }
        this.confermato = confermatoIniziale;
        if (contattiIniziali != null) {
            this.contatti.addAll(contattiIniziali);
        }
        this.cancellato = cancellato;
        this.cancellatoAt = cancellatoAt;
        if (daRiconfermareIniziali != null) {
            this.daRiconfermare.addAll(daRiconfermareIniziali);
        }
        if (cancellazioneVistaDaIniziali != null) {
            this.cancellazioneVistaDa.addAll(cancellazioneVistaDaIniziali);
        }
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getTitolo() {
        return this.titolo;
    }

    @Override
    public String getCorso() {
        return this.corso;
    }

    @Override
    public String getMateria() {
        return this.materia;
    }

    @Override
    public String getArgomento() {
        return this.argomento;
    }

    @Override
    public LocalDate getData() {
        return this.data;
    }

    @Override
    public LocalTime getOra() {
        return this.ora;
    }

    @Override
    public int getDurataOre() {
        return this.durataOre;
    }

    @Override
    public String getAutoreMatricola() {
        return this.autoreMatricola;
    }

    @Override
    public BoxType getTipo() {
        return this.tipo;
    }

    @Override
    public List<String> getCandidati() {
        return Collections.unmodifiableList(this.candidati);
    }

    @Override
    public String getConfermato() {
        return this.confermato;
    }

    @Override
    public boolean isCandidato(final String matricola) {
        return matricola != null && this.candidati.contains(matricola);
    }

    @Override
    public boolean puoModificareProgrammazione() {
        // La programmazione si blocca non solo se l'annuncio e' stato eliminato,
        // ma anche appena esiste un candidato attivo o una conferma: in quei casi
        // cambiare data/ora richiederebbe una riconferma esplicita, gestita da
        // aggiornaProgrammazione() tramite daRiconfermare.
        return !this.cancellato && this.candidati.isEmpty() && this.confermato == null;
    }

    @Override
    public void aggiornaProgrammazione(
            final String richiedenteMatricola,
            final LocalDate nuovaData,
            final LocalTime nuovaOra,
            final int nuovaDurataOre) {
        if (!this.autoreMatricola.equals(richiedenteMatricola)) {
            throw new SecurityException("Solo l'autore puo' modificare la programmazione.");
        }
        if (!puoModificareProgrammazione()) {
            throw new IllegalStateException(
                    "Un annuncio eliminato non puo' piu' essere modificato.");
        }
        if (nuovaData == null || nuovaOra == null) {
            throw new IllegalArgumentException("Data e ora sono obbligatorie.");
        }
        if (nuovaDurataOre < 1 || nuovaDurataOre > 8) {
            throw new IllegalArgumentException("La durata deve essere compresa tra 1 e 8 ore.");
        }
        if (LocalDateTime.of(nuovaData, nuovaOra).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e ora devono essere successive a quelle attuali.");
        }

        // Chi era gia' candidato o gia' confermato deve riconfermare la propria
        // disponibilita' alla nuova programmazione: li segnamo prima di
        // applicare il cambiamento, cosi' la UI puo' notificarli.
        this.daRiconfermare.addAll(this.candidati);
        if (this.confermato != null) {
            this.daRiconfermare.add(this.confermato);
        }

        this.data = nuovaData;
        this.ora = nuovaOra;
        this.durataOre = nuovaDurataOre;
    }

    @Override
    public Set<String> getInAttesaDiRiconferma() {
        return Collections.unmodifiableSet(this.daRiconfermare);
    }

    @Override
    public boolean isInAttesaDiRiconferma(final String matricola) {
        return matricola != null && this.daRiconfermare.contains(matricola);
    }

    @Override
    public void riconfermaProgrammazione(final String matricola) {
        if (matricola != null) {
            this.daRiconfermare.remove(matricola);
        }
    }

    @Override
    public List<String> getContatti() {
        return Collections.unmodifiableList(this.contatti);
    }

    @Override
    public void aggiungiContatto(final String matricola) {
        if (matricola == null || matricola.isBlank()) {
            return;
        }
        if (matricola.equals(this.autoreMatricola)) {
            return;
        }
        if (!this.contatti.contains(matricola)) {
            this.contatti.add(matricola);
        }
    }

    @Override
    public void aggiungiCandidato(final String matricola) {
        if (matricola == null || matricola.isBlank()) {
            return;
        }
        if (this.confermato != null) {
            return;
        }
        if (matricola.equals(this.autoreMatricola)) {
            return;
        }
        if (!this.candidati.contains(matricola)) {
            this.candidati.add(matricola);
        }
    }

    @Override
    public void rimuoviCandidato(final String matricola) {
        this.candidati.remove(matricola);
        // Chi ritira/viene rifiutato non e' piu' coinvolto nella programmazione.
        this.daRiconfermare.remove(matricola);
    }

    @Override
    public void confermaCandidato(final String matricola) {
        if (matricola == null || !this.candidati.contains(matricola)) {
            return;
        }
        this.confermato = matricola;
        this.candidati.remove(matricola);
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public boolean isCancellato() {
        return this.cancellato;
    }

    @Override
    public LocalDateTime getCancellatoAt() {
        return this.cancellatoAt;
    }

    @Override
    public boolean eliminaAnnuncio(final String richiedenteMatricola) {
        if (!this.autoreMatricola.equals(richiedenteMatricola)) {
            throw new SecurityException("Solo l'autore puo' eliminare l'annuncio.");
        }
        if (this.cancellato) {
            // Gia' eliminato: nessuna ulteriore azione.
            return false;
        }
        if (this.confermato == null) {
            // Nessuna conferma presente: eliminazione definitiva e immediata,
            // sara' il chiamante a rimuovere l'annuncio dal repository.
            return true;
        }
        // Sessione gia' confermata: cancellazione "soft" con preavviso di 24 ore
        // per l'autore e per il candidato confermato.
        this.cancellato = true;
        this.cancellatoAt = LocalDateTime.now();
        this.cancellazioneVistaDa.clear();
        return false;
    }

    @Override
    public boolean isCancellazioneVista(final String matricola) {
        return matricola != null && this.cancellazioneVistaDa.contains(matricola);
    }

    @Override
    public void segnaCancellazioneVista(final String matricola) {
        if (matricola != null) {
            this.cancellazioneVistaDa.add(matricola);
        }
    }
}