package it.unibo.tutoring;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class UniBoTutoringLoginApp {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#ECECEC");
    private static final Color CARD_BG = Color.web("#ffffff");

    private UniBoTutoringLoginApp() {
    }

    public static Scene createScene(final Stage stage) {
        final VBox root = new VBox(16);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(22, 16, 22, 16));
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxWidth(520);

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

        final VBox formCard = new VBox(10);
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setPadding(new Insets(20, 24, 18, 24));
        formCard.setMaxWidth(520);
        formCard.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(12), Insets.EMPTY)));
        formCard.setBorder(new Border(new BorderStroke(Color.web("#C5C5C5"), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

        final Label formTitle = new Label("Accedi al tuo account");
        formTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 30));
        formTitle.setMaxWidth(Double.MAX_VALUE);
        formTitle.setTextAlignment(TextAlignment.CENTER);
        formTitle.setAlignment(Pos.CENTER);

        final Label formSubtitle = new Label("Inserisci la tua matricola e password per accedere");
        formSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
        formSubtitle.setTextFill(Color.web("#6A6A6A"));

        final Label matricolaLabel = new Label("Matricola");
        matricolaLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));

        final TextField matricolaField = new TextField();
        matricolaField.setPromptText("es. 1234567");
        matricolaField.setFont(Font.font("System", FontWeight.NORMAL, 20));
        matricolaField.setPrefHeight(44);

        final Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));

        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("******");
        passwordField.setFont(Font.font("System", FontWeight.NORMAL, 20));
        passwordField.setPrefHeight(44);

        final Button loginButton = new Button("Accedi");
        loginButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        loginButton.setTextFill(Color.WHITE);
        loginButton.setPrefHeight(48);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(10), Insets.EMPTY)));

        final Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(PRIMARY_RED);
        feedbackLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        feedbackLabel.setVisible(false);

        loginButton.setOnAction(event -> {
            final String matricola = matricolaField.getText().trim();
            final String password = passwordField.getText();

            if (!matricola.matches("\\d{7}")) {
                feedbackLabel.setText("Inserisci una matricola valida di 7 cifre.");
                feedbackLabel.setVisible(true);
                return;
            }
            if (password.isBlank()) {
                feedbackLabel.setText("Inserisci la password.");
                feedbackLabel.setVisible(true);
                return;
            }

            if (AuthService.getInstance().authenticate(matricola, password)) {
                stage.setScene(UniBoTutoringDashboardApp.createScene());
                stage.setTitle("UniBo Tutoring - Dashboard");
                return;
            }

            feedbackLabel.setText("Credenziali non riconosciute.");
            feedbackLabel.setVisible(true);
        });

        final Label registerPrefix = new Label("Non hai un account?");
        registerPrefix.setFont(Font.font("System", FontWeight.NORMAL, 20));

        final Button registerLink = new Button("Registrati");
        registerLink.setOnAction(event -> {
            stage.setScene(UniBoTutoringRegistrationApp.createScene(stage));
            stage.setTitle("UniBo Tutoring - Registrazione");
        });
        registerLink.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
        registerLink.setTextFill(PRIMARY_RED);
        registerLink.setBackground(Background.EMPTY);
        registerLink.setBorder(Border.EMPTY);
        registerLink.setPadding(new Insets(0));

        final HBox registerLine = new HBox(6, registerPrefix, registerLink);
        registerLine.setAlignment(Pos.CENTER);
        registerLine.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(
            formTitle,
            formSubtitle,
            matricolaLabel,
            matricolaField,
            passwordLabel,
            passwordField,
            loginButton,
            feedbackLabel,
            registerLine
        );

        root.getChildren().addAll(topBar, logoView, title, subtitle, formCard);

        return new Scene(root, 1180, 900);
    }
}
