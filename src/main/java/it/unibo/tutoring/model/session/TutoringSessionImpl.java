package it.unibo.tutoring.model.session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class TutoringSessionImpl implements TutoringSession {

    private final UUID id;
    private final String materia;
    private final LocalDateTime dataOra;
    private final Duration durata;

    // Questa variabile tiene traccia dello stato attuale
    private SessionState statoCorrente;

    public TutoringSessionImpl(String materia, LocalDateTime dataOra, Duration durata) {
        this.id = UUID.randomUUID();
        this.materia = materia;
        this.dataOra = dataOra;
        this.durata = durata;

        // Appena creata, la sessione è Proposta
        this.statoCorrente = new ProposedState();
    }

    // Metodo aggiuntivo usato dagli Stati per cambiare lo stato della sessione
    public void setStatoCorrente(SessionState nuovoStato) {
        this.statoCorrente = nuovoStato;
    }

    @Override
    public UUID getId() { return this.id; }

    @Override
    public String getMateria() { return this.materia; }

    @Override
    public LocalDateTime getDataOra() { return this.dataOra; }

    @Override
    public Duration getDurata() { return this.durata; }

    @Override
    public SessionState getStatoCorrente() {
        return this.statoCorrente;
    }

    @Override
    public void conferma() {
        this.statoCorrente.conferma(this);
    }

    @Override
    public void annulla() {
        this.statoCorrente.annulla(this);
    }

    @Override
    public void completa() {
        this.statoCorrente.completa(this);
    }
}