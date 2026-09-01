package it.unibo.tutoring;

import it.unibo.tutoring.view.components.AppIcon;
import it.unibo.tutoring.view.components.AppCard;
import it.unibo.tutoring.view.components.AppButton;
import it.unibo.tutoring.view.components.FormControlStyle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        title.setTextFill(TEXT_DARK);

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

        final Label formSubtitle = new Label(
            "Inserisci la tua matricola o email "
            + "\n e password per accedere");
        formSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
        formSubtitle.setTextAlignment(TextAlignment.CENTER);
        formSubtitle.setAlignment(Pos.CENTER);
        formSubtitle.setMaxWidth(Double.MAX_VALUE);
        formSubtitle.setTextFill(Color.web("#6A6A6A"));

        final Label matricolaLabel = new Label("Matricola o email");
        matricolaLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));
        matricolaLabel.setTextFill(TEXT_DARK);

        final TextField matricolaField = new TextField();
        matricolaField.setPromptText("es. 1234567890 oppure nome@studio.unibo.it");
        FormControlStyle.apply(matricolaField, 44);
        matricolaField.setFont(Font.font("System", FontWeight.NORMAL, 20));

        final Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));
        passwordLabel.setTextFill(TEXT_DARK);

        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("min 6 caratteri, una maiuscola, un numero e un carattere speciale");
        FormControlStyle.apply(passwordField, 44);
        passwordField.setFont(Font.font("System", FontWeight.NORMAL, 20));

        Hyperlink forgotPasswordLink = new Hyperlink("Password dimenticata?");
        forgotPasswordLink.setStyle("-fx-text-fill: #0056b3; -fx-font-size: 13px; -fx-border-color: transparent; -fx-padding: 0;");

        forgotPasswordLink.setOnAction(e -> {
            // Creazione della finestra di avviso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Recupero Password");
            alert.setHeaderText(null);
            alert.setContentText("Contatta il nostro supporto tutoring@unibo.it e ti invieremo le nuove credenziali il prima possibile.");

            alert.showAndWait();
        });

        final HBox passwordFieldWrapper = createPasswordFieldWithToggle(passwordField);

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
    final String identifier = matricolaField.getText().trim();
    final String password = passwordField.getText();

    if (identifier.isBlank()) {
        feedbackLabel.setText("Inserisci matricola o email.");
        feedbackLabel.setVisible(true);
        return;
    }
    if (password.isBlank()) {
        feedbackLabel.setText("Inserisci la password.");
        feedbackLabel.setVisible(true);
        return;
    }
    final UserAccount user = AuthService.getInstance().login(identifier, password);
    if (user != null) {
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
            passwordFieldWrapper,
            forgotPasswordLink,
            loginButton,
            feedbackLabel,
            registerLine
        );

        root.getChildren().addAll(topBar, brandBlock, formCard);

        return it.unibo.tutoring.view.components.WindowUtil.createScrollableScene(root);
    }

    private static HBox createPasswordFieldWithToggle(final PasswordField passwordField) {
        //crea un TextField per visualizzare la password in chiaro
        final TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText(passwordField.getPromptText());
       // Applica lo stesso stile del PasswordField originale
        FormControlStyle.apply(visiblePasswordField, 44);
        visiblePasswordField.setFont(passwordField.getFont());
        // Collega il TextField visibile al PasswordField originale
        // in modo che entrambi abbiano lo stesso contenuto
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        final AppIcon hiddenIcon = new AppIcon("eye_close.png", 22, 22);
        final AppIcon visibleIcon = new AppIcon("eye.png", 22, 22);
        //crea il pulsante per mostrare/nascondere la password
        final Button toggleVisibilityButton = new Button("", hiddenIcon);
        toggleVisibilityButton.setCursor(Cursor.HAND);
        toggleVisibilityButton.setStyle("-fx-background-color: transparent; -fx-padding: 6;");
        //toggleVisibilityButton.setFocusTraversable(false);

        //crea una variabile booleana per lo stato di visibilità della password
        //inizialmente la password è nascosta
        final BooleanProperty passwordVisible = new SimpleBooleanProperty(false);
        passwordVisible.addListener((observable, oldValue, newValue) -> {
            passwordField.setVisible(!newValue);
            passwordField.setManaged(!newValue);
            visiblePasswordField.setVisible(newValue);
            visiblePasswordField.setManaged(newValue);
            toggleVisibilityButton.setGraphic(newValue ? visibleIcon : hiddenIcon);
        });
        //al click del pulsante cambia lo stato di visibilità della password
        toggleVisibilityButton.setOnAction(event -> passwordVisible.set(!passwordVisible.get()));

        final HBox wrapper = new HBox(8, passwordField, visiblePasswordField, toggleVisibilityButton);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(visiblePasswordField, Priority.ALWAYS);
        return wrapper;
    }
}
