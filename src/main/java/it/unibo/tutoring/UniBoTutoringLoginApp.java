package it.unibo.tutoring;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.view.components.AppIcon;
import it.unibo.tutoring.view.components.AppCard;
import it.unibo.tutoring.view.components.AppButton;
import it.unibo.tutoring.view.components.FormControlStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
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
    private static final Color TEXT_DARK = Color.web("#111111");

    private UniBoTutoringLoginApp() {
    }

    public static Scene createScene(final Stage stage) {
        final VBox root = new VBox(16);
        root.getStyleClass().add("app-shell");
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(22, 16, 22, 16));
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxWidth(520);

        final AppIcon leftArrowView = new AppIcon("arrow_left.png", 16, 16);

        final Button backHomeButton = new Button("Home", leftArrowView);
        backHomeButton.getStyleClass().add("back-button");
        backHomeButton.setOnAction(event -> {
            it.unibo.tutoring.view.components.NavigationHelper.goToHomeOrDashboard(stage);
        });
        topBar.getChildren().add(backHomeButton);

        final AppIcon logoView = new AppIcon("logo.png", 70, 70);

        final Label title = new Label("UniBo Tutoring");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 36));

        final Label subtitle = new Label("Università di Bologna");
        subtitle.setTextFill(Color.web("#6B6B6B"));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 22));

        final VBox brandBlock = new VBox(6, logoView, title, subtitle);
        brandBlock.setAlignment(Pos.CENTER);
        brandBlock.setCursor(Cursor.HAND);
        brandBlock.setOnMouseClicked(event -> it.unibo.tutoring.view.components.NavigationHelper.goToHomeOrDashboard(stage));

        final AppCard formCard = new AppCard(
                10, new Insets(20, 24, 18, 24), 12, Color.web("#C5C5C5"))
                .withMaxWidth(520);
        formCard.getStyleClass().add("auth-card");
        formCard.setAlignment(Pos.CENTER_LEFT);

        final Label formTitle = new Label("Accedi al tuo account");
        formTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 30));
        formTitle.setTextFill(TEXT_DARK);
        formTitle.setMaxWidth(Double.MAX_VALUE);
        formTitle.setTextAlignment(TextAlignment.CENTER);
        formTitle.setAlignment(Pos.CENTER);

        final Label formSubtitle = new Label("Inserisci la tua matricola e password per accedere");
        formSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
        formSubtitle.setTextFill(Color.web("#6A6A6A"));

        final Label matricolaLabel = new Label("Matricola");
        matricolaLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));
        matricolaLabel.setTextFill(TEXT_DARK);

        final TextField matricolaField = new TextField();
        matricolaField.setPromptText("es. 1234567890");
        FormControlStyle.apply(matricolaField, 44);
        matricolaField.setFont(Font.font("System", FontWeight.NORMAL, 20));

        final Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));
        passwordLabel.setTextFill(TEXT_DARK);

        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("min 6 caratteri, 1 numero");
        FormControlStyle.apply(passwordField, 44);
        passwordField.setFont(Font.font("System", FontWeight.NORMAL, 20));

        final AppButton loginButton = AppButton.primary("Accedi");
        loginButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        loginButton.setPrefHeight(48);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-scale-x: 1; -fx-scale-y: 1; -fx-translate-x: 0; -fx-translate-y: 0;");

        final Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(PRIMARY_RED);
        feedbackLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        feedbackLabel.setVisible(false);

        loginButton.setOnAction(event -> {
    final String matricola = matricolaField.getText().trim();
    final String password = passwordField.getText();

    if (!matricola.matches("\\d{10}")) {
        feedbackLabel.setText("Inserisci una matricola valida di 10 cifre.");
        feedbackLabel.setVisible(true);
        return;
    }
    if (password.isBlank()) {
        feedbackLabel.setText("Inserisci la password.");
        feedbackLabel.setVisible(true);
        return;
    }
    if (!AuthService.isPasswordValid(password)) {
        feedbackLabel.setText("La password deve avere almeno 6 caratteri, una lettera e un numero.");
        feedbackLabel.setVisible(true);
        return;
    }

    if (AuthService.getInstance().authenticate(matricola, password)) {

        UserAccount user = AuthService.getInstance().getUser(matricola);
        CurrentSession.setUser(user);

        it.unibo.tutoring.view.components.NavigationHelper.goToDashboard(stage);
        return;
    }

    feedbackLabel.setText("Credenziali non riconosciute.");
    feedbackLabel.setVisible(true);
});

        final Label registerPrefix = new Label("Non hai un account?");
        registerPrefix.setFont(Font.font("System", FontWeight.NORMAL, 20));
        registerPrefix.setTextFill(TEXT_DARK);

        final Button registerLink = new Button("Registrati");
        registerLink.setOnAction(event -> {
            stage.setScene(UniBoTutoringRegistrationApp.createScene(stage));
            stage.setTitle("UniBo Tutoring - Registrazione");
            it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
        });
        registerLink.getStyleClass().add("text-link");
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

        root.getChildren().addAll(topBar, brandBlock, formCard);

        return it.unibo.tutoring.view.components.WindowUtil.createScrollableScene(root);
    }
}