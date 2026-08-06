package it.unibo.tutoring.view.box;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
import java.time.LocalTime;

import it.unibo.tutoring.model.box.BoxRepository;
import it.unibo.tutoring.model.box.BoxTutoraggioImpl;
import it.unibo.tutoring.model.box.BoxType;






public final class CreateAnnouncementViewApp {

    private static final Color PRIMARY_RED =
        Color.web("#D91E43");

    private static final Color PAGE_BG =
        Color.web("#EFEFEF");

    private static final Color TEXT_DARK =
        Color.web("#1B1B1B");

    private CreateAnnouncementViewApp() {
    }

    public static Scene createScene() {

        final UserAccount user =
            CurrentSession.getUser();

        final String userNome =
            user != null ? user.getName() : "Utente";

        final String userCognome =
            user != null ? user.getSurname() : "sconosciuto";

        final String userMatricola =
            user != null ? user.getMatricola() : "N/D";

        final VBox root = new VBox();
        root.getStyleClass().add("app-shell");
        root.setPrefSize(1200, 900);
        root.setFillWidth(true);

        root.setBackground(
            new Background(
                new BackgroundFill(
                    PAGE_BG,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        );

        final VBox pageContent = new VBox(24);
        pageContent.setPadding(new Insets(30));
        pageContent.setAlignment(Pos.TOP_CENTER);
        pageContent.setFillWidth(true);
        pageContent.setMaxWidth(Double.MAX_VALUE);

        final HBox header = new HBox();
        header.setMaxWidth(760);
        header.setPrefWidth(760);

        final Label title = new Label(
            "Nuovo Annuncio"
        );

        title.setFont(
            Font.font(
                "System",
                FontWeight.EXTRA_BOLD,
                32
            )
        );

        title.setTextFill(TEXT_DARK);

        final Region spacer = new Region();

        HBox.setHgrow(
            spacer,
            Priority.ALWAYS
        );

        final Button dashboardButton =
            new Button("← Dashboard");

        dashboardButton.getStyleClass().add("text-link");
        dashboardButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        dashboardButton.setTextFill(TEXT_DARK);
        dashboardButton.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        dashboardButton.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        dashboardButton.setPadding(new Insets(8, 14, 8, 14));
        dashboardButton.setCursor(Cursor.HAND);

        dashboardButton.setOnAction(event -> {

            final Stage stage =
                (Stage) dashboardButton
                    .getScene()
                    .getWindow();

            stage.setScene(
                UniBoTutoringDashboardApp
                    .createScene()
            );

            stage.setTitle(
                "UniBo Tutoring - Dashboard"
            );
            it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
        });

        header.getChildren().addAll(
            title,
            spacer,
            dashboardButton
        );

        final VBox card = new VBox(6);
        card.getStyleClass().add("auth-card");

        card.setPadding(new Insets(30));
        card.setMaxWidth(760);
        card.setPrefWidth(760);
        card.setMinWidth(0);

        card.setAlignment(Pos.TOP_LEFT);

        card.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(16),
                    Insets.EMPTY
                )
            )
        );

        card.setBorder(
            new Border(
                new BorderStroke(
                    Color.web("#D6D6D6"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(16),
                    BorderWidths.DEFAULT
                )
            )
        );

        final Label sessionLabel =
            new Label(
                "Sessione con "
                + userNome
                + " "
                + userCognome
            );

        sessionLabel.setFont(
            Font.font(
                "System",
                FontWeight.EXTRA_BOLD,
                24
            )
        );

        sessionLabel.setTextFill(TEXT_DARK);
        VBox.setMargin(sessionLabel, new Insets(0, 0, 14, 0));

        final ComboBox<String> corsoBox = new ComboBox<>();
        corsoBox.getItems().addAll(it.unibo.tutoring.model.box.CorsiDiStudio.TUTTI);
        corsoBox.setPromptText("Seleziona il corso");
        styleField(corsoBox);

        final TextField materiaField = new TextField();
        materiaField.setPromptText("Es. Programmazione ad Oggetti");
        styleField(materiaField);

        final TextField argomentoField = new TextField();
        argomentoField.setPromptText("Es. Pattern MVC");
        styleField(argomentoField);

        final DatePicker dataPicker = new DatePicker();
        styleField(dataPicker);

        final TextField oraField = new TextField();
        oraField.setPromptText("HH:mm");
        styleField(oraField);

        final Spinner<Integer> durataSpinner = new Spinner<>(1, 8, 2);
        styleField(durataSpinner);

        final ToggleGroup tipoGroup = new ToggleGroup();

        final RadioButton offertaRadio = new RadioButton("Offerta Tutoraggio (offro aiuto)");
        offertaRadio.setToggleGroup(tipoGroup);
        offertaRadio.setSelected(true);
        offertaRadio.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        offertaRadio.setCursor(Cursor.HAND);
        offertaRadio.setTextFill(TEXT_DARK);
        offertaRadio.setWrapText(true);

        final RadioButton richiestaRadio = new RadioButton("Richiesta Tutoraggio (cerco aiuto)");
        richiestaRadio.setToggleGroup(tipoGroup);
        richiestaRadio.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        richiestaRadio.setCursor(Cursor.HAND);
        richiestaRadio.setTextFill(TEXT_DARK);
        richiestaRadio.setWrapText(true);

        final HBox offertaBox = new HBox(offertaRadio);
        offertaBox.setAlignment(Pos.CENTER_LEFT);
        offertaBox.setPadding(new Insets(10, 12, 10, 12));
        offertaBox.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #D6D6D6; -fx-border-radius: 8; -fx-background-radius: 8;");

        final HBox richiestaBox = new HBox(richiestaRadio);
        richiestaBox.setAlignment(Pos.CENTER_LEFT);
        richiestaBox.setPadding(new Insets(10, 12, 10, 12));
        richiestaBox.setStyle("-fx-background-color: white; -fx-border-color: #D6D6D6; -fx-border-radius: 8; -fx-background-radius: 8;");

        final HBox tipoRow = new HBox(12, offertaBox, richiestaBox);
        tipoRow.setAlignment(Pos.CENTER_LEFT);

        tipoGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            final boolean offerSelected = offertaRadio.isSelected();
            offertaBox.setStyle(offerSelected
                ? "-fx-background-color: #FFF5F7; -fx-border-color: #D91E43; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;"
                : "-fx-background-color: white; -fx-border-color: #D6D6D6; -fx-border-radius: 8; -fx-background-radius: 8;");
            richiestaBox.setStyle(!offerSelected
                ? "-fx-background-color: #FFF5F7; -fx-border-color: #D91E43; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;"
                : "-fx-background-color: white; -fx-border-color: #D6D6D6; -fx-border-radius: 8; -fx-background-radius: 8;");
        });

        final Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(PRIMARY_RED);
        feedbackLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        feedbackLabel.setWrapText(true);
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);

        final Button publishButton = new Button("Pubblica Annuncio");

        publishButton.getStyleClass().add("primary-btn");
        publishButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        publishButton.setTextFill(Color.WHITE);
        publishButton.setPadding(new Insets(11, 22, 11, 22));
        publishButton.setCursor(Cursor.HAND);

        publishButton.setBackground(
            new Background(
                new BackgroundFill(
                    PRIMARY_RED,
                    new CornerRadii(8),
                    Insets.EMPTY
                )
            )
        );

        publishButton.setBorder(Border.EMPTY);

        publishButton.setOnAction(event -> {

            if (corsoBox.getValue() == null
                || materiaField.getText().isBlank()
                || argomentoField.getText().isBlank()
                || dataPicker.getValue() == null
                || oraField.getText().isBlank()) {

                feedbackLabel.setText("Compila tutti i campi prima di pubblicare.");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                return;
            }

            try {

                final LocalTime ora = LocalTime.parse(oraField.getText());

                final BoxType tipo =
                    offertaRadio.isSelected()
                        ? BoxType.OFFER
                        : BoxType.REQUEST;

                final String titolo =
                    "Sessione con "
                    + userNome
                    + " "
                    + userCognome;

                final BoxTutoraggioImpl box =
                    new BoxTutoraggioImpl(
                        titolo,
                        corsoBox.getValue(),
                        materiaField.getText(),
                        argomentoField.getText(),
                        dataPicker.getValue(),
                        ora,
                        durataSpinner.getValue(),
                        userMatricola,
                        tipo
                    );

                BoxRepository.addBox(box);

                final Stage stage =
                    (Stage) publishButton
                        .getScene()
                        .getWindow();

                stage.setScene(
                    UniBoTutoringDashboardApp
                        .createScene()
                );

                stage.setTitle(
                    "UniBo Tutoring - Dashboard"
                );
                it.unibo.tutoring.view.components.WindowUtil.maximize(stage);

            } catch (final Exception exception) {

                feedbackLabel.setText("Formato orario non valido. Usa HH:mm (es. 15:00).");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
            }
        });

        final HBox publishRow = new HBox(publishButton);
        publishRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(publishRow, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(
            sessionLabel,

            fieldLabel("Corso"),
            corsoBox,

            fieldLabel("Materia"),
            materiaField,

            fieldLabel("Argomento"),
            argomentoField,

            fieldLabel("Data"),
            dataPicker,

            fieldLabel("Orario"),
            oraField,

            fieldLabel("Durata (ore)"),
            durataSpinner,

            fieldLabel("Tipo annuncio"),
            tipoRow,

            feedbackLabel,
            publishRow
        );

        pageContent.getChildren().addAll(
            header,
            card
        );

        final ScrollPane scrollPane = new ScrollPane(pageContent);
        it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(scrollPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setBackground(Background.EMPTY);

        root.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final Scene scene = new Scene(root, 1200, 900);
        scene.getStylesheets().add(CreateAnnouncementViewApp.class.getResource("/styles.css").toExternalForm());
        return scene;
    }

    private static Label fieldLabel(final String text) {
        final Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web("#4A4A4A"));
        VBox.setMargin(label, new Insets(8, 0, 0, 0));
        return label;
    }

    private static void styleField(final javafx.scene.control.Control field) {
        field.setPrefHeight(38);
        field.setMaxWidth(Double.MAX_VALUE);
        field.getStyleClass().add("form-field");
        field.setStyle(
            "-fx-background-color: white;"
            + "-fx-border-color: #CFCFCF;"
            + "-fx-border-radius: 7;"
            + "-fx-background-radius: 7;"
            + "-fx-font-family: 'System';"
            + "-fx-font-size: 13px;"
        );
    }
}