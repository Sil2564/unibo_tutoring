package it.unibo.tutoring;

import it.unibo.tutoring.AuthService;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.view.components.AppIcon;
import java.nio.file.Path;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.layout.Priority;
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
    private static final Color TEXT_DARK = Color.web("#111111");

    private UniBoTutoringRegistrationApp() {
    }

    public static Scene createScene(final Stage stage) {
        final VBox root = new VBox(16);
        root.getStyleClass().add("app-shell");
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
        backHomeButton.getStyleClass().add("back-button");
        backHomeButton.setOnAction(event -> {
            it.unibo.tutoring.view.components.NavigationHelper.goToHomeOrDashboard(stage);
        });
        topBar.getChildren().add(backHomeButton);

        final Image logo = new Image(Path.of("src", "icons", "logo.png").toUri().toString());
        final ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(70);
        logoView.setFitHeight(70);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        final Label title = new Label("UniBo Tutoring");
        title.setTextFill(Color.web("#111111"));
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 36));

        final Label subtitle = new Label("Università di Bologna");
        subtitle.setTextFill(Color.web("#6B6B6B"));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 22));

        final VBox brandBlock = new VBox(6, logoView, title, subtitle);
        brandBlock.setAlignment(Pos.CENTER);
        brandBlock.setCursor(Cursor.HAND);
        brandBlock.setOnMouseClicked(event -> it.unibo.tutoring.view.components.NavigationHelper.goToHomeOrDashboard(stage));

        final VBox formCard = new VBox(12);
        formCard.getStyleClass().add("auth-card");
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setPadding(new Insets(18, 24, 16, 24));
        formCard.setMaxWidth(740);
        formCard.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(12), Insets.EMPTY)));
        formCard.setBorder(new Border(new BorderStroke(Color.web("#C5C5C5"), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

        final Label formTitle = new Label("Registra un nuovo account");
        formTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 28));
        formTitle.setTextFill(TEXT_DARK);
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

        final TextField nameField = createTextField("es. Mario");
        final TextField surnameField = createTextField("es. Rossi");
        final TextField birthDateField = createTextField("GG/MM/AAAA");
        final TextField matricolaField = createTextField("es. 1234567890");
        final TextField emailField = createTextField("mario.rossi@studio.unibo.it");
        final PasswordField passwordField = createPasswordField("min 6 caratteri, 1 numero");
        final PasswordField confirmPasswordField = createPasswordField("min 6 caratteri, 1 numero");
        final HBox passwordFieldWrapper = createPasswordFieldWithToggle(passwordField);
        final HBox confirmPasswordFieldWrapper = createPasswordFieldWithToggle(confirmPasswordField);

        // Aggiunto da Niki: Il menu a tendina per far scegliere il corso all'utente durante l'iscrizione.
        // Carica in automatico gli stessi corsi definiti in CorsiDiStudio.TUTTI per rimanere coerente con la dashboard.
        final ComboBox<String> corsoBox = new ComboBox<>();
        corsoBox.getItems().addAll(it.unibo.tutoring.model.box.CorsiDiStudio.TUTTI);
        corsoBox.setPromptText("Seleziona corso");
        corsoBox.setPrefWidth(205);
        corsoBox.setPrefHeight(40);
        corsoBox.setStyle("-fx-font-size: 16px;");

        addField(fieldsGrid, 0, 0, "Nome", nameField);
        addField(fieldsGrid, 1, 0, "Cognome", surnameField);
        addField(fieldsGrid, 2, 0, "Data di Nascita", birthDateField);
        addField(fieldsGrid, 0, 1, "Matricola", matricolaField);
        addField(fieldsGrid, 1, 1, "Email", emailField);
        addField(fieldsGrid, 2, 1, "Corso di Studi", corsoBox);
        addField(fieldsGrid, 0, 2, "Password", passwordFieldWrapper);
        addField(fieldsGrid, 1, 2, "Conferma password", confirmPasswordFieldWrapper);

        final HBox submitWrap = new HBox();
        submitWrap.setAlignment(Pos.CENTER);
        final Button registerButton = new Button("Registrati");
        registerButton.getStyleClass().add("primary-btn");
        registerButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setMinWidth(300);
        registerButton.setPrefHeight(46);
        registerButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(10), Insets.EMPTY)));
        submitWrap.getChildren().add(registerButton);

        final Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(PRIMARY_RED);
        feedbackLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        feedbackLabel.setVisible(false);

        registerButton.setOnAction(event -> {
            final String name = nameField.getText().trim();
            final String surname = surnameField.getText().trim();
            final String birthDate = birthDateField.getText().trim();
            final String matricola = matricolaField.getText().trim();
            final String email = emailField.getText().trim();
            final String password = passwordField.getText();
            final String confirmPassword = confirmPasswordField.getText();
            // Aggiunto da Niki: Preleviamo il corso selezionato. Se non seleziona nulla (null), la validazione sotto lo blocca.
            final String corso = corsoBox.getValue();

            if (name.isBlank() || surname.isBlank() || birthDate.isBlank() || matricola.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || corso == null) {
                feedbackLabel.setText("Compila tutti i campi.");
                feedbackLabel.setVisible(true);
                return;
            }
            if (!birthDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                feedbackLabel.setText("Formato data errato (GG/MM/AAAA).");
                feedbackLabel.setVisible(true);
                return;
            }
            if (!matricola.matches("\\d{10}")) {
                feedbackLabel.setText("La matricola deve contenere 10 cifre.");
                feedbackLabel.setVisible(true);
                return;
            }
            if (!email.contains("@")) {
                feedbackLabel.setText("Inserisci una email valida.");
                feedbackLabel.setVisible(true);
                return;
            }
            if (!AuthService.isPasswordValid(password)) {
                feedbackLabel.setText("La password deve avere almeno 6 caratteri, una maiuscola, un numero e un carattere speciale.");
                feedbackLabel.setVisible(true);
                return;
            }
            if (!password.equals(confirmPassword)) {
                feedbackLabel.setText("Le password non coincidono.");
                feedbackLabel.setVisible(true);
                return;
            }

            // Aggiunto da Niki: passiamo anche il corso all'AuthService per salvarlo correttamente
            final AuthService.RegistrationResult result = AuthService.getInstance().register(name, surname, matricola, email, password, birthDate, corso);
            if (!result.isSuccess()) {
                feedbackLabel.setText(result.getMessage());
                feedbackLabel.setVisible(true);
                return;
            }

            CurrentSession.setUser(AuthService.getInstance().getUser(matricola));

            it.unibo.tutoring.view.components.NavigationHelper.goToDashboard(stage);
        });

        final Label loginPrefix = new Label("Hai già un account?");
        loginPrefix.setFont(Font.font("System", FontWeight.NORMAL, 20));
        loginPrefix.setTextFill(TEXT_DARK);

        final Button loginLink = new Button("Accedi");
        loginLink.setOnAction(event -> {
            stage.setScene(UniBoTutoringLoginApp.createScene(stage));
            stage.setTitle("UniBo Tutoring - Login");
            it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
        });
        loginLink.getStyleClass().add("text-link");
        loginLink.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
        loginLink.setTextFill(PRIMARY_RED);
        loginLink.setBackground(Background.EMPTY);
        loginLink.setBorder(Border.EMPTY);
        loginLink.setPadding(new Insets(0));

        final HBox loginLine = new HBox(6, loginPrefix, loginLink);
        loginLine.setAlignment(Pos.CENTER);
        loginLine.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(formTitle, formSubtitle, fieldsGrid, submitWrap, feedbackLabel, loginLine);
        root.getChildren().addAll(topBar, brandBlock, formCard);

        return it.unibo.tutoring.view.components.WindowUtil.createScrollableScene(root);
    }

    private static void addField(final GridPane grid, final int column, final int row, final String labelText, final Node field) {
        final VBox cell = new VBox(6);
        final Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.NORMAL, 22));
        label.setTextFill(TEXT_DARK);
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

    private static HBox createPasswordFieldWithToggle(final PasswordField passwordField) {
        final TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText(passwordField.getPromptText());
        styleField(visiblePasswordField);
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        final AppIcon hiddenIcon = new AppIcon("eye_close.png", 20, 20);
        final AppIcon visibleIcon = new AppIcon("eye.png", 20, 20);

        final Button toggleVisibilityButton = new Button("", hiddenIcon);
        toggleVisibilityButton.setCursor(Cursor.HAND);
        toggleVisibilityButton.setStyle("-fx-background-color: transparent; -fx-padding: 6;");
        toggleVisibilityButton.setFocusTraversable(false);

        final BooleanProperty passwordVisible = new SimpleBooleanProperty(false);
        passwordVisible.addListener((observable, oldValue, newValue) -> {
            passwordField.setVisible(!newValue);
            passwordField.setManaged(!newValue);
            visiblePasswordField.setVisible(newValue);
            visiblePasswordField.setManaged(newValue);
            toggleVisibilityButton.setGraphic(newValue ? visibleIcon : hiddenIcon);
        });
        toggleVisibilityButton.setOnAction(event -> passwordVisible.set(!passwordVisible.get()));

        final HBox wrapper = new HBox(8, passwordField, visiblePasswordField, toggleVisibilityButton);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(visiblePasswordField, Priority.ALWAYS);
        return wrapper;
    }

    private static void styleField(final TextField field) {
        field.setFont(Font.font("System", FontWeight.NORMAL, 18));
        field.setPrefWidth(205);
        field.setPrefHeight(40);
    }
}