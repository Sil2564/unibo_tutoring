package it.unibo.tutoring.view.box;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UserAccount;
import it.unibo.tutoring.UserSession;
import it.unibo.tutoring.controller.session.TutoringSessionController;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.box.BoxType;
import it.unibo.tutoring.model.credit.ReviewRepository;
import it.unibo.tutoring.view.components.AppHeader;
import it.unibo.tutoring.view.components.AppCard;
import it.unibo.tutoring.view.components.AppButton;
import it.unibo.tutoring.view.components.FormControlStyle;
import it.unibo.tutoring.view.components.DashboardButton;
import it.unibo.tutoring.view.components.NavigationHelper;
import it.unibo.tutoring.view.session.SessionLinkUtil;
import it.unibo.tutoring.view.session.TutoringSessionViewApp;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class AnnouncementDetailViewApp {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color GREEN = Color.web("#28A745");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);
    private static final DateTimeFormatter COMPLETION_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm", Locale.ITALIAN);

    private AnnouncementDetailViewApp() {
    }

    public static Scene createScene(final Stage stage, final BoxTutoraggio box) {

        final boolean offer = box.getTipo() == BoxType.OFFER;
        final String me = CurrentSession.getUser() != null ? CurrentSession.getUser().getMatricola() : null;
        final boolean isAutore = me != null && me.equals(box.getAutoreMatricola());
        final boolean isConfermato = me != null && me.equals(box.getConfermato());
        final boolean isCandidato = me != null && box.isCandidato(me);
        final String autoreNome = SessionLinkUtil.nomeCompleto(box.getAutoreMatricola());

        final VBox root = new VBox();
        root.getStyleClass().add("app-shell");
        root.setBackground(
            new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY))
        );

        final AppHeader header = new AppHeader();

        final VBox pageContent = new VBox(24);
        pageContent.setPadding(new Insets(30));
        pageContent.setAlignment(Pos.TOP_CENTER);
        pageContent.setFillWidth(true);
        pageContent.setMaxWidth(Double.MAX_VALUE);

        final HBox topRow = new HBox();
        topRow.setMaxWidth(760);
        topRow.setPrefWidth(760);
        topRow.setAlignment(Pos.CENTER_LEFT);

        final Label title = new Label("Dettaglio Annuncio");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final Button backButton = new DashboardButton();

        topRow.getChildren().addAll(title, spacer, backButton);

        final AppCard card = new AppCard(10, new Insets(30), 16).withWidth(760);
        card.getStyleClass().add("auth-card");
        card.setAlignment(Pos.TOP_LEFT);

        final Label tag = new Label(offer ? "Offerta tutoraggio" : "Cerco tutor");
        tag.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 11));
        tag.setTextFill(Color.WHITE);
        tag.setPadding(new Insets(4, 10, 4, 10));
        tag.setBackground(new Background(new BackgroundFill(offer ? PRIMARY_RED : Color.web("#A1A1A1"), new CornerRadii(999), Insets.EMPTY)));

        final javafx.scene.text.Text titlePrefix = new javafx.scene.text.Text("Sessione con ");
        titlePrefix.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        titlePrefix.setFill(TEXT_DARK);

        final javafx.scene.text.Text titleName = new javafx.scene.text.Text(autoreNome);
        titleName.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        titleName.setFill(PRIMARY_RED);
        titleName.setUnderline(true);
        titleName.setCursor(Cursor.HAND);
        titleName.setOnMouseClicked(event -> {
            final Stage win = (Stage) card.getScene().getWindow();
            win.setScene(it.unibo.tutoring.UniBoTutoringProfileApp.createScene(box.getAutoreMatricola()));
            win.setTitle("UniBo Tutoring - Profilo");
            it.unibo.tutoring.view.components.WindowUtil.maximize(win);
        });

        final javafx.scene.text.Text titleSuffix = new javafx.scene.text.Text(offer ? " (Tutor)" : " (Studente)");
        titleSuffix.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        titleSuffix.setFill(TEXT_DARK);

        final javafx.scene.text.TextFlow sessionTitle =
            new javafx.scene.text.TextFlow(titlePrefix, titleName, titleSuffix);
        VBox.setMargin(sessionTitle, new Insets(10, 0, 6, 0));

        card.getChildren().addAll(
            tag,
            sessionTitle,
            new Separator(),

            detailRow("Corso", box.getCorso()),
            detailRow("Materia", box.getMateria()),
            detailRow("Argomento", box.getArgomento()),
            detailRow("Data", box.getData() != null ? box.getData().format(DATE_FORMAT) : "N/D"),
            detailRow("Orario", box.getOra() != null ? box.getOra().toString() : "N/D"),
            detailRow("Durata", box.getDurataOre() + " ore"),
            detailRow("Tipo annuncio", offer ? "Offerta tutoraggio (offre aiuto)" : "Richiesta tutoraggio (cerca aiuto)"),
            detailRow(offer ? "Tutor" : "Studente", autoreNome)
        );

        if (box.getNote() != null && !box.getNote().isBlank()) {
            card.getChildren().add(buildNoteBlock(box.getNote()));
        }

        if (isAutore) {
            card.getChildren().add(buildScheduleManagementSection(stage, box, me));
        }

        // Pulsante Contatta: sempre disponibile per chi non e' l'autore, apre la
        // chat con l'autore indipendentemente da candidatura/conferma.
        if (!isAutore && me != null) {
            final AppButton contactButton = AppButton.primary("Contatta");
            contactButton.setOnAction(event -> {
                box.aggiungiContatto(me);
                it.unibo.tutoring.model.box.BoxRepository.saveAll();
                final Stage win = (Stage) contactButton.getScene().getWindow();
                win.setScene(TutoringSessionViewApp.createScene(win, box, box.getAutoreMatricola()));
                win.setTitle("UniBo Tutoring - Dettaglio Sessione");
                it.unibo.tutoring.view.components.WindowUtil.maximize(win);
            });

            final HBox contactRow = new HBox(contactButton);
            contactRow.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(contactRow, new Insets(16, 0, 0, 0));
            card.getChildren().add(contactRow);
        }

        if (isAutore) {
            card.getChildren().add(buildConversationsSection(stage, box));
        }

        // Sezione dinamica: candidature / conferma / completamento / recensione.
        card.getChildren().add(buildWorkflowSection(stage, box, me, isAutore, isConfermato, isCandidato, autoreNome));

        pageContent.getChildren().addAll(topRow, card);

        final ScrollPane scrollPane = new ScrollPane(pageContent);
        it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(scrollPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setBackground(Background.EMPTY);

        root.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final Scene scene = new Scene(root, 1200, 900);
        scene.getStylesheets().add(AnnouncementDetailViewApp.class.getResource("/styles.css").toExternalForm());
        return scene;
    }

    /**
     * Permette all'autore di modificare esclusivamente data, ora e durata.
     * La disponibilita' mostrata dalla UI replica la regola applicata anche
     * dal modello, quindi una candidatura arrivata nel frattempo non puo'
     * essere aggirata premendo il pulsante di salvataggio.
     */
    private static VBox buildScheduleManagementSection(
            final Stage stage,
            final BoxTutoraggio box,
            final String me) {
        final VBox section = new VBox(10);
        VBox.setMargin(section, new Insets(20, 0, 0, 0));

        final Label title = new Label("Programmazione dell'annuncio");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        title.setTextFill(TEXT_DARK);
        section.getChildren().add(title);

        if (!box.puoModificareProgrammazione()) {
            final String reason = box.getConfermato() != null
                    ? "La programmazione non puo' piu' essere modificata perché la sessione e' stata confermata."
                    : "La programmazione non puo' essere modificata finche' sono presenti candidature attive.";
            section.getChildren().add(infoLabel(reason));
            return section;
        }

        section.getChildren().add(infoLabel(
                "Puoi modificare data, ora e durata finche' non ricevi la prima candidatura."));

        final AppButton editButton = AppButton.secondary("Modifica data e orario");

        final VBox editor = new VBox(8);
        editor.setVisible(false);
        editor.setManaged(false);
        editor.setPadding(new Insets(14));
        editor.setBackground(new Background(new BackgroundFill(
                Color.web("#F8F9FA"), new CornerRadii(8), Insets.EMPTY)));
        editor.setBorder(new Border(new BorderStroke(
                Color.web("#E3E3E3"), BorderStrokeStyle.SOLID,
                new CornerRadii(8), BorderWidths.DEFAULT)));

        final DatePicker datePicker = new DatePicker(box.getData());
        FormControlStyle.apply(datePicker);
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(final java.time.LocalDate date, final boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(java.time.LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #F0F0F0; -fx-opacity: 0.5;");
                }
            }
        });

        final TextField timeField = new TextField(
                box.getOra() != null ? box.getOra().toString() : "");
        timeField.setPromptText("HH:mm");
        FormControlStyle.apply(timeField);

        final int initialDuration = Math.max(1, Math.min(8, box.getDurataOre()));
        final Spinner<Integer> durationSpinner = new Spinner<>(1, 8, initialDuration);
        FormControlStyle.apply(durationSpinner);

        final Label feedback = new Label();
        feedback.setTextFill(PRIMARY_RED);
        feedback.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        feedback.setWrapText(true);
        feedback.setVisible(false);
        feedback.setManaged(false);

        final AppButton saveButton = AppButton.primary("Salva programmazione", GREEN);
        saveButton.setOnAction(event -> {
            feedback.setVisible(false);
            feedback.setManaged(false);
            try {
                if (datePicker.getValue() == null || timeField.getText().isBlank()) {
                    throw new IllegalArgumentException("Data e ora sono obbligatorie.");
                }
                box.aggiornaProgrammazione(
                        me,
                        datePicker.getValue(),
                        LocalTime.parse(timeField.getText().trim()),
                        durationSpinner.getValue());
                refresh(stage, box);
            } catch (final DateTimeParseException exception) {
                showScheduleFeedback(feedback, "Formato orario non valido. Usa HH:mm (es. 15:00).");
            } catch (final IllegalArgumentException | IllegalStateException | SecurityException exception) {
                showScheduleFeedback(feedback, exception.getMessage());
            }
        });

        final AppButton cancelButton = AppButton.secondary("Annulla");
        cancelButton.setOnAction(event -> {
            editor.setVisible(false);
            editor.setManaged(false);
            editButton.setDisable(false);
        });

        final HBox actionRow = new HBox(10, saveButton, cancelButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        editor.getChildren().addAll(
                fieldLabel("Data"),
                datePicker,
                fieldLabel("Orario"),
                timeField,
                fieldLabel("Durata (ore)"),
                durationSpinner,
                feedback,
                actionRow);

        editButton.setOnAction(event -> {
            editor.setVisible(true);
            editor.setManaged(true);
            editButton.setDisable(true);
        });

        section.getChildren().addAll(buttonRow(editButton), editor);
        return section;
    }

    private static void showScheduleFeedback(final Label feedback, final String message) {
        feedback.setText(message);
        feedback.setVisible(true);
        feedback.setManaged(true);
    }

    private static Label fieldLabel(final String text) {
        final Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        label.setTextFill(TEXT_MEDIUM);
        return label;
    }

    private static VBox buildConversationsSection(final Stage stage, final BoxTutoraggio box) {
        final VBox section = new VBox(10);
        VBox.setMargin(section, new Insets(20, 0, 0, 0));

        final Label title = new Label("Messaggi ricevuti");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        title.setTextFill(TEXT_DARK);
        section.getChildren().add(title);

        if (box.getContatti().isEmpty()) {
            section.getChildren().add(infoLabel("Nessun utente ti ha ancora contattato."));
            return section;
        }

        for (final String contactMatricola : box.getContatti()) {
            section.getChildren().add(buildConversationRow(stage, box, contactMatricola));
        }
        return section;
    }

    private static HBox buildConversationRow(
            final Stage stage,
            final BoxTutoraggio box,
            final String contactMatricola) {
        final String contactName = SessionLinkUtil.nomeCompleto(contactMatricola);
        final TutoringSessionController controller = SessionLinkUtil.buildController(
                box,
                contactMatricola,
                box.getAutoreMatricola());
        final var messages = controller.getModel().getStoricoChat();
        final String preview;
        if (messages.isEmpty()) {
            preview = "Conversazione aperta, nessun messaggio inviato.";
        } else {
            final var lastMessage = messages.get(messages.size() - 1);
            final String sender = lastMessage.getIdMittente().equals(box.getAutoreMatricola())
                    ? "Tu"
                    : contactName;
            preview = sender + ": " + lastMessage.getTesto();
        }

        final Label nameLabel = new Label(contactName);
        nameLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 13));
        nameLabel.setTextFill(TEXT_DARK);

        final Label previewLabel = new Label(preview);
        previewLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        previewLabel.setTextFill(TEXT_MEDIUM);
        previewLabel.setWrapText(true);

        final VBox text = new VBox(3, nameLabel, previewLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        final AppButton openChat = AppButton.primary("Apri chat");
        openChat.setPadding(new Insets(6, 14, 6, 14));
        openChat.setOnAction(event -> {
            final Stage win = stage != null
                    ? stage
                    : (Stage) openChat.getScene().getWindow();
            win.setScene(TutoringSessionViewApp.createScene(
                    win,
                    box,
                    contactMatricola));
            win.setTitle("UniBo Tutoring - Chat con " + contactName);
            it.unibo.tutoring.view.components.WindowUtil.maximize(win);
        });

        final HBox row = new HBox(12, text, openChat);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setBackground(new Background(new BackgroundFill(
                Color.web("#F8F9FA"), new CornerRadii(8), Insets.EMPTY)));
        row.setBorder(new Border(new BorderStroke(
                Color.web("#E3E3E3"), BorderStrokeStyle.SOLID,
                new CornerRadii(8), BorderWidths.DEFAULT)));
        return row;
    }

    /**
     * Costruisce la sezione con lo stato corrente della candidatura/sessione
     * e i relativi pulsanti d'azione, in base al ruolo di chi guarda.
     */
    private static VBox buildWorkflowSection(
        final Stage stage,
        final BoxTutoraggio box,
        final String me,
        final boolean isAutore,
        final boolean isConfermato,
        final boolean isCandidato,
        final String autoreNome
    ) {
        final VBox section = new VBox(12);
        VBox.setMargin(section, new Insets(20, 0, 0, 0));

        final Label sectionTitle = new Label("Stato sessione");
        sectionTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        sectionTitle.setTextFill(TEXT_DARK);
        section.getChildren().add(sectionTitle);

        if (me == null) {
            section.getChildren().add(infoLabel("Devi essere autenticato per interagire con questo annuncio."));
            return section;
        }

        if (box.getConfermato() != null) {
            // C'e' gia' un candidato confermato.
            if (isAutore || isConfermato) {
                section.getChildren().add(buildConfermatoSection(stage, box, me, isAutore));
            } else {
                section.getChildren().add(infoLabel("Questo annuncio e' gia' stato assegnato a un altro utente e non e' piu' disponibile."));
            }
            return section;
        }

        // Nessun confermato ancora.
        if (isAutore) {
            section.getChildren().add(buildCandidatiListSection(stage, box));
        } else if (isCandidato) {
            final Label info = infoLabel("Ti sei candidato per questo annuncio. In attesa che " + autoreNome + " confermi un candidato.");

            final AppButton ritira = AppButton.secondary("Ritira candidatura");
            ritira.setOnAction(event -> {
                box.rimuoviCandidato(me);
                SessionLinkUtil.buildController(box, box.getAutoreMatricola(), me).annullaSessione();
                refresh(stage, box);
            });

            section.getChildren().addAll(info, buttonRow(ritira));
        } else {
            final Label info = infoLabel("Vuoi candidarti per questa sessione di tutoraggio?");

            final AppButton candidati = AppButton.primary("Candidati", GREEN);
            candidati.setOnAction(event -> {
                box.aggiungiCandidato(me);
                SessionLinkUtil.buildController(box, box.getAutoreMatricola(), me).proponi();
                refresh(stage, box);
            });

            section.getChildren().addAll(info, buttonRow(candidati));
        }

        return section;
    }

    /** Lista dei candidati visibile solo all'autore, con Conferma/Rifiuta per ciascuno. */
    private static VBox buildCandidatiListSection(final Stage stage, final BoxTutoraggio box) {
        final VBox list = new VBox(10);

        final List<String> candidati = box.getCandidati();
        if (candidati.isEmpty()) {
            list.getChildren().add(infoLabel("Nessun candidato ancora. Gli utenti che si candideranno all'annuncio compariranno qui."));
            return list;
        }

        list.getChildren().add(infoLabel(candidati.size() + " candidat" + (candidati.size() == 1 ? "o" : "i") + " in attesa di conferma:"));

        for (final String candidatoMatricola : candidati) {
            list.getChildren().add(buildCandidatoRow(stage, box, candidatoMatricola));
        }

        return list;
    }

    private static HBox buildCandidatoRow(final Stage stage, final BoxTutoraggio box, final String candidatoMatricola) {
        final UserAccount user = it.unibo.tutoring.AuthService.getInstance().getUser(candidatoMatricola);
        final String nomeCompleto = user != null ? user.getName() + " " + user.getSurname() : candidatoMatricola;

        final HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setBackground(new Background(new BackgroundFill(Color.web("#F8F9FA"), new CornerRadii(8), Insets.EMPTY)));
        row.setBorder(new Border(new BorderStroke(Color.web("#E3E3E3"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final Label nameLabel = new Label(nomeCompleto);
        nameLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 13));
        nameLabel.setTextFill(TEXT_DARK);

        final Label matricolaLabel = new Label("Matricola: " + candidatoMatricola);
        matricolaLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        matricolaLabel.setTextFill(TEXT_MEDIUM);

        final Label ratingLabel = new Label(mediaStelleTesto(candidatoMatricola));
        ratingLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        ratingLabel.setTextFill(Color.web("#B8860B"));

        final VBox infoBox = new VBox(2, nameLabel, matricolaLabel, ratingLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        final AppButton conferma = AppButton.primary("Conferma", GREEN);
        conferma.setPadding(new Insets(6, 14, 6, 14));
        conferma.setOnAction(event -> {
            // Conferma questo candidato: le altre candidature pendenti vengono annullate
            // e rimosse dalla lista, cosi' spariscono anche dalla loro sezione "Le mie sessioni".
            for (final String altro : List.copyOf(box.getCandidati())) {
                if (!altro.equals(candidatoMatricola)) {
                    SessionLinkUtil.buildController(box, altro, box.getAutoreMatricola()).annullaSessione();
                    box.rimuoviCandidato(altro);
                }
            }
            box.confermaCandidato(candidatoMatricola);
            SessionLinkUtil.buildController(box, candidatoMatricola, box.getAutoreMatricola()).confermaSessione();
            refresh(stage, box);
        });

        final AppButton rifiuta = AppButton.secondary("Rifiuta");
        rifiuta.setPadding(new Insets(6, 14, 6, 14));
        rifiuta.setOnAction(event -> {
            box.rimuoviCandidato(candidatoMatricola);
            SessionLinkUtil.buildController(box, candidatoMatricola, box.getAutoreMatricola()).annullaSessione();
            refresh(stage, box);
        });

        row.getChildren().addAll(infoBox, conferma, rifiuta);
        return row;
    }

    /** Sezione mostrata quando c'e' gia' un candidato confermato: bottone Completata + eventuale recensione. */
    private static VBox buildConfermatoSection(
        final Stage stage,
        final BoxTutoraggio box,
        final String me,
        final boolean isAutore
    ) {
        final VBox section = new VBox(12);

        final String counterparty = SessionLinkUtil.controparteDi(box, me);
        final String nomeControparte = SessionLinkUtil.nomeCompleto(counterparty);
        final TutoringSessionController controller = SessionLinkUtil.buildController(box, counterparty, me);

        if (!controller.isCompletataDaEntrambi()) {
            final Label info = infoLabel("Sessione confermata con " + nomeControparte + ".");

            final AppButton completata = new AppButton();
            aggiornaPulsanteCompletamento(completata, controller, nomeControparte);
            completata.setOnAction(event -> {
                controller.segnalaCompletamento();
                refresh(stage, box);
            });

            if (!controller.haGiaSegnalatoCompletamento()
                    && !controller.puoSegnalareCompletamento()) {
                final long millisecondiAllaFine = java.time.Duration.between(
                        LocalDateTime.now(),
                        controller.getFinePrevista()).toMillis();
                if (millisecondiAllaFine > 0) {
                    final PauseTransition abilitaAllaFine = new PauseTransition(
                            javafx.util.Duration.millis(millisecondiAllaFine));
                    abilitaAllaFine.setOnFinished(event ->
                            aggiornaPulsanteCompletamento(
                                    completata,
                                    controller,
                                    nomeControparte));
                    abilitaAllaFine.play();
                } else {
                    aggiornaPulsanteCompletamento(
                            completata,
                            controller,
                            nomeControparte);
                }
            }

            section.getChildren().addAll(info, buttonRow(completata));
            return section;
        }

        // Sessione completata da entrambi.
        if (controller.isReviewer() && !controller.isReviewSaved()) {
            section.getChildren().add(buildReviewSection(stage, box, controller, nomeControparte));
        } else {
            section.getChildren().add(infoLabel("Sessione completata con " + nomeControparte + ". Grazie per aver usato UniBo Tutoring!"));
        }

        return section;
    }

    private static void aggiornaPulsanteCompletamento(
            final AppButton completata,
            final TutoringSessionController controller,
            final String nomeControparte) {
        if (controller.haGiaSegnalatoCompletamento()) {
            completata.setText("In attesa che " + nomeControparte + " completi anche lui/lei...");
            completata.setDisable(true);
            completata.asPrimary(TEXT_MEDIUM);
            return;
        }

        if (!controller.puoSegnalareCompletamento()) {
            completata.setText(
                    "Disponibile dal "
                            + controller.getFinePrevista().format(COMPLETION_DATE_FORMAT));
            completata.setDisable(true);
            completata.asPrimary(TEXT_MEDIUM);
            return;
        }

        completata.setText("Segna come completata");
        completata.setDisable(false);
        completata.asPrimary(PRIMARY_RED);
    }

    /** Form di recensione mostrato solo a chi ha ricevuto la lezione (lo studente), a completamento avvenuto. */
    private static VBox buildReviewSection(
        final Stage stage,
        final BoxTutoraggio box,
        final TutoringSessionController controller,
        final String nomeTutor
    ) {
        final VBox section = new VBox(10);
        section.setPadding(new Insets(16));
        section.setBackground(new Background(new BackgroundFill(Color.web("#FFF8E6"), new CornerRadii(10), Insets.EMPTY)));
        section.setBorder(new Border(new BorderStroke(Color.web("#F0C419"), BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));

        final Label title = new Label("Sessione completata! Lascia una recensione a " + nomeTutor);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
        title.setTextFill(TEXT_DARK);
        title.setWrapText(true);

        final HBox starBar = new HBox(6);
        starBar.setAlignment(Pos.CENTER_LEFT);
        final java.util.List<Label> stars = new java.util.ArrayList<>();
        final int[] selectedStars = {0};
        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            final Label starLabel = new Label("☆");
            starLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
            starLabel.setTextFill(Color.web("#F0C419"));
            starLabel.setStyle("-fx-cursor: hand;");
            starLabel.setOnMouseClicked(event -> {
                selectedStars[0] = starValue;
                for (int j = 0; j < stars.size(); j++) {
                    stars.get(j).setText(j < starValue ? "★" : "☆");
                }
            });
            stars.add(starLabel);
            starBar.getChildren().add(starLabel);
        }

        final TextArea commentArea = new TextArea();
        commentArea.setPromptText("Condividi la tua esperienza (opzionale)");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(3);
        commentArea.setFont(Font.font("System", 13));

        final Label errorLabel = new Label();
        errorLabel.setTextFill(PRIMARY_RED);
        errorLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        final AppButton invia = AppButton.primary("Invia recensione");
        invia.setOnAction(event -> {
            if (selectedStars[0] == 0) {
                errorLabel.setText("Seleziona almeno una stella prima di inviare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            controller.registraRecensione(selectedStars[0], commentArea.getText(), UserSession.getDisplayName());
            goToDashboard(stage, invia);
        });

        section.getChildren().addAll(title, starBar, commentArea, errorLabel, buttonRow(invia));
        return section;
    }

    private static void refresh(final Stage stage, final BoxTutoraggio box) {
        // Ogni azione che modifica lo stato dell'annuncio (candidati, ritira,
        // conferma, rifiuta) passa da qui: e' il punto unico in cui persistiamo
        // su data/boxes.csv cosi' che la modifica sopravviva al riavvio.
        it.unibo.tutoring.model.box.BoxRepository.saveAll();

        final Stage win = stage != null ? stage : null;
        if (win != null) {
            win.setScene(AnnouncementDetailViewApp.createScene(win, box));
            win.setTitle("UniBo Tutoring - Dettaglio Annuncio");
            it.unibo.tutoring.view.components.WindowUtil.maximize(win);
        }
    }

    private static void goToDashboard(final Stage stage, final Button anyButtonInScene) {
        final Stage win = stage != null ? stage : (Stage) anyButtonInScene.getScene().getWindow();
        NavigationHelper.goToDashboard(win);
    }

    private static String mediaStelleTesto(final String matricola) {
        final List<ReviewRepository.Review> reviews = ReviewRepository.loadReviewsForRecipient(matricola);
        if (reviews.isEmpty()) {
            return "Nessuna recensione ancora";
        }
        final double media = reviews.stream().mapToInt(ReviewRepository.Review::stars).average().orElse(0.0);
        return String.format(Locale.ITALIAN, "%.1f ★ (%d recension%s)", media, reviews.size(), reviews.size() == 1 ? "e" : "i");
    }

    private static Label infoLabel(final String text) {
        final Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.NORMAL, 13));
        label.setTextFill(TEXT_MEDIUM);
        label.setWrapText(true);
        return label;
    }

    private static HBox buttonRow(final Button button) {
        final HBox row = new HBox(button);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Nota facoltativa scritta dall'autore alla pubblicazione. Usa un nodo
     * Text con larghezza di wrapping esplicita: va sempre a capo e non viene
     * mai troncata con i puntini, per quanto sia lunga.
     */
    private static VBox buildNoteBlock(final String note) {
        final Label label = new Label("Nota dell'autore");
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web("#4A4A4A"));

        final javafx.scene.text.Text text = new javafx.scene.text.Text(note);
        text.setFont(Font.font("System", FontWeight.NORMAL, 15));
        text.setFill(TEXT_DARK);
        text.setWrappingWidth(680);

        final VBox block = new VBox(6, label, text);
        block.setPadding(new Insets(12, 14, 12, 14));
        block.setBackground(new Background(new BackgroundFill(Color.web("#FFF8E1"), new CornerRadii(8), Insets.EMPTY)));
        block.setBorder(new Border(new BorderStroke(Color.web("#F0DFA0"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        VBox.setMargin(block, new Insets(6, 0, 0, 0));
        return block;
    }

    private static VBox detailRow(final String label, final String value) {
        final Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        labelNode.setTextFill(Color.web("#4A4A4A"));

        final Label valueNode = new Label(value == null || value.isBlank() ? "N/D" : value);
        valueNode.setFont(Font.font("System", FontWeight.NORMAL, 16));
        valueNode.setTextFill(TEXT_DARK);
        valueNode.setWrapText(true);

        final VBox row = new VBox(2, labelNode, valueNode);
        VBox.setMargin(row, new Insets(6, 0, 0, 0));
        return row;
    }
}