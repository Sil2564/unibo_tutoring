package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.session.CompletedState;
import it.unibo.tutoring.model.session.ConfirmedState;
import it.unibo.tutoring.model.session.ProposedState;
import it.unibo.tutoring.model.session.SessionState;
import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.model.session.TutoringSessionImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class TutoringSessionController {

    private static final Path SESSION_FOLDER = Path.of("data", "sessions");
    private static final String FILE_PREFIX = "SESS_";
    private static final String FILE_EXTENSION = ".csv";
    private static final int DEFAULT_COMPLETED_HOURS = 1;

    private final TutoringSession model;
    private final String materia;
    private final String nomeInserzionista;
    private final boolean tutorOffer;
    private final String userMatricola;
    private final String tutorMatricola;
    private final String studenteMatricola;
    private final Path fileCondivisoPath;

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String matricolaInserzionista,
            final String userMatricola) {

        this.materia = requireText(materia, "materia");
        this.nomeInserzionista = requireText(nomeInserzionista, "nomeInserzionista");
        this.tutorOffer = tutorOffer;
        final String inserzionista = requireText(matricolaInserzionista, "matricolaInserzionista");
        this.userMatricola = requireText(userMatricola, "userMatricola");

        if (this.tutorOffer) {
            this.tutorMatricola = inserzionista;
            this.studenteMatricola = this.userMatricola;
        } else {
            this.tutorMatricola = this.userMatricola;
            this.studenteMatricola = inserzionista;
        }

        this.model = new TutoringSessionImpl(
                this.materia,
                LocalDateTime.now(),
                Duration.ofHours(DEFAULT_COMPLETED_HOURS),
                this.tutorMatricola);

        this.fileCondivisoPath = SESSION_FOLDER.resolve(buildFileName());
        caricaDaFileSePresente();
    }

    public String getRuoloInserzionista() {
        return this.tutorOffer ? "Tutor:" : "Studente:";
    }

    public String getNomeInserzionista() {
        return this.nomeInserzionista;
    }

    public String getTutorMatricola() {
        return this.tutorMatricola;
    }

    public String getUserMatricola() {
        return this.userMatricola;
    }

    public TutoringSession getModel() {
        return this.model;
    }

    public void confermaSessione() {
        this.model.conferma();
        salvaSuFile();
    }

    public void completaSessione() {
        if (this.model.getStatoCorrente() instanceof CompletedState) {
            throw new IllegalStateException("La sessione e' gia' stata completata.");
        }
        if (!(this.model.getStatoCorrente() instanceof ConfirmedState)) {
            throw new IllegalStateException("Solo una sessione confermata puo essere completata.");
        }
        setStatoCorrente(new CompletedState());
        CreditService.addCompletedHours(this.tutorMatricola, DEFAULT_COMPLETED_HOURS);
        salvaSuFile();
    }

    public void inviaMessaggio(final String testo) {
        if (testo != null && !testo.trim().isEmpty()) {
            this.model.inviaMessaggio(testo.trim(), this.userMatricola);
            salvaSuFile();
        }
    }

    private void caricaDaFileSePresente() {
        if (!Files.exists(this.fileCondivisoPath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(this.fileCondivisoPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("STATO;")) {
                    ripristinaStato(line.substring("STATO;".length()));
                } else if (line.startsWith("MSG;")) {
                    ripristinaMessaggio(line.substring("MSG;".length()));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile leggere la sessione da " + this.fileCondivisoPath, e);
        }
    }

    private void ripristinaStato(final String statoSalvato) {
        switch (statoSalvato.trim()) {
            case "Proposed" -> setStatoCorrente(new ProposedState());
            case "Confirmed" -> setStatoCorrente(new ConfirmedState());
            case "Completed" -> setStatoCorrente(new CompletedState());
            default -> throw new IllegalArgumentException("Stato sessione non riconosciuto: " + statoSalvato);
        }
    }

    private void ripristinaMessaggio(final String payload) {
        final String[] campi = payload.split("\\|", 2);
        if (campi.length != 2) {
            return;
        }

        final String mittenteID = campi[0];
        final String testo = campi[1];
        if (!mittenteID.isBlank() && !testo.isBlank()) {
            this.model.inviaMessaggio(testo, mittenteID);
        }
    }

    private void salvaSuFile() {
        try {
            Files.createDirectories(SESSION_FOLDER);

            try (BufferedWriter writer = Files.newBufferedWriter(this.fileCondivisoPath, StandardCharsets.UTF_8)) {
                writer.write("STATO;" + getNomeStatoCorrente());
                writer.newLine();

                for (Message message : this.model.getStoricoChat()) {
                    writer.write("MSG;" + message.getIdMittente() + "|" + message.getTesto());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile salvare la sessione in " + this.fileCondivisoPath, e);
        }
    }

    private void setStatoCorrente(final SessionState stato) {
        if (this.model instanceof TutoringSessionImpl sessionImpl) {
            sessionImpl.setStatoCorrente(stato);
            return;
        }
        throw new IllegalStateException("Il modello non permette di ripristinare lo stato della sessione.");
    }

    private String getNomeStatoCorrente() {
        return this.model.getStatoCorrente().getClass().getSimpleName().replace("State", "");
    }

    private String buildFileName() {
        final String materiaSenzaSpazi = this.materia.replaceAll("\\s+", "");
        return FILE_PREFIX + materiaSenzaSpazi + "_" + this.tutorMatricola + "_" + this.studenteMatricola + FILE_EXTENSION;
    }

    private static String requireText(final String value, final String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " non puo' essere vuoto.");
        }
        return value.trim();
    }
}
