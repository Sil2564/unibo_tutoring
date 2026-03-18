package it.unibo.samplejavafx.unibotutoring;

import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class UniBoTutoringRegistrationApp {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#ECECEC");
    private static final Color CARD_BG = Color.web("#ffffff");

    private UniBoTutoringRegistrationApp() {
    }

    public static Scene createScene(final Stage stage) {
        final VBox root = new VBox(16);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(22, 16, 22, 16));
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxWidth(740);

        final Image leftArrow = new Image(Path.of("src", "icons", "arrow_left.png").toUri().toString());
        final ImageView leftArrowView = new ImageView(leftArrow);
        leftArrowView.setFitWidth(16);
        leftArrowView.setFitHeight(16);
        leftArrowView.setPreserveRatio(true);
        leftArrowView.setSmooth(true);

        final Button backHomeButton = new Button("Home", leftArrowView);
        backHomeButton.setOnAction(event -> {
            stage.setScene(UniBoTutoringHomeApp.createScene());
            stage.setTitle("UniBo Tutoring - Home");
        });
        backHomeButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        backHomeButton.setTextFill(Color.web("#4A4A4A"));
        backHomeButton.setBackground(Background.EMPTY);
        backHomeButton.setBorder(Border.EMPTY);
        backHomeButton.setPadding(new Insets(0));
        topBar.getChildren().add(backHomeButton);

        final Image logo = new Image(Path.of("src", "icons", "unibo.png").toUri().toString());
        final ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(70);
        logoView.setFitHeight(70);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        final Label title = new Label("UniBo Tutoring");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 36));

        final Label subtitle = new Label("Università di Bologna");
        subtitle.setTextFill(Color.web("#6B6B6B"));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 22));

        final VBox formCard = new VBox(12);
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setPadding(new Insets(18, 24, 16, 24));
        formCard.setMaxWidth(740);
        formCard.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(12), Insets.EMPTY)));
        formCard.setBorder(new Border(new BorderStroke(Color.web("#C5C5C5"), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

        final Label formTitle = new Label("Registra un nuovo account");
        formTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 28));
        formTitle.setMaxWidth(Double.MAX_VALUE);
        formTitle.setTextAlignment(TextAlignment.CENTER);
        formTitle.setAlignment(Pos.CENTER);

        final Label formSubtitle = new Label("Solo studenti UniBo con matricola valida");
        formSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 18));
        formSubtitle.setTextFill(Color.web("#6A6A6A"));
        formSubtitle.setMaxWidth(Double.MAX_VALUE);
        formSubtitle.setTextAlignment(TextAlignment.CENTER);
        formSubtitle.setAlignment(Pos.CENTER);

        final GridPane fieldsGrid = new GridPane();
        fieldsGrid.setHgap(16);
        fieldsGrid.setVgap(10);

        addField(fieldsGrid, 0, 0, "Nome", createTextField(""));
        addField(fieldsGrid, 1, 0, "Matricola", createTextField("es. 0000123456"));
        addField(fieldsGrid, 2, 0, "Email", createTextField("mario.rossi@studio.unibo.it"));
        addField(fieldsGrid, 0, 1, "Cognome", createTextField(""));
        addField(fieldsGrid, 1, 1, "Password", createPasswordField("******"));
        addField(fieldsGrid, 2, 1, "Conferma password", createPasswordField("******"));

        final HBox submitWrap = new HBox();
        submitWrap.setAlignment(Pos.CENTER);
        final Button registerButton = new Button("Registrati");
        registerButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setMinWidth(300);
        registerButton.setPrefHeight(46);
        registerButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(10), Insets.EMPTY)));
        submitWrap.getChildren().add(registerButton);

        final Label loginPrefix = new Label("Hai già un account?");
        loginPrefix.setFont(Font.font("System", FontWeight.NORMAL, 20));

        final Button loginLink = new Button("Accedi");
        loginLink.setOnAction(event -> {
            stage.setScene(UniBoTutoringLoginApp.createScene(stage));
            stage.setTitle("UniBo Tutoring - Login");
        });
        loginLink.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
        loginLink.setTextFill(PRIMARY_RED);
        loginLink.setBackground(Background.EMPTY);
        loginLink.setBorder(Border.EMPTY);
        loginLink.setPadding(new Insets(0));

        final HBox loginLine = new HBox(6, loginPrefix, loginLink);
        loginLine.setAlignment(Pos.CENTER);
        loginLine.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(formTitle, formSubtitle, fieldsGrid, submitWrap, loginLine);
        root.getChildren().addAll(topBar, logoView, title, subtitle, formCard);

        return new Scene(root, 1180, 900);
    }

    private static void addField(final GridPane grid, final int column, final int row, final String labelText, final TextField field) {
        final VBox cell = new VBox(6);
        final Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.NORMAL, 22));
        cell.getChildren().addAll(label, field);
        grid.add(cell, column, row);
    }

    private static TextField createTextField(final String placeholder) {
        final TextField field = new TextField();
        field.setPromptText(placeholder);
        styleField(field);
        return field;
    }

    private static PasswordField createPasswordField(final String placeholder) {
        final PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        styleField(field);
        return field;
    }

    private static void styleField(final TextField field) {
        field.setFont(Font.font("System", FontWeight.NORMAL, 18));
        field.setPrefWidth(205);
        field.setPrefHeight(40);
    }
}
