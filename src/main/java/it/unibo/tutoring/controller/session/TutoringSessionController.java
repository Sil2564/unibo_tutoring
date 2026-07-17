package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.credit.CompletedSessionRepository;
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
import java.time.format.DateTimeFormatter;

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

    // Dati della recensione salvata per la sessione.
    // reviewStars = -1 indica che non è stata ancora lasciata alcuna valutazione.
    private int reviewStars = -1;
    private String reviewComment = "";
    private String reviewAuthor = "";
    private boolean reviewSaved = false;

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
    public String getStudentName() {
        final var student = AuthService.getInstance().getUser(this.studenteMatricola);
        return student != null ? student.getName() + " " + student.getSurname() : this.studenteMatricola;
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

        final int completedHours = (int) this.model.getDurata().toHours();
        final int creditsGiven = completedHours / 2;
        final String subject = this.model.getMateria();
        final String date = this.model.getDataOra().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        final String studentName = getStudentName();

        CompletedSessionRepository.saveCompletedSession(
            studentName,
            subject,
            date,
            completedHours,
            creditsGiven,
            this.tutorMatricola
        );

        setStatoCorrente(new CompletedState());
        it.unibo.tutoring.AppConfig.getInstance()
            .getEventBus()
            .publish(
                new it.unibo.tutoring.event.SessionCompletedEvent(
                    this.tutorMatricola,
                    completedHours
                )
            );
        salvaSuFile();
    }

    public void inviaMessaggio(final String testo) {
        if (testo != null && !testo.trim().isEmpty()) {
            this.model.inviaMessaggio(testo.trim(), this.userMatricola);
            salvaSuFile();
        }
    }

    // Metodo helper per salvare una recensione senza autore esplicito.
    public void registraRecensione(final int stelle, final String commento) {
        registraRecensione(stelle, commento, "");
    }

    // Registra la recensione nella sessione e la persiste su file.
    public void registraRecensione(
            final int stelle,
            final String commento,
            final String autoreRecensione) {
        if (stelle < 0 || stelle > 5) {
            throw new IllegalArgumentException("Il valore delle stelle deve essere tra 0 e 5.");
        }
        this.reviewStars = stelle;
        this.reviewComment = commento != null ? commento.trim() : "";
        this.reviewAuthor = autoreRecensione != null ? autoreRecensione.trim() : "";
        this.reviewSaved = true;
        salvaSuFile();
    }

    public int getReviewStars() {
        return this.reviewStars;
    }

    public String getReviewComment() {
        return this.reviewComment;
    }

    public boolean isReviewSaved() {
        return this.reviewSaved;
    }

    public boolean shouldAskForReview() {
        return this.model.getStatoCorrente() instanceof CompletedState && !this.reviewSaved;
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
                } else if (line.startsWith("REVIEW;")) {
                    // Legge i dati della recensione salvata se presenti.
                    ripristinaRecensione(line.substring("REVIEW;".length()));
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

    // Ripristina i valori salvati della recensione da disco.
    private void ripristinaRecensione(final String payload) {
        final String[] campi = payload.split("\\|", 3);
        if (campi.length < 2) {
            return;
        }

        try {
            this.reviewStars = Integer.parseInt(campi[0].trim());
        } catch (NumberFormatException ignored) {
            return;
        }

        this.reviewComment = campi[1].trim();
        this.reviewAuthor = campi.length == 3 ? campi[2].trim() : "";
        this.reviewSaved = true;
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

                if (this.reviewSaved) {
                    // Salva la recensione in fondo al file con formato REVIEW;stelle|commento
                    writer.write("REVIEW;" + this.reviewStars + "|" + sanitizeReviewComment(this.reviewComment));
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

    private static String sanitizeReviewComment(final String comment) {
        if (comment == null) {
            return "";
        }
        return comment.replace("\n", " ").replace("\r", " ").replace("|", "/");
    }

    private static String requireText(final String value, final String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " non puo' essere vuoto.");
        }
        return value.trim();
    }
}
