package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.model.credit.CompletedSessionRepository;
import it.unibo.tutoring.model.session.CancelledState;
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
    private final String conversationId;
    private final Path fileCondivisoPath;

    // Dati della recensione salvata per la sessione.
    // reviewStars = -1 indica che non è stata ancora lasciata alcuna valutazione.
    private int reviewStars = -1;
    private String reviewComment = "";
    private String reviewAuthor = "";
    private boolean reviewSaved = false;

    // Flag di completamento indipendenti per ciascun lato: la sessione si
    // considera davvero completata solo quando entrambi sono true.
    private boolean completatoTutor = false;
    private boolean completatoStudente = false;

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String matricolaInserzionista,
            final String userMatricola) {
        this(
                materia,
                nomeInserzionista,
                tutorOffer,
                matricolaInserzionista,
                userMatricola,
                LocalDateTime.now(),
                Duration.ofHours(DEFAULT_COMPLETED_HOURS),
                null
        );
    }

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String matricolaInserzionista,
            final String userMatricola,
            final LocalDateTime dataOra,
            final Duration durata) {
        this(
                materia,
                nomeInserzionista,
                tutorOffer,
                matricolaInserzionista,
                userMatricola,
                dataOra,
                durata,
                null
        );
    }

    public TutoringSessionController(
            final String materia,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String matricolaInserzionista,
            final String userMatricola,
            final LocalDateTime dataOra,
            final Duration durata,
            final String conversationId) {

        this.materia = requireText(materia, "materia");
        this.nomeInserzionista = requireText(nomeInserzionista, "nomeInserzionista");
        this.tutorOffer = tutorOffer;
        final String inserzionista = requireText(matricolaInserzionista, "matricolaInserzionista");
        this.userMatricola = requireText(userMatricola, "userMatricola");
        this.conversationId = conversationId == null ? "" : conversationId.trim();

        if (this.tutorOffer) {
            this.tutorMatricola = inserzionista;
            this.studenteMatricola = this.userMatricola;
        } else {
            this.tutorMatricola = this.userMatricola;
            this.studenteMatricola = inserzionista;
        }

        this.model = new TutoringSessionImpl(
                this.materia,
                dataOra,
                durata,
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

    public String getStudenteMatricola() {
        return this.studenteMatricola;
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

    /**
     * Crea/persiste la proposta iniziale (stato Proposed) quando un candidato
     * si candida a un annuncio. Se il file esiste gia' (es. ri-candidatura dopo un
     * ritiro) lo stato viene comunque riportato a Proposed.
     */
    public void proponi() {
        if (!(this.model.getStatoCorrente() instanceof ProposedState)) {
            setStatoCorrente(new ProposedState());
        }
        salvaSuFile();
    }

    public void confermaSessione() {
        this.model.conferma();
        salvaSuFile();
    }

    /**
     * Annulla la candidatura/sessione: usato sia per "Ritira candidatura" da
     * parte del candidato, sia per "Rifiuta" da parte dell'autore.
     */
    public void annullaSessione() {
        this.model.annulla();
        salvaSuFile();
    }

    /**
     * L'utente corrente segnala che, dal suo lato, la sessione si e' svolta.
     * Solo quando ENTRAMBI i lati hanno segnalato il completamento la sessione
     * passa realmente a Completed (con relativo accredito ore/crediti).
     */
    public void segnalaCompletamento() {
        if (this.model.getStatoCorrente() instanceof CompletedState) {
            throw new IllegalStateException("La sessione e' gia' stata completata.");
        }
        if (!(this.model.getStatoCorrente() instanceof ConfirmedState)) {
            throw new IllegalStateException("Solo una sessione confermata puo' essere completata.");
        }
        if (!puoSegnalareCompletamento()) {
            throw new IllegalStateException(
                    "La sessione puo' essere completata solo dopo la fine prevista: "
                            + getFinePrevista());
        }

        if (this.userMatricola.equals(this.tutorMatricola)) {
            this.completatoTutor = true;
        } else {
            this.completatoStudente = true;
        }

        if (this.completatoTutor && this.completatoStudente) {
            finalizzaCompletamento();
        }

        salvaSuFile();
    }

    private void finalizzaCompletamento() {
        final int completedHours = (int) this.model.getDurata().toHours();
        final var creditService = it.unibo.tutoring.AppConfig.getInstance().getCreditService();
        final var currentCreditRecord = creditService.getCreditRecord(this.tutorMatricola);
        final int creditsGiven =
                (currentCreditRecord.getTotalHours() + completedHours) / 2
                        - currentCreditRecord.getTotalCredits();
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
    }

    public boolean isCompletatoDaTutor() {
        return this.completatoTutor;
    }

    public boolean isCompletatoDaStudente() {
        return this.completatoStudente;
    }

    public boolean isCompletataDaEntrambi() {
        return this.model.getStatoCorrente() instanceof CompletedState;
    }

    /**
     * Istante in cui termina la sessione, calcolato da data/ora iniziale e
     * durata concordata.
     */
    public LocalDateTime getFinePrevista() {
        return this.model.getDataOra().plus(this.model.getDurata());
    }

    /**
     * Il completamento diventa disponibile esattamente alla fine prevista.
     */
    public boolean puoSegnalareCompletamento() {
        return puoSegnalareCompletamento(LocalDateTime.now());
    }

    boolean puoSegnalareCompletamento(final LocalDateTime adesso) {
        if (adesso == null) {
            throw new IllegalArgumentException("L'istante corrente e' obbligatorio.");
        }
        return !adesso.isBefore(getFinePrevista());
    }

    /** True se l'utente corrente ha gia' segnalato il proprio completamento. */
    public boolean haGiaSegnalatoCompletamento() {
        return this.userMatricola.equals(this.tutorMatricola) ? this.completatoTutor : this.completatoStudente;
    }

    public boolean isProposta() {
        return this.model.getStatoCorrente() instanceof ProposedState;
    }

    public boolean isConfermata() {
        return this.model.getStatoCorrente() instanceof ConfirmedState;
    }

    public boolean isAnnullata() {
        return this.model.getStatoCorrente() instanceof CancelledState;
    }

    /** True se l'utente corrente e' lo studente: e' lui/lei a dover lasciare la recensione al tutor. */
    public boolean isReviewer() {
        return this.userMatricola.equals(this.studenteMatricola);
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

        it.unibo.tutoring.model.credit.ReviewRepository.saveReview(
            this.reviewAuthor,
            this.materia,
            this.model.getDataOra().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            this.reviewStars,
            this.reviewComment,
            this.tutorMatricola
        );

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
        return isCompletataDaEntrambi() && isReviewer() && !this.reviewSaved;
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
                } else if (line.startsWith("MSG2;")) {
                    ripristinaMessaggioConTimestamp(line.substring("MSG2;".length()));
                } else if (line.startsWith("MSG;")) {
                    ripristinaMessaggioLegacy(line.substring("MSG;".length()));
                } else if (line.startsWith("REVIEW;")) {
                    // Legge i dati della recensione salvata se presenti.
                    ripristinaRecensione(line.substring("REVIEW;".length()));
                } else if (line.startsWith("COMPLETO_TUTOR;")) {
                    this.completatoTutor = Boolean.parseBoolean(line.substring("COMPLETO_TUTOR;".length()).trim());
                } else if (line.startsWith("COMPLETO_STUDENTE;")) {
                    this.completatoStudente = Boolean.parseBoolean(line.substring("COMPLETO_STUDENTE;".length()).trim());
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
            case "Cancelled" -> setStatoCorrente(new CancelledState());
            default -> throw new IllegalArgumentException("Stato sessione non riconosciuto: " + statoSalvato);
        }
    }

    private void ripristinaMessaggioConTimestamp(final String payload) {
        final String[] campi = payload.split(";", 3);
        if (campi.length != 3) {
            return;
        }

        try {
            final LocalDateTime timestamp = LocalDateTime.parse(campi[0]);
            final String mittenteID = campi[1];
            final String testo = campi[2];
            if (!mittenteID.isBlank() && !testo.isBlank()) {
                this.model.inviaMessaggio(testo, mittenteID, timestamp);
            }
        } catch (java.time.format.DateTimeParseException ignored) {
            // Ignora la singola riga non valida e continua a caricare la chat.
        }
    }

    private void ripristinaMessaggioLegacy(final String payload) {
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
                if (!this.conversationId.isBlank()) {
                    writer.write("CONVERSATION_ID;" + sanitizeLine(this.conversationId));
                    writer.newLine();
                }
                writer.write("MATERIA;" + sanitizeLine(this.model.getMateria()));
                writer.newLine();
                writer.write("DATA_ORA;" + this.model.getDataOra());
                writer.newLine();
                writer.write("DURATA;" + this.model.getDurata());
                writer.newLine();
                writer.write("TUTOR;" + sanitizeLine(this.tutorMatricola));
                writer.newLine();
                writer.write("STUDENTE;" + sanitizeLine(this.studenteMatricola));
                writer.newLine();
                writer.write("STATO;" + getNomeStatoCorrente());
                writer.newLine();

                writer.write("COMPLETO_TUTOR;" + this.completatoTutor);
                writer.newLine();
                writer.write("COMPLETO_STUDENTE;" + this.completatoStudente);
                writer.newLine();

                for (Message message : this.model.getStoricoChat()) {
                    writer.write(
                            "MSG2;"
                                    + message.getTimestamp()
                                    + ";"
                                    + sanitizeLine(message.getIdMittente())
                                    + ";"
                                    + sanitizeLine(message.getTesto())
                    );
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
        if (!this.conversationId.isBlank()) {
            return FILE_PREFIX
                    + sanitizeFilePart(this.conversationId)
                    + "_"
                    + sanitizeFilePart(this.tutorMatricola)
                    + "_"
                    + sanitizeFilePart(this.studenteMatricola)
                    + FILE_EXTENSION;
        }
        final String materiaSenzaSpazi = this.materia.replaceAll("\\s+", "");
        return FILE_PREFIX + materiaSenzaSpazi + "_" + this.tutorMatricola + "_" + this.studenteMatricola + FILE_EXTENSION;
    }

    private static String sanitizeFilePart(final String value) {
        return value.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    private static String sanitizeReviewComment(final String comment) {
        if (comment == null) {
            return "";
        }
        return comment.replace("\n", " ").replace("\r", " ").replace("|", "/");
    }

    private static String sanitizeLine(final String value) {
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ');
    }

    private static String requireText(final String value, final String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " non puo' essere vuoto.");
        }
        return value.trim();
    }
}