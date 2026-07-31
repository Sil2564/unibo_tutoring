package it.unibo.tutoring.view.box;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserSession;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.box.BoxType;
import it.unibo.tutoring.view.components.AppHeader;
import it.unibo.tutoring.view.session.TutoringSessionViewApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
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
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);

    private AnnouncementDetailViewApp() {
    }

    public static Scene createScene(final Stage stage, final BoxTutoraggio box) {

        final boolean offer = box.getTipo() == BoxType.OFFER;
        final String autoreNome = estraiNomeAutore(box.getTitolo());

        final VBox root = new VBox();
        root.getStyleClass().add("app-shell");
        root.setBackground(
            new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY))
        );

        final AppHeader header = new AppHeader(
            UserSession.getDisplayName(),
            stage != null ? UserSession.createLogoutAction(stage) : null
        );

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

        final Button backButton = new Button("← Dashboard");
        backButton.getStyleClass().add("text-link");
        backButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        backButton.setTextFill(TEXT_DARK);
        backButton.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        backButton.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        backButton.setPadding(new Insets(8, 14, 8, 14));
        backButton.setCursor(Cursor.HAND);
        backButton.setOnAction(event -> {
            final Stage win = stage != null ? stage : (Stage) backButton.getScene().getWindow();
            win.setScene(UniBoTutoringDashboardApp.createScene());
            win.setTitle("UniBo Tutoring - Dashboard");
            it.unibo.tutoring.view.components.WindowUtil.maximize(win);
        });

        topRow.getChildren().addAll(title, spacer, backButton);

        final VBox card = new VBox(10);
        card.getStyleClass().add("auth-card");
        card.setPadding(new Insets(30));
        card.setMaxWidth(760);
        card.setPrefWidth(760);
        card.setAlignment(Pos.TOP_LEFT);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(16), BorderWidths.DEFAULT)));

        final Label tag = new Label(offer ? "Offerta tutoraggio" : "Cerco tutor");
        tag.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 11));
        tag.setTextFill(Color.WHITE);
        tag.setPadding(new Insets(4, 10, 4, 10));
        tag.setBackground(new Background(new BackgroundFill(offer ? PRIMARY_RED : Color.web("#A1A1A1"), new CornerRadii(999), Insets.EMPTY)));

        final Label sessionTitle = new Label(box.getTitolo() + (offer ? " (Tutor)" : " (Studente)"));
        sessionTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        sessionTitle.setTextFill(TEXT_DARK);
        sessionTitle.setWrapText(true);
        VBox.setMargin(sessionTitle, new Insets(10, 0, 6, 0));

        final Separator separator = new Separator();

        card.getChildren().addAll(
            tag,
            sessionTitle,
            separator,

            detailRow("Corso", box.getCorso()),
            detailRow("Materia", box.getMateria()),
            detailRow("Argomento", box.getArgomento()),
            detailRow("Data", box.getData() != null ? box.getData().format(DATE_FORMAT) : "N/D"),
            detailRow("Orario", box.getOra() != null ? box.getOra().toString() : "N/D"),
            detailRow("Durata", box.getDurataOre() + " ore"),
            detailRow("Tipo annuncio", offer ? "Offerta tutoraggio (offre aiuto)" : "Richiesta tutoraggio (cerca aiuto)"),
            detailRow(offer ? "Tutor" : "Studente", autoreNome)
        );

        final Button contactButton = new Button("Contatta");
        contactButton.getStyleClass().add("primary-btn");
        contactButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        contactButton.setTextFill(Color.WHITE);
        contactButton.setPadding(new Insets(11, 22, 11, 22));
        contactButton.setCursor(Cursor.HAND);
        contactButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(8), Insets.EMPTY)));
        contactButton.setBorder(Border.EMPTY);
        contactButton.setOnAction(event -> {
            final Stage win = (Stage) contactButton.getScene().getWindow();
            win.setScene(TutoringSessionViewApp.createScene(win, box.getMateria(), autoreNome, offer, box.getAutoreMatricola()));
            win.setTitle("UniBo Tutoring - Dettaglio Sessione");
            it.unibo.tutoring.view.components.WindowUtil.maximize(win);
        });

        final HBox actionsRow = new HBox(contactButton);
        actionsRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(actionsRow, new Insets(16, 0, 0, 0));

        card.getChildren().add(actionsRow);

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

    private static String estraiNomeAutore(final String titolo) {
        final String prefix = "Sessione con ";
        return titolo != null && titolo.startsWith(prefix) ? titolo.substring(prefix.length()) : titolo;
    }
}
