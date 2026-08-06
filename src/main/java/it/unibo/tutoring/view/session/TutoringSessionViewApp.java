package it.unibo.tutoring.view.session;

import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserSession;
import it.unibo.tutoring.controller.session.TutoringSessionController;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.view.components.AppHeader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TutoringSessionViewApp extends Application {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");
    private static final Color CARD_BG = Color.WHITE;
    private final TutoringSessionController controller;
    private final BoxTutoraggio box;
    private final String counterpartyMatricola;
    private StackPane reviewOverlay;
    private Label notificationLabel;

    public TutoringSessionViewApp() {
        this.box = null;
        this.counterpartyMatricola = null;
        this.controller = new TutoringSessionController(
                "Progettazione e Sviluppo del Software",
                "Mario Rossi",
                true,
                "0000001",
                "0000002");
    }

    private TutoringSessionViewApp(
            final String materiaAnnuncio,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String matricolaInserzionista) {
        this.box = null;
        this.counterpartyMatricola = matricolaInserzionista;
        this.controller = new TutoringSessionController(
                materiaAnnuncio,
                nomeInserzionista,
                tutorOffer,
                matricolaInserzionista,
                UserSession.getMatricola());
    }

    private TutoringSessionViewApp(
            final TutoringSessionController controller,
            final BoxTutoraggio box,
            final String counterpartyMatricola) {
        this.controller = controller;
        this.box = box;
        this.counterpartyMatricola = counterpartyMatricola;
    }

    public static Scene createSceneForBox(
            final Stage stage,
            final BoxTutoraggio box,
            final String counterpartyMatricola) {
        final TutoringSessionController controller = SessionLinkUtil.buildController(
                box,
                counterpartyMatricola,
                UserSession.getMatricola()
        );
        return new TutoringSessionViewApp(controller, box, counterpartyMatricola).createScene(stage);
    }

    public static Scene createScene(
            final Stage stage,
            final String materiaAnnuncio,
            final String nomeInserzionista,
            final boolean tutorOffer,
            final String tutorMatricola) {
        final TutoringSessionViewApp app = new TutoringSessionViewApp(materiaAnnuncio, nomeInserzionista, tutorOffer, tutorMatricola);
        return app.createScene(stage);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("UniBo Tutoring - Dettaglio Sessione");
        stage.setScene(createScene(stage));
        stage.show();
        it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
    }

    public Scene createScene(final Stage stage) {
        final VBox root = new VBox();
        root.getStyleClass().add("app-shell");
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final Stage window = stage;
        final AppHeader header = new AppHeader(
                UserSession.getDisplayName(),
                window != null ? UserSession.createLogoutAction(window) : null);

        final Button btnBack = new Button("< Torna indietro");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #6A6A6A; -fx-font-weight: bold;");
        VBox.setMargin(btnBack, new Insets(10, 0, 0, 15));
        if (stage != null) {
            btnBack.setOnAction(e -> {
                stage.setScene(UniBoTutoringDashboardApp.createScene());
                stage.setTitle("UniBo Tutoring - Dashboard");
            });
        }

        root.getChildren().addAll(header, btnBack, createMainArea());
        this.reviewOverlay = createReviewOverlay();
        this.notificationLabel = createNotificationLabel();

        final ScrollPane pageScroll = new ScrollPane(root);
        it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(pageScroll);

        final StackPane stack = new StackPane(pageScroll, this.reviewOverlay, this.notificationLabel);
        StackPane.setAlignment(this.notificationLabel, Pos.TOP_CENTER);
        StackPane.setMargin(this.notificationLabel, new Insets(20, 0, 0, 0));
        final Scene scene = it.unibo.tutoring.view.components.WindowUtil.createScrollableScene(stack);
        return scene;
    }

    private HBox createMainArea() {
        final HBox main = new HBox(20);
        main.setAlignment(Pos.TOP_LEFT);
        main.setPadding(new Insets(24));

        // Colonna Sinistra
        final VBox sessionDetails = createSessionDetailsCard();

        // Colonna Destra
        final VBox chatBox = createChatCard();
        HBox.setHgrow(chatBox, Priority.ALWAYS);

        main.getChildren().addAll(sessionDetails, chatBox);
        return main;
    }

    private VBox createSessionDetailsCard() {
        final VBox card = new VBox(15);
        card.getStyleClass().add("soft-card");
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));
        card.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final Label title = new Label("Dettagli Sessione");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 22));
        title.setTextFill(TEXT_DARK);

        // Etichetta dello stato, in sola lettura: le azioni (candidati, conferma,
        // completata) si trovano ora nella pagina "Dettaglio Annuncio".
        final String statoText = statoSessioneVisualizzato();

        final Label lblStatoValue = new Label(statoText);
        lblStatoValue.setFont(Font.font("System", FontWeight.NORMAL, 15));
        lblStatoValue.setTextFill(TEXT_DARK);
        VBox rowStato = new VBox(2,
                createLabel("Stato:", true),
                lblStatoValue
        );

        final Label hint = new Label("Le azioni per candidarsi, confermare o completare la sessione si trovano nella pagina dell'annuncio.");
        hint.setFont(Font.font("System", FontWeight.NORMAL, 11));
        hint.setTextFill(TEXT_MEDIUM);
        hint.setWrapText(true);

        card.getChildren().addAll(
                title, new Separator(),
                infoRow("Materia:", controller.getModel().getMateria()),
                infoRow(controller.getRuoloInserzionista(), controller.getNomeInserzionista()),
                rowStato,
                hint
        );

        final Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        card.getChildren().add(spacer);

        // Il pulsante per lasciare la recensione compare solo per chi ha
        // ricevuto la lezione (lo studente), a sessione completata da entrambi,
        // e solo se non e' gia' stata lasciata una recensione.
        if (controller.shouldAskForReview()) {
            final Button btnRecensione = new Button("Lascia una recensione");
            btnRecensione.getStyleClass().add("primary-btn");
            btnRecensione.setFont(Font.font("System", FontWeight.BOLD, 14));
            btnRecensione.setTextFill(Color.WHITE);
            btnRecensione.setMaxWidth(Double.MAX_VALUE);
            btnRecensione.setBackground(new Background(new BackgroundFill(Color.web("#F0C419"), new CornerRadii(6), Insets.EMPTY)));
            btnRecensione.setOnAction(e -> {
                if (this.reviewOverlay != null) {
                    this.reviewOverlay.setVisible(true);
                    this.reviewOverlay.setManaged(true);
                }
            });
            card.getChildren().add(btnRecensione);
        } else if (controller.isReviewer() && controller.isReviewSaved()) {
            final Label doneLabel = new Label("Hai gia' lasciato una recensione. Grazie!");
            doneLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
            doneLabel.setTextFill(TEXT_MEDIUM);
            doneLabel.setWrapText(true);
            card.getChildren().add(doneLabel);
        }

        return card;
    }

    private VBox createChatCard() {
        final VBox card = new VBox(0);
        card.getStyleClass().add("soft-card");
        card.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final HBox chatHeader = new HBox();
        chatHeader.setPadding(new Insets(15));
        chatHeader.setBackground(new Background(new BackgroundFill(Color.web("#F8F9FA"), new CornerRadii(8, 8, 0, 0, false), Insets.EMPTY)));
        chatHeader.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        final Label chatTitle = new Label("Chat della Sessione");
        chatTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        chatTitle.getStyleClass().add("section-title");
        chatHeader.getChildren().add(chatTitle);

        final VBox messageArea = new VBox(10);
        messageArea.setPadding(new Insets(15));

        final ScrollPane scrollPane = new ScrollPane(messageArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(15));
        inputBox.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        final TextField messageField = new TextField();
        messageField.setPromptText("Scrivi un messaggio...");
        messageField.setFont(Font.font("System", 14));
        messageField.setPrefHeight(40);
        HBox.setHgrow(messageField, Priority.ALWAYS);

        final Button sendBtn = new Button("Invia");
        sendBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        sendBtn.setTextFill(Color.WHITE);
        sendBtn.setPrefHeight(40);
        sendBtn.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(6), Insets.EMPTY)));

        // PATTERN FACADE
        sendBtn.setOnAction(e -> {
            String testo = messageField.getText();
            if (!testo.trim().isEmpty()) {
                controller.inviaMessaggio(testo);
                messageField.clear();
                aggiornaMessaggi(messageArea);
            }
        });

        aggiornaMessaggi(messageArea);

        inputBox.getChildren().addAll(messageField, sendBtn);
        card.getChildren().addAll(chatHeader, scrollPane, inputBox);

        return card;
    }

    private StackPane createReviewOverlay() {
        final StackPane overlay = new StackPane();
        overlay.setVisible(false);
        overlay.setManaged(false);
        overlay.setPadding(new Insets(40));
        overlay.setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0, 0.35), CornerRadii.EMPTY, Insets.EMPTY)));

        final VBox banner = new VBox(12);
        banner.setPrefWidth(480);
        banner.setMaxWidth(520);
        banner.setMinWidth(420);
        banner.setMaxHeight(Region.USE_PREF_SIZE);
        banner.setPadding(new Insets(18, 18, 20, 18));
        banner.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(16), Insets.EMPTY)));
        banner.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(1))));

        final HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        final Label title = new Label("Lascia una Recensione");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        title.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final Label closeButton = new Label("X");
        closeButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        closeButton.setTextFill(TEXT_MEDIUM);
        closeButton.setStyle("-fx-cursor: hand;");
        closeButton.setOnMouseClicked(e -> {
            overlay.setVisible(false);
            overlay.setManaged(false);
        });

        header.getChildren().addAll(title, spacer, closeButton);

        final Label subtitle = new Label("Com'è andata questa sessione di tutoring?");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setTextFill(TEXT_MEDIUM);

        final HBox starBar = new HBox(8);
        starBar.setAlignment(Pos.CENTER_LEFT);
        final List<Label> stars = new ArrayList<>();
        // Inizializza il valore delle stelle con la recensione già presente se caricata da file.
        final int[] selectedStars = { controller.getReviewStars() >= 0 ? controller.getReviewStars() : 0 };

        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            final Label starLabel = new Label("☆");
            starLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
            starLabel.setTextFill(Color.web("#F0C419"));
            starLabel.setStyle("-fx-cursor: hand;");
            starLabel.setOnMouseClicked(e -> {
                selectedStars[0] = starValue;
                updateStarDisplay(stars, selectedStars[0]);
            });
            stars.add(starLabel);
            starBar.getChildren().add(starLabel);
        }

        updateStarDisplay(stars, selectedStars[0]);

        final TextArea reviewText = new TextArea();
        reviewText.setPromptText("Condividi la tua esperienza");
        reviewText.setWrapText(true);
        reviewText.setFont(Font.font("System", 13));
        reviewText.setPrefRowCount(3);
        reviewText.setPrefHeight(100);
        reviewText.setBackground(new Background(new BackgroundFill(Color.web("#F8F9FA"), new CornerRadii(8), Insets.EMPTY)));
        reviewText.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final Label errorLabel = new Label();
        errorLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        errorLabel.setTextFill(Color.web("#C82333"));
        errorLabel.setVisible(false);

        final Button saveButton = new Button("Salva");
        saveButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        saveButton.setTextFill(Color.WHITE);
        saveButton.setBackground(new Background(new BackgroundFill(Color.web("#28A745"), new CornerRadii(8), Insets.EMPTY)));
        saveButton.setPrefWidth(100);
        saveButton.setOnAction(e -> {
            // Richiediamo almeno stelle o testo prima di salvare la recensione.
            if (selectedStars[0] == 0 && reviewText.getText().trim().isEmpty()) {
                errorLabel.setText("Compila tutti i campi");
                errorLabel.setVisible(true);
                return;
            }
            errorLabel.setVisible(false);
            controller.registraRecensione(selectedStars[0], reviewText.getText(), UserSession.getDisplayName());
            overlay.setVisible(false);
            overlay.setManaged(false);
            showNotification("Recensione salvata con successo");
        });

        banner.getChildren().addAll(header, subtitle, starBar, reviewText, errorLabel, saveButton);
        overlay.getChildren().add(banner);
        StackPane.setAlignment(banner, Pos.CENTER);
        return overlay;
    }

    private Label createNotificationLabel() {
        final Label label = new Label();
        label.setVisible(false);
        label.setManaged(false);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("System", FontWeight.BOLD, 13));
        label.setPadding(new Insets(10, 18, 10, 18));
        label.setBackground(new Background(new BackgroundFill(Color.web("#28A745"), new CornerRadii(8), Insets.EMPTY)));
        return label;
    }

    private void showNotification(final String message) {
        if (this.notificationLabel == null) {
            return;
        }
        this.notificationLabel.setText(message);
        this.notificationLabel.setVisible(true);
        this.notificationLabel.setManaged(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(8), event -> {
                    this.notificationLabel.setVisible(false);
                    this.notificationLabel.setManaged(false);
                })
        );
        timeline.play();
    }

    private void updateStarDisplay(List<Label> stars, int selected) {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setText(i < selected ? "★" : "☆");
            stars.get(i).setTextFill(i < selected ? Color.web("#F0C419") : Color.web("#B5B5B5"));
        }
    }

    private void aggiornaMessaggi(VBox messageArea) {
        messageArea.getChildren().clear();
        for (Message m : controller.getModel().getStoricoChat()) {
            boolean isMe = m.getIdMittente().equals(controller.getUserMatricola());
            messageArea.getChildren().add(chatBubble(m.getTesto(), m.getIdMittente(), isMe));
        }
    }

    private String statoSessioneVisualizzato() {
        return statoSessioneVisualizzato(
                this.controller,
                this.box,
                this.counterpartyMatricola);
    }

    static String statoSessioneVisualizzato(
            final TutoringSessionController controller,
            final BoxTutoraggio box,
            final String counterpartyMatricola) {
        if (controller.isCompletataDaEntrambi()) {
            return "Sessione completata";
        }
        if (controller.isConfermata()) {
            return "Sessione confermata";
        }
        if (box == null) {
            return controller.isProposta()
                    ? "In attesa di conferma"
                    : "Candidature aperte";
        }

        final String viewer = controller.getUserMatricola();
        final String candidato = viewer.equals(box.getAutoreMatricola())
                ? counterpartyMatricola
                : viewer;

        if (candidato != null && candidato.equals(box.getConfermato())) {
            return "Sessione confermata";
        }
        if (candidato != null && box.isCandidato(candidato)) {
            return "In attesa di conferma";
        }
        return "Candidature aperte";
    }

    private Label createLabel(String text, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("System", bold ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        l.setTextFill(TEXT_MEDIUM);
        return l;
    }

    private VBox infoRow(String label, String value) {
        Label v = new Label(value);
        v.setFont(Font.font("System", FontWeight.NORMAL, 15));
        v.setTextFill(TEXT_DARK);
        return new VBox(2, createLabel(label, true), v);
    }

    private HBox chatBubble(String text, String senderId, boolean isMe) {
        final VBox bubble = new VBox(3);

        if (!isMe) {
            final Label senderLabel = new Label(SessionLinkUtil.nomeCompleto(senderId));
            senderLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
            senderLabel.setTextFill(TEXT_MEDIUM);
            bubble.getChildren().add(senderLabel);
        }

        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setFont(Font.font("System", 14));
        msgLabel.setPadding(new Insets(10, 14, 10, 14));

        if (isMe) {
            msgLabel.setTextFill(Color.WHITE);
            msgLabel.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(15, 15, 0, 15, false), Insets.EMPTY)));
        } else {
            msgLabel.setTextFill(TEXT_DARK);
            msgLabel.setBackground(new Background(new BackgroundFill(Color.web("#E9ECEF"), new CornerRadii(15, 15, 15, 0, false), Insets.EMPTY)));
        }

        bubble.getChildren().add(msgLabel);

        HBox row = new HBox(bubble);
        row.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }



    public static void main(final String[] args) {
        launch(args);
    }
}