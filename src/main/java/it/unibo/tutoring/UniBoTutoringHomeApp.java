package it.unibo.tutoring;
import it.unibo.tutoring.view.components.AppFooter;
import it.unibo.tutoring.view.components.AppIcon;
import it.unibo.tutoring.view.components.AppCard;
import it.unibo.tutoring.view.components.AppButton;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class UniBoTutoringHomeApp extends Application {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color LIGHT_BACKGROUND = Color.web("#F5F5F5");
    @Override
    public void start(final Stage stage) {
        final Scene scene = createScene();
        stage.setTitle("UniBo Tutoring - Home");
        stage.setScene(scene);
        stage.show();
        it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
    }

    public static Scene createScene() {
        final UniBoTutoringHomeApp app = new UniBoTutoringHomeApp();
        final VBox page = new VBox(
            app.createHeroSection(),
            app.createHowItWorksSection(),
            app.createWhySection(),
            new AppFooter()
        );
        page.getStyleClass().add("page-shell");
        page.setBackground(new Background(new BackgroundFill(LIGHT_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        page.setMinHeight(Region.USE_PREF_SIZE);

        final ScrollPane scrollPane = new ScrollPane(page);
        it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(scrollPane);

        final Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add(UniBoTutoringHomeApp.class.getResource("/styles.css").toExternalForm());
        scene.setRoot(scrollPane);
        return scene;
    }

    private VBox createHeroSection() {
        final VBox section = new VBox(0);
        section.getStyleClass().add("hero-shell");
        section.setBackground(new Background(new BackgroundFill(LIGHT_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 24, 8, 24));
        topBar.getStyleClass().add("app-header");
        topBar.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        final ImageView uniBoLogo = new AppIcon("logo.png", 34, 34);

        final VBox brand = new VBox(2);
        final Label brandTitle = new Label("UniBo Tutoring");
        brandTitle.setTextFill(Color.web("#1A1A1A"));
        brandTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 19));
        final Label brandSubtitle = new Label("Università di Bologna");
        brandSubtitle.setTextFill(Color.web("#4B4B4B"));
        brandSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 12));
        brand.getChildren().addAll(brandTitle, brandSubtitle);

        final HBox brandBlock = new HBox(8, uniBoLogo, brand);
        brandBlock.setAlignment(Pos.CENTER_LEFT);
        brandBlock.setCursor(Cursor.HAND);
        brandBlock.setOnMouseClicked(event -> {
            final Stage stage = (Stage) brandBlock.getScene().getWindow();
            it.unibo.tutoring.view.components.NavigationHelper.goToHomeOrDashboard(stage);
        });

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final Button loginButton = new Button("Accedi");
        loginButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        loginButton.setTextFill(PRIMARY_RED);
        loginButton.setPadding(new Insets(12, 10, 12, 10));
        loginButton.getStyleClass().add("text-link");
        loginButton.setBackground(Background.EMPTY);
        loginButton.setBorder(Border.EMPTY);
        loginButton.setStyle("-fx-scale-x: 1; -fx-scale-y: 1; -fx-translate-x: 0; -fx-translate-y: 0;");
        loginButton.setOnAction(event -> openLoginPage(loginButton));
        final Button registerButton = new Button("Registrati");
        registerButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setPadding(new Insets(12, 14, 12, 14));
        registerButton.getStyleClass().add("primary-btn");
        registerButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(999), Insets.EMPTY)));
        registerButton.setBorder(Border.EMPTY);
        registerButton.setStyle("-fx-scale-x: 1; -fx-scale-y: 1; -fx-translate-x: 0; -fx-translate-y: 0;");
        registerButton.setOnAction(event -> openRegistrationPage(registerButton));
        topBar.getChildren().addAll(brandBlock, spacer, loginButton, registerButton);

        final HBox heroBody = new HBox(34);
        heroBody.setAlignment(Pos.CENTER_LEFT);
        heroBody.setPadding(new Insets(22, 40, 28, 40));

        final VBox heroContent = new VBox(heroBody);
        heroContent.getStyleClass().add("hero-panel");
        heroContent.setBackground(new Background(new BackgroundFill(PRIMARY_RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final VBox heroText = new VBox(14);
        heroText.setAlignment(Pos.TOP_LEFT);

        final Label title = new Label("Trova il tutor perfetto per il tuo percorso universitario");
        title.setWrapText(true);
        title.setMinHeight(Region.USE_PREF_SIZE);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 42));

        final Label subtitle = new Label(
            "UniBo Tutoring mette in contatto studenti che offrono e cercano supporto nelle materie universitarie."
                + " \nCondividi le tue conoscenze o trova l'aiuto di cui hai bisogno."
        );
        subtitle.setWrapText(true);
        subtitle.setMinHeight(Region.USE_PREF_SIZE);
        subtitle.setTextFill(Color.gray(0.95));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));

        heroText.getChildren().addAll(title, subtitle);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        final VBox imageCard = new VBox();
        imageCard.setAlignment(Pos.CENTER);
        final ImageView imageView = new AppIcon("comp.png", 392, 228);

        final Rectangle roundedClip = new Rectangle(392, 228);
        roundedClip.setArcWidth(24);
        roundedClip.setArcHeight(24);
        imageView.setClip(roundedClip);

        final VBox imageInner = new VBox(8, imageView);
        imageInner.setAlignment(Pos.CENTER);
        imageInner.setPadding(new Insets(10));
        imageCard.getChildren().add(imageInner);

        heroBody.getChildren().addAll(heroText, imageCard);
        section.getChildren().addAll(topBar, heroContent);
        return section;
    }

    private VBox createHowItWorksSection() {

    VBox section = new VBox(30);
    section.setAlignment(Pos.CENTER);
    section.setPadding(new Insets(60, 40, 60, 40));
    section.setBackground(new Background(
            new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)
    ));

    Label title = new Label("Come funziona UniBo Tutoring");
    title.setFont(Font.font("System", FontWeight.BOLD, 36));
    title.setTextFill(Color.web("#111111"));

    Label subtitle = new Label(
            "Una piattaforma semplice e intuitiva per connettere studenti\n" +
            "dell'Università di Bologna presso la sede di Cesena"
    );
    subtitle.setFont(Font.font("System", 16));
    subtitle.setTextFill(Color.web("#6B6B6B"));
    subtitle.setAlignment(Pos.CENTER);
    subtitle.setTextAlignment(TextAlignment.CENTER);

    HBox cards = new HBox(70,
            infoCard("src/icons/book.png",
                    "Offri Tutoraggio",
                    "Crea annunci per le materie che conosci meglio e aiuta altri studenti"),

            infoCard("src/icons/people.png",
                    "Trova un Tutor",
                    "Cerca tra le offerte disponibili e filtra per materia, anno o corso"),

            infoCard("src/icons/calendar.png",
                    "Gestisci Sessioni",
                    "Organizza le tue sessioni di tutoraggio con stati chiari: proposta, confermata, conclusa"),

            infoCard("src/icons/coccarda.png",
                    "Guadagna Crediti",
                    "Raccogli crediti per ogni sessione completata e consulta le tue statistiche")
    );

    cards.setAlignment(Pos.CENTER);
        // Sezione esclusiva per il campus di Cesena
        final VBox exclusiveSection = new VBox(20);
        exclusiveSection.setAlignment(Pos.CENTER);
        exclusiveSection.setPadding(new Insets(40, 40, 40, 40));
        exclusiveSection.setBackground(new Background(new BackgroundFill(LIGHT_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        final Label exclTitle = new Label("Esclusivamente per il Campus di Cesena");
        exclTitle.setFont(Font.font("System", FontWeight.BOLD, 30));
        exclTitle.setTextFill(Color.web("#111111"));

        final Label exclSubtitle = new Label("UniBo Tutoring è una piattaforma dedicata agli studenti dell'Università di Bologna presso la sede di Cesena.");
        exclSubtitle.setFont(Font.font("System", 15));
        exclSubtitle.setTextFill(Color.web("#6B6B6B"));
        exclSubtitle.setWrapText(true);
        exclSubtitle.setTextAlignment(TextAlignment.CENTER);
        exclSubtitle.setMinHeight(Region.USE_PREF_SIZE);

     final HBox exclCards = new HBox(30,
    infoCardWithBorder(
        "src/icons/campus.png",
        "Campus di Cesena",
        "Il servizio è attivo esclusivamente per gli studenti frequentanti il campus di Cesena dell'Università di Bologna. Non è disponibile per altri dipartimenti o sedi."
    ),

    infoCardWithBorder(
        "src/icons/matricola.png",
        "Matricola universitaria",
        "Per registrarti devi essere uno studente attivo e possedere un codice matricola UniBo valido. La matricola viene verificata in fase di registrazione per garantire l'accesso solo agli aventi diritto."
    ),

    infoCardWithBorder(
        "src/icons/verified.png",
        "Ambiente sicuro e verificato",
        "Grazie alla verifica tramite matricola, tutti gli utenti della piattaforma sono studenti reali dell'ateneo. Nessun accesso anonimo o esterno è consentito."
    )
);

exclCards.setAlignment(Pos.CENTER);
exclCards.setPadding(new Insets(10, 0, 0, 0));

        exclusiveSection.getChildren().addAll(exclTitle, exclSubtitle, exclCards);

        section.getChildren().addAll(title, subtitle, cards, exclusiveSection, createAppFlow());
        return section;
}

    private VBox createAppFlow() {
        final VBox section = new VBox(18);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(60, 120, 62, 120));
        section.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        final Label title = new Label("Come funziona una sessione");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#111111"));

        final Label subtitle = new Label("Dal primo annuncio alla recensione finale.");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#64748B"));

        final HBox steps = new HBox(52,
            appFlowStep(1, "Pubblica l'annuncio",
                "Offri o richiedi tutoraggio specificando materia e disponibilità."),
            appFlowStep(2, "Entrambi accettano",
                "Tutor e studente devono confermare. Finchè non accettano entrambi, la sessione resta in stato proposta."),
            appFlowStep(3, "Sessione confermata",
                "La sessione si svolge alla data concordata e viene poi segnata come conclusa."),
            appFlowStep(4, "Lascia una recensione",
                "A sessione chiusa, lo studente può recensire il tutor.")
        );
        steps.setAlignment(Pos.TOP_CENTER);
        steps.setPadding(new Insets(24, 0, 0, 0));

        section.getChildren().addAll(title, subtitle, steps);
        return section;
    }

    private VBox appFlowStep(final int number, final String title, final String description) {
        final Label numberLabel = new Label(String.valueOf(number));
        numberLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        numberLabel.setTextFill(Color.WHITE);
        numberLabel.setAlignment(Pos.CENTER);

        final StackPane numberCircle = new StackPane(numberLabel);
        numberCircle.setPrefSize(38, 38);
        numberCircle.setMinSize(38, 38);
        numberCircle.setMaxSize(38, 38);
        numberCircle.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(999), Insets.EMPTY)));

        final Label heading = new Label(title);
        heading.setFont(Font.font("System", FontWeight.BOLD, 15));
        heading.setTextFill(Color.web("#111111"));
        heading.setWrapText(true);
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setMinHeight(Region.USE_PREF_SIZE);

        final Label body = new Label(description);
        body.setFont(Font.font("System", FontWeight.NORMAL, 13));
        body.setTextFill(Color.web("#64748B"));
        body.setWrapText(true);
        body.setTextAlignment(TextAlignment.CENTER);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setMinHeight(Region.USE_PREF_SIZE);

        final VBox step = new VBox(14, numberCircle, heading, body);
        step.setAlignment(Pos.TOP_CENTER);
        step.setPrefWidth(160);
        step.setMaxWidth(160);
        return step;
    }


    private HBox createWhySection() {
        final HBox section = new HBox(24);
        section.setPadding(new Insets(10, 40, 36, 40));

        final AppCard left = new AppCard(
                14, new Insets(18), 12, Color.web("#E0E0E0"));
        HBox.setHgrow(left, Priority.ALWAYS);

        final Label leftTitle = new Label("Perché scegliere UniBo Tutoring?");
        leftTitle.setTextFill(Color.web("#111111"));
        leftTitle.setFont(Font.font("System", FontWeight.BOLD, 30));

        left.getChildren().addAll(
            leftTitle,
            bullet("Sicuro e affidabile", "Accesso riservato agli studenti UniBo con matricola universitaria presso la sede di Cesena"),
            bullet("Recensioni e feedback", "Sistema di valutazioni per garantire la qualità del tutoraggio"),
            bullet("Gestione semplificata", "Dashboard intuitiva per tenere traccia di tutte le tue attività"),
            bullet("Statistiche dettagliate", "Monitora le tue ore di tutoraggio, crediti e recensioni ricevute")
        );

        final AppCard cta = new AppCard(
                12, new Insets(22), 12, Color.web("#E0E0E0"));
        cta.setAlignment(Pos.CENTER);
        cta.setPrefWidth(360);

        final ImageView ctaIcon = new AppIcon("whitebook.png", 28, 28);

        final StackPane ctaIconCircle = new StackPane(ctaIcon);
        ctaIconCircle.setPrefSize(58, 58);
        ctaIconCircle.setMinSize(58, 58);
        ctaIconCircle.setMaxSize(58, 58);
        ctaIconCircle.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(999), Insets.EMPTY)));


        final Label ctaTitle = new Label("Pronto a iniziare?");
        ctaTitle.setTextFill(Color.web("#111111"));
        ctaTitle.setFont(Font.font("System", FontWeight.BOLD, 32));

        final Label ctaSubtitle = new Label("Registrati ora con la tua matricola universitaria");
        ctaSubtitle.setTextFill(Color.web("#525252"));
        ctaSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 15));
        ctaSubtitle.setTextAlignment(TextAlignment.CENTER);
        ctaSubtitle.setWrapText(true);
        ctaSubtitle.setMinHeight(Region.USE_PREF_SIZE);

        final AppButton register = AppButton.primary("Crea il tuo account");
        register.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 18));
        register.setPadding(new Insets(10, 18, 10, 18));
        register.setMaxWidth(Double.MAX_VALUE);
        register.setOnAction(event -> openRegistrationPage(register));

        final Label loginPrefix = new Label("Hai già un account?");
        loginPrefix.setTextFill(Color.web("#434343"));
        loginPrefix.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        final Button loginLink = new Button("Accedi");
        loginLink.setOnAction(event -> openLoginPage(loginLink));
        loginLink.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
        loginLink.setTextFill(PRIMARY_RED);
        loginLink.setBackground(Background.EMPTY);
        loginLink.setBorder(Border.EMPTY);
        loginLink.setPadding(new Insets(0));

        final HBox loginLine = new HBox(4, loginPrefix, loginLink);
        loginLine.setAlignment(Pos.CENTER);

        cta.getChildren().addAll(ctaIconCircle, ctaTitle, ctaSubtitle, register, loginLine);
        section.getChildren().addAll(left, cta);
        return section;
    }

    private void openLoginPage(final Button sourceButton) {
        final Stage stage = (Stage) sourceButton.getScene().getWindow();
        it.unibo.tutoring.view.components.NavigationHelper.goToLogin(stage);
    }

    private void openRegistrationPage(final Button sourceButton) {
        final Stage stage = (Stage) sourceButton.getScene().getWindow();
        it.unibo.tutoring.view.components.NavigationHelper.goToRegistration(stage);
    }

    private VBox infoCard(final String iconPath, final String title, final String description) {
        final VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);
        card.setPadding(new Insets(16, 12, 16, 12));

        final ImageView icon = new AppIcon(iconPath, 28, 28);

        final StackPane iconBox = new StackPane(icon);
        iconBox.setPrefSize(56, 56);
        iconBox.setMinSize(56, 56);
        iconBox.setMaxSize(56, 56);
        iconBox.setBackground(new Background(new BackgroundFill(Color.web("#E0E0E0"), new CornerRadii(10), Insets.EMPTY)));

        final Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardTitle.setTextFill(Color.web("#111111"));

        final Label cardDescription = new Label(description);
        cardDescription.setWrapText(true);
        cardDescription.setMinHeight(Region.USE_PREF_SIZE);
        cardDescription.setTextAlignment(TextAlignment.CENTER);
        cardDescription.setFont(Font.font("System", FontWeight.NORMAL, 13));
        cardDescription.setTextFill(Color.web("#525252"));

        card.getChildren().addAll(iconBox, cardTitle, cardDescription);
        return card;
    }

    private static VBox infoCardWithBorder(
        final String iconPath,
        final String title,
        final String description) {

        final ImageView icon = new AppIcon(iconPath, 22, 22);

    final StackPane iconContainer = new StackPane(icon);
    iconContainer.setPrefSize(52, 52);
    iconContainer.setMinSize(52, 52);
    iconContainer.setMaxSize(52, 52);

    iconContainer.setBackground(new Background(
            new BackgroundFill(
                    Color.web("#FCECEF"),
                    new CornerRadii(16),
                    Insets.EMPTY)));

    final Label cardTitle = new Label(title);
    cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
    cardTitle.setTextFill(Color.web("#D91E43"));

    final HBox header = new HBox(16, iconContainer, cardTitle);
    header.setAlignment(Pos.CENTER_LEFT);

    final Label body = new Label(description);
    body.setWrapText(true);
    body.setFont(Font.font("System", 15));
    body.setTextFill(Color.web("#374151"));
    body.setLineSpacing(5);

    final VBox card = new VBox(20, header, body);
    card.setAlignment(Pos.TOP_LEFT);

    card.setPrefWidth(450);
    card.setMaxWidth(450);
    card.setMinHeight(250);

    card.setPadding(new Insets(30));

    card.setBackground(new Background(
            new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(20),
                    Insets.EMPTY)));

    card.setBorder(new Border(
            new BorderStroke(
                    Color.web("#E5E7EB"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(20),
                    new BorderWidths(1))));

    card.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0.20, 0, 3);");

    return card;
}
    private VBox bullet(final String title, final String detail) {
        final VBox box = new VBox(2);
        final Label titleLabel = new Label("• " + title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#202020"));

        final Label detailLabel = new Label(detail);
        detailLabel.setWrapText(true);
        detailLabel.setMinHeight(Region.USE_PREF_SIZE);
        detailLabel.setTextFill(Color.web("#5E5E5E"));
        detailLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        box.getChildren().addAll(titleLabel, detailLabel);
        return box;
    }

    private Button primaryWhiteButton(final String text) {
        final Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 13));
        button.setTextFill(PRIMARY_RED);
        button.setPadding(new Insets(8, 16, 8, 16));
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        return button;
    }

    private Button secondaryButton(final String text) {
        final Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 13));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(8, 16, 8, 16));
        button.setStyle("-fx-background-color: #FF536A;");
        button.setBorder(Border.EMPTY);
        return button;
    }

    public static void run(final String[] args) {
        launch(args);
    }
}
