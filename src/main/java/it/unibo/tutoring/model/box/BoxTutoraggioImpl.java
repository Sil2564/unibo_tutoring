package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalTime;
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
}