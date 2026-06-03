package it.unibo.tutoring.model.box;

import java.util.UUID;

public class BoxTutoraggioImpl
    implements BoxTutoraggio {

    private final UUID id;

    private final String titolo;

    private final String materia;

    private final String descrizione;

    private final String autoreMatricola;

    private final BoxType tipo;

    public BoxTutoraggioImpl(
        final String titolo,
        final String materia,
        final String descrizione,
        final String autoreMatricola,
        final BoxType tipo
    ) {

        this.id = UUID.randomUUID();

        this.titolo = titolo;

        this.materia = materia;

        this.descrizione = descrizione;

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
    public String getMateria() {
        return this.materia;
    }

    @Override
    public String getDescrizione() {
        return this.descrizione;
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