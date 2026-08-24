package it.unibo.tutoring.controller.session;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.model.chat.ChatObserver;
import it.unibo.tutoring.model.credit.CompletedSessionRepository;
import it.unibo.tutoring.model.session.CancelledState;
import it.unibo.tutoring.model.session.CompletedState;
import it.unibo.tutoring.model.session.ConfirmedState;
import it.unibo.tutoring.model.session.ProposedState;
import it.unibo.tutoring.model.session.SessionRepository;
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
import java.util.LinkedHashSet;
import java.util.Set;

public class TutoringSessionController {

    public static final String SYSTEM_SENDER_ID = "SYSTEM";

    private static final Path SESSION_FOLDER = Path.of("data", "sessions");
    private static final String FILE_PREFIX = "SESS_";
    private static final String FILE_EXTENSION = ".csv";
    private static final int DEFAULT_COMPLETED_HOURS = 1;
    private static final long ORE_VISIBILITA_SESSIONE_CANCELLATA = 24;
    private static final DateTimeFormatter CONFLICT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

    // Metadati della cancellazione di una sessione gia' confermata.
    private String cancellataDa = "";
    private LocalDateTime cancellataAt;
    private String motivoCancellazione = "";
    private final Set<String> cancellazioneVistaDa = new LinkedHashSet<>();

    // Matricole che hanno letto tutti i messaggi correnti della chat. La
    // presenza della riga dedicata nel file distingue il nuovo formato dai
    // file vecchi, che non devono generare notifiche arretrate.
    private final Set<String> chatVistaDa = new LinkedHashSet<>();
    private boolean statoLetturaChatPresente = false;

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
        verificaAssenzaSovrapposizioni();
        this.model.conferma();
        salvaSuFile();
    }

    /**
     * Impedisce di confermare la sessione quando il tutor o lo studente hanno
     * gia' un'altra sessione confermata nello stesso intervallo temporale.
     */
    private void verificaAssenzaSovrapposizioni() {
        final SessionRepository repository = new SessionRepository();
        verificaDisponibilitaPartecipante(repository, "Il tutor", this.tutorMatricola);
        verificaDisponibilitaPartecipante(repository, "Lo studente", this.studenteMatricola);
    }

    private void verificaDisponibilitaPartecipante(
            final SessionRepository repository,
            final String ruolo,
            final String matricola) {
        repository.findOverlappingConfirmedSession(
                        matricola,
                        this.model.getDataOra(),
                        this.model.getDurata(),
                        this.fileCondivisoPath)
                .ifPresent(conflict -> {
                    throw new IllegalStateException(
                            ruolo
                                    + " ha gia' una sessione confermata per '"
                                    + conflict.materia()
                                    + "' dal "
                                    + conflict.inizio().format(CONFLICT_DATE_FORMAT)
                                    + " al "
                                    + conflict.fine().format(CONFLICT_DATE_FORMAT)
                                    + ".");
                });
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
     * Cancella una sessione gia' confermata e non ancora terminata. Il motivo
     * e' facoltativo; autore, istante e motivo vengono conservati nel file
     * condiviso e la chat riceve un messaggio di sistema permanente.
     */
    public void cancellaSessione(final String motivo) {
        if (!(this.model.getStatoCorrente() instanceof ConfirmedState)) {
            throw new IllegalStateException("Solo una sessione confermata puo' essere annullata.");
        }

        final LocalDateTime adesso = LocalDateTime.now();
        if (!puoCancellareSessione(adesso)) {
            throw new IllegalStateException(
                    "La sessione non puo' essere annullata dopo la fine prevista: "
                            + getFinePrevista());
        }

        this.cancellataDa = this.userMatricola;
        this.cancellataAt = adesso;
        this.motivoCancellazione = motivo == null ? "" : sanitizeLine(motivo).trim();
        this.cancellazioneVistaDa.clear();
        // Chi compie l'azione l'ha gia' vista; la notifica resta non letta per
        // l'altra persona fino all'apertura dell'annuncio o della chat.
        this.cancellazioneVistaDa.add(this.userMatricola);

        this.model.annulla();
        this.model.inviaMessaggio(
                creaMessaggioSistemaCancellazione(),
                SYSTEM_SENDER_ID,
                this.cancellataAt);
        salvaSuFile();
    }

    public boolean puoCancellareSessione() {
        return puoCancellareSessione(LocalDateTime.now());
    }

    boolean puoCancellareSessione(final LocalDateTime adesso) {
        if (adesso == null) {
            throw new IllegalArgumentException("L'istante corrente e' obbligatorio.");
        }
        return this.model.getStatoCorrente() instanceof ConfirmedState
                && adesso.isBefore(getFinePrevista());
    }

    public String getCancellataDa() {
        return this.cancellataDa;
    }

    public LocalDateTime getCancellataAt() {
        return this.cancellataAt;
    }

    /**
     * Una sessione cancellata resta consultabile per le 24 ore successive,
     * come un annuncio eliminato che ha gia' dei candidati.
     *
     * @return {@code true} finche' la finestra di visibilita' non e' scaduta
     */
    public boolean isCancellazioneVisibile() {
        return isCancellazioneVisibile(LocalDateTime.now());
    }

    boolean isCancellazioneVisibile(final LocalDateTime adesso) {
        if (adesso == null) {
            throw new IllegalArgumentException("L'istante corrente e' obbligatorio.");
        }
        return isAnnullata()
                && this.cancellataAt != null
                && !this.cancellataAt
                        .plusHours(ORE_VISIBILITA_SESSIONE_CANCELLATA)
                        .isBefore(adesso);
    }

    public String getMotivoCancellazione() {
        return this.motivoCancellazione;
    }

    public boolean isCancellazioneVistaDa(final String matricola) {
        return matricola != null && this.cancellazioneVistaDa.contains(matricola);
    }

    public boolean haVistoCancellazione() {
        return isCancellazioneVistaDa(this.userMatricola);
    }

    /** Segna come letta la notifica di cancellazione per l'utente corrente. */
    public void segnaCancellazioneVista() {
        if (isAnnullata() && this.cancellataAt != null
                && this.cancellazioneVistaDa.add(this.userMatricola)) {
            salvaSuFile();
        }
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
        if (!fineSessioneRaggiunta()) {
            throw new IllegalStateException(
                    "La sessione puo' essere completata solo dopo la fine prevista: "
                            + getFinePrevista());
        }

        if (isStudenteCorrente() && !this.completatoTutor) {
            throw new IllegalStateException(
                    "Il tutor deve confermare per primo il completamento della sessione.");
        }

        if (haGiaSegnalatoCompletamento()) {
            throw new IllegalStateException(
                    "Hai gia' confermato il completamento della sessione.");
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

        // La transizione a Completed e la pubblicazione dell'evento hanno un
        // solo punto di responsabilita': ConfirmedState.completa().
        this.model.completa();
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
        return this.model.getStatoCorrente() instanceof ConfirmedState
                && !haGiaSegnalatoCompletamento()
                && fineSessioneRaggiunta(adesso)
                && (!isStudenteCorrente() || this.completatoTutor);
    }

    public boolean fineSessioneRaggiunta() {
        return fineSessioneRaggiunta(LocalDateTime.now());
    }

    boolean fineSessioneRaggiunta(final LocalDateTime adesso) {
        if (adesso == null) {
            throw new IllegalArgumentException("L'istante corrente e' obbligatorio.");
        }
        return !adesso.isBefore(getFinePrevista());
    }

    public boolean isTutorCorrente() {
        return this.userMatricola.equals(this.tutorMatricola);
    }

    public boolean isStudenteCorrente() {
        return this.userMatricola.equals(this.studenteMatricola);
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
            // Ogni nuovo messaggio rende la conversazione non letta per la
            // controparte e gia' letta per chi lo ha appena inviato.
            this.chatVistaDa.clear();
            this.chatVistaDa.add(this.userMatricola);
            this.statoLetturaChatPresente = true;
            salvaSuFile();
        }
    }

    /** True quando l'utente corrente ha almeno un messaggio ricevuto non letto. */
    public boolean haMessaggiChatNonLetti() {
        if (!this.statoLetturaChatPresente
                || this.chatVistaDa.contains(this.userMatricola)) {
            return false;
        }
        return this.model.getStoricoChat().stream()
                .anyMatch(message -> !SYSTEM_SENDER_ID.equals(message.getIdMittente())
                        && !this.userMatricola.equals(message.getIdMittente()));
    }

    /** Segna come letti i messaggi correnti per l'utente che ha aperto la chat. */
    public void segnaChatComeLetta() {
        if (this.statoLetturaChatPresente
                && this.chatVistaDa.add(this.userMatricola)) {
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
                } else if (line.startsWith("CANCELLATA_DA;")) {
                    this.cancellataDa = line.substring("CANCELLATA_DA;".length()).trim();
                } else if (line.startsWith("CANCELLATA_AT;")) {
                    ripristinaIstanteCancellazione(line.substring("CANCELLATA_AT;".length()));
                } else if (line.startsWith("MOTIVO_CANCELLAZIONE;")) {
                    this.motivoCancellazione = line.substring("MOTIVO_CANCELLAZIONE;".length()).trim();
                } else if (line.startsWith("CANCELLAZIONE_VISTA_DA;")) {
                    ripristinaVisualizzazioniCancellazione(
                            line.substring("CANCELLAZIONE_VISTA_DA;".length()));
                } else if (line.startsWith("CHAT_VISTA_DA;")) {
                    ripristinaVisualizzazioniChat(
                            line.substring("CHAT_VISTA_DA;".length()));
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

    private void ripristinaIstanteCancellazione(final String valore) {
        if (valore == null || valore.isBlank()) {
            return;
        }
        try {
            this.cancellataAt = LocalDateTime.parse(valore.trim());
        } catch (java.time.format.DateTimeParseException ignored) {
            // Un metadato non valido non deve impedire il caricamento della sessione.
        }
    }

    private void ripristinaVisualizzazioniCancellazione(final String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }
        for (final String matricola : payload.split(",")) {
            if (!matricola.isBlank()) {
                this.cancellazioneVistaDa.add(matricola.trim());
            }
        }
    }

    private void ripristinaVisualizzazioniChat(final String payload) {
        this.statoLetturaChatPresente = true;
        if (payload == null || payload.isBlank()) {
            return;
        }
        for (final String matricola : payload.split(",")) {
            if (!matricola.isBlank()) {
                this.chatVistaDa.add(matricola.trim());
            }
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

                if (this.statoLetturaChatPresente) {
                    writer.write("CHAT_VISTA_DA;" + String.join(",", this.chatVistaDa));
                    writer.newLine();
                }

                if (this.cancellataAt != null) {
                    writer.write("CANCELLATA_DA;" + sanitizeLine(this.cancellataDa));
                    writer.newLine();
                    writer.write("CANCELLATA_AT;" + this.cancellataAt);
                    writer.newLine();
                    writer.write("MOTIVO_CANCELLAZIONE;" + sanitizeLine(this.motivoCancellazione));
                    writer.newLine();
                    writer.write(
                            "CANCELLAZIONE_VISTA_DA;"
                                    + String.join(",", this.cancellazioneVistaDa));
                    writer.newLine();
                }

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

    private String creaMessaggioSistemaCancellazione() {
        final String nome = nomeUtente(this.cancellataDa);
        final StringBuilder messaggio = new StringBuilder(
                "La sessione e' stata annullata da " + nome + ".");
        if (!this.motivoCancellazione.isBlank()) {
            messaggio.append(" Motivo: ").append(this.motivoCancellazione);
        }
        return messaggio.toString();
    }

    private static String nomeUtente(final String matricola) {
        final var user = AuthService.getInstance().getUser(matricola);
        return user != null
                ? user.getName() + " " + user.getSurname()
                : matricola;
    }

    private static String requireText(final String value, final String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " non puo' essere vuoto.");
        }
        return value.trim();
    }

    public void addChatObserver(final ChatObserver observer) {
        this.model.addChatObserver(observer);
    }
}