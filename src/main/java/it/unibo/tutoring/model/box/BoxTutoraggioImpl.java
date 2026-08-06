package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BoxTutoraggioImpl
    implements BoxTutoraggio {

    private final UUID id;

    private final String titolo;

    private final String corso;

    private final String materia;

    private final String argomento;

    private final LocalDate data;

    private final LocalTime ora;

    private final int durataOre;

    private final String autoreMatricola;

    private final BoxType tipo;

    private final List<String> candidati = new ArrayList<>();

    private final List<String> contatti = new ArrayList<>();

    private String confermato;

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
        final List<String> contattiIniziali
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
        if (candidatiIniziali != null) {
            this.candidati.addAll(candidatiIniziali);
        }
        this.confermato = confermatoIniziale;
        if (contattiIniziali != null) {
            this.contatti.addAll(contattiIniziali);
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
    }

    @Override
    public void confermaCandidato(final String matricola) {
        if (matricola == null || !this.candidati.contains(matricola)) {
            return;
        }
        this.confermato = matricola;
        this.candidati.remove(matricola);
    }
}