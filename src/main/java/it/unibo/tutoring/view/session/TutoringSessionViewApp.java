package it.unibo.tutoring.view.session;

import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserSession;
import it.unibo.tutoring.controller.session.TutoringSessionController;
import it.unibo.tutoring.model.chat.Message;
import it.unibo.tutoring.view.components.AppHeader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TutoringSessionViewApp extends Application {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");
    private static final Color CARD_BG = Color.WHITE;
    private final TutoringSessionController controller;

    public TutoringSessionViewApp() {
        this.controller = new TutoringSessionController();
    }

    private TutoringSessionViewApp(
            final String materiaAnnuncio,
            final String nomeInserzionista,
            final boolean tutorOffer) {
        this.controller = new TutoringSessionController(materiaAnnuncio, nomeInserzionista, tutorOffer);
    }

    public static Scene createScene(
            final Stage stage,
            final String materiaAnnuncio,
            final String nomeInserzionista,
            final boolean tutorOffer) {
        final TutoringSessionViewApp app = new TutoringSessionViewApp(materiaAnnuncio, nomeInserzionista, tutorOffer);
        return app.createScene(stage);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("UniBo Tutoring - Dettaglio Sessione");
        stage.setScene(createScene(stage));
        stage.show();
    }

    public Scene createScene(final Stage stage) {
        final VBox root = new VBox();
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
        return new Scene(root, 1320, 920);
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
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));
        card.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final Label title = new Label("Dettagli Sessione");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 22));
        title.setTextFill(TEXT_DARK);

        // Etichetta dello stato Dinamica
        final Label lblStatoValue = new Label("Proposta");
        lblStatoValue.setFont(Font.font("System", FontWeight.NORMAL, 15));
        lblStatoValue.setTextFill(TEXT_DARK);
        VBox rowStato = new VBox(2,
                createLabel("Stato:", true),
                lblStatoValue
        );

        card.getChildren().addAll(
                title, new Separator(),
                infoRow("Materia:", controller.getModel().getMateria()),
                infoRow(controller.getRuoloInserzionista(), controller.getNomeInserzionista()),
                rowStato
        );

        final Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        final Button btnConferma = new Button("Conferma Sessione");
        btnConferma.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnConferma.setTextFill(Color.WHITE);
        btnConferma.setMaxWidth(Double.MAX_VALUE);
        btnConferma.setBackground(new Background(new BackgroundFill(Color.web("#28A745"), new CornerRadii(6), Insets.EMPTY)));

        final Button btnCompleta = new Button("Completa Sessione");
        btnCompleta.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnCompleta.setTextFill(Color.WHITE);
        btnCompleta.setMaxWidth(Double.MAX_VALUE);
        btnCompleta.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(6), Insets.EMPTY)));
        btnCompleta.setDisable(true); // All'inizio non si può completare

        // PATTERN STATE
        btnConferma.setOnAction(e -> {
            try {
                controller.confermaSessione();
                lblStatoValue.setText("Confermata");
                btnConferma.setDisable(true); // Non puoi ri-confermare
                btnCompleta.setDisable(false); // Ora puoi completare
            } catch (IllegalStateException ex) {
                System.out.println("Errore di stato: " + ex.getMessage());
            }
        });

        btnCompleta.setOnAction(e -> {
            try {
                controller.completaSessione();
                lblStatoValue.setText("Completata");
                btnCompleta.setDisable(true);
            } catch (IllegalStateException ex) {
                System.out.println("Errore di stato: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(spacer, btnConferma, btnCompleta);
        return card;
    }

    private VBox createChatCard() {
        final VBox card = new VBox(0);
        card.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(8), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));

        final HBox chatHeader = new HBox();
        chatHeader.setPadding(new Insets(15));
        chatHeader.setBackground(new Background(new BackgroundFill(Color.web("#F8F9FA"), new CornerRadii(8, 8, 0, 0, false), Insets.EMPTY)));
        chatHeader.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        final Label chatTitle = new Label("Chat della Sessione");
        chatTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
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
                controller.inviaMessaggio(testo, "Tu"); // Invia al controller
                messageField.clear();
                aggiornaMessaggi(messageArea);
            }
        });

        aggiornaMessaggi(messageArea);

        inputBox.getChildren().addAll(messageField, sendBtn);
        card.getChildren().addAll(chatHeader, scrollPane, inputBox);

        return card;
    }

    private void aggiornaMessaggi(VBox messageArea) {
        messageArea.getChildren().clear();
        for (Message m : controller.getModel().getStoricoChat()) {
            boolean isMe = m.getIdMittente().equals("Tu");
            messageArea.getChildren().add(chatBubble(m.getTesto(), isMe));
        }
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

    private HBox chatBubble(String text, boolean isMe) {
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

        HBox row = new HBox(msgLabel);
        row.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }



    public static void main(final String[] args) {
        launch(args);
    }
}