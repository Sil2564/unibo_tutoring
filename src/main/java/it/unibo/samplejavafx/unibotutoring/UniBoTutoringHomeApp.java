package it.unibo.samplejavafx.unibotutoring;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private static final Color CARD_BACKGROUND = Color.WHITE;

    @Override
    public void start(final Stage stage) {
        final Scene scene = createScene();
        stage.setTitle("UniBo Tutoring - Home");
        stage.setScene(scene);
        stage.show();
    }

    public static Scene createScene() {
        final UniBoTutoringHomeApp app = new UniBoTutoringHomeApp();
        final VBox page = new VBox(
            app.createHeroSection(),
            app.createHowItWorksSection(),
            app.createWhySection(),
            app.createFooterSection()
        );
        page.setBackground(new Background(new BackgroundFill(LIGHT_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        final ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return new Scene(scrollPane, 1180, 900);
    }

    private VBox createHeroSection() {
        final VBox section = new VBox(0);
        section.setBackground(new Background(new BackgroundFill(LIGHT_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 24, 8, 24));
        topBar.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        final Image uniBoLogoImage = new Image(Path.of("src", "icons", "unibo.png").toUri().toString());
        final ImageView uniBoLogo = new ImageView(uniBoLogoImage);
        uniBoLogo.setFitWidth(34);
        uniBoLogo.setFitHeight(34);
        uniBoLogo.setPreserveRatio(true);
        uniBoLogo.setSmooth(true);

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

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final Button loginButton = new Button("Accedi");
        loginButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        loginButton.setTextFill(PRIMARY_RED);
        loginButton.setPadding(new Insets(12, 10, 12, 10));
        loginButton.setBackground(Background.EMPTY);
        loginButton.setBorder(Border.EMPTY);
        loginButton.setOnAction(event -> openLoginPage(loginButton));
        final Button registerButton = new Button("Registrati");
        registerButton.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        registerButton.setTextFill(Color.WHITE);
        registerButton.setPadding(new Insets(12, 14, 12, 14));
        registerButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(14), Insets.EMPTY)));
        registerButton.setBorder(Border.EMPTY);
        registerButton.setOnAction(event -> openRegistrationPage(registerButton));
        topBar.getChildren().addAll(brandBlock, spacer, loginButton, registerButton);

        final HBox heroBody = new HBox(34);
        heroBody.setAlignment(Pos.CENTER_LEFT);
        heroBody.setPadding(new Insets(22, 40, 28, 40));

        final VBox heroContent = new VBox(heroBody);
        heroContent.setBackground(new Background(new BackgroundFill(PRIMARY_RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final VBox heroText = new VBox(14);
        heroText.setAlignment(Pos.TOP_LEFT);

        final Label title = new Label("Trova il tutor perfetto per il tuo percorso universitario");
        title.setWrapText(true);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 42));

        final Label subtitle = new Label(
            "UniBo Tutoring mette in contatto studenti che offrono e cercano supporto "
                + "nelle materie universitarie. Condividi le tue conoscenze o trova l'aiuto di cui hai bisogno."
        );
        subtitle.setWrapText(true);
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.92));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));

        final Button startNowButton = primaryWhiteButton("Inizia Subito");
        startNowButton.setOnAction(event -> openRegistrationPage(startNowButton));
        final Button existingAccountButton = secondaryButton("Ho già un account");
        existingAccountButton.setOnAction(event -> openLoginPage(existingAccountButton));
        final HBox ctaButtons = new HBox(12, startNowButton, existingAccountButton);
        heroText.getChildren().addAll(title, subtitle, ctaButtons);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        final VBox imageCard = new VBox();
        imageCard.setAlignment(Pos.CENTER);
        final Image image = new Image(Path.of("src", "icons", "comp.png").toUri().toString());
        final ImageView imageView = new ImageView(image);
        imageView.setFitWidth(392);
        imageView.setFitHeight(228);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

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
            "dell'Università di Bologna"
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
    section.getChildren().addAll(title, subtitle, cards);
    return section;
}

    private HBox createWhySection() {
        final HBox section = new HBox(24);
        section.setPadding(new Insets(10, 40, 36, 40));

        final VBox left = new VBox(14);
        left.setPadding(new Insets(18));
        left.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, new CornerRadii(12), Insets.EMPTY)));
        left.setBorder(new Border(new BorderStroke(Color.web("#E0E0E0"), BorderStrokeStyle.SOLID, new CornerRadii(12), BorderWidths.DEFAULT)));
        HBox.setHgrow(left, Priority.ALWAYS);

        final Label leftTitle = new Label("Perché scegliere UniBo Tutoring?");
        leftTitle.setFont(Font.font("System", FontWeight.BOLD, 30));

        left.getChildren().addAll(
            leftTitle,
            bullet("Sicuro e affidabile", "Accesso riservato agli studenti UniBo con matricola universitaria"),
            bullet("Recensioni e feedback", "Sistema di valutazioni per garantire la qualità del tutoraggio"),
            bullet("Gestione semplificata", "Dashboard intuitiva per tenere traccia di tutte le tue attività"),
            bullet("Statistiche dettagliate", "Monitora le tue ore di tutoraggio, crediti e recensioni ricevute")
        );

        final VBox cta = new VBox(12);
        cta.setAlignment(Pos.CENTER);
        cta.setPadding(new Insets(22));
        cta.setPrefWidth(360);
        cta.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, new CornerRadii(12), Insets.EMPTY)));
        cta.setBorder(new Border(new BorderStroke(Color.web("#E0E0E0"), BorderStrokeStyle.SOLID, new CornerRadii(12), BorderWidths.DEFAULT)));

        final Image ctaIconImage = new Image(Path.of("src", "icons", "whitebook.png").toUri().toString());
        final ImageView ctaIcon = new ImageView(ctaIconImage);
        ctaIcon.setFitWidth(28);
        ctaIcon.setFitHeight(28);
        ctaIcon.setPreserveRatio(true);
        ctaIcon.setSmooth(true);

        final StackPane ctaIconCircle = new StackPane(ctaIcon);
        ctaIconCircle.setPrefSize(58, 58);
        ctaIconCircle.setMinSize(58, 58);
        ctaIconCircle.setMaxSize(58, 58);
        ctaIconCircle.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(999), Insets.EMPTY)));

        
        final Label ctaTitle = new Label("Pronto a iniziare?");
        ctaTitle.setFont(Font.font("System", FontWeight.BOLD, 32));

        final Label ctaSubtitle = new Label("Registrati ora con la tua matricola universitaria");
        ctaSubtitle.setTextFill(Color.web("#525252"));
        ctaSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 15));
        ctaSubtitle.setTextAlignment(TextAlignment.CENTER);
        ctaSubtitle.setWrapText(true);

        final Button register = redButton("Crea il tuo account");
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
        stage.setScene(UniBoTutoringLoginApp.createScene(stage));
        stage.setTitle("UniBo Tutoring - Login");
    }

    private void openRegistrationPage(final Button sourceButton) {
        final Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.setScene(UniBoTutoringRegistrationApp.createScene(stage));
        stage.setTitle("UniBo Tutoring - Registrazione");
    }

    private VBox createFooterSection() {
        final VBox section = new VBox(20);
        section.setPadding(new Insets(26, 40, 18, 40));
        section.setBackground(new Background(new BackgroundFill(PRIMARY_RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox cols = new HBox(50,
            footerColumn("Università di Bologna", "UniBo Tutoring è la piattaforma ufficiale per il supporto tra studenti dell'Università di Bologna.\n\nVia Zamboni, 33\n40126 Bologna, Italia"),
            footerColumn("Documenti", "Privacy Policy\nTermini e Condizioni\nCodice di Condotta"),
            footerColumn("Contatti e Assistenza", "Email di supporto:\ntutoring@unibo.it\n\nHai bisogno di aiuto?\nApri box assistenza")
        );

        final Label copyright = new Label("© 2026 Università di Bologna - UniBo Tutoring. Tutti i diritti riservati.");
        copyright.setTextFill(Color.rgb(255, 255, 255, 0.94));
        copyright.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

        section.getChildren().addAll(cols, copyright);
        return section;
    }

    private VBox infoCard(final String iconPath, final String title, final String description) {
        final VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);
        card.setPadding(new Insets(8, 6, 8, 6));

        final Image iconImage = new Image(Path.of(iconPath).toUri().toString());
        final ImageView icon = new ImageView(iconImage);
        icon.setFitWidth(28);
        icon.setFitHeight(28);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);

        final StackPane iconBox = new StackPane(icon);
        iconBox.setPrefSize(56, 56);
        iconBox.setMinSize(56, 56);
        iconBox.setMaxSize(56, 56);
        iconBox.setBackground(new Background(new BackgroundFill(Color.web("#E0E0E0"), new CornerRadii(10), Insets.EMPTY)));

        final Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        final Label cardDescription = new Label(description);
        cardDescription.setWrapText(true);
        cardDescription.setTextAlignment(TextAlignment.CENTER);
        cardDescription.setFont(Font.font("System", FontWeight.NORMAL, 13));
        cardDescription.setTextFill(Color.web("#525252"));

        card.getChildren().addAll(iconBox, cardTitle, cardDescription);
        return card;
    }

    private VBox bullet(final String title, final String detail) {
        final VBox box = new VBox(2);
        final Label titleLabel = new Label("• " + title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#202020"));

        final Label detailLabel = new Label(detail);
        detailLabel.setWrapText(true);
        detailLabel.setTextFill(Color.web("#5E5E5E"));
        detailLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        box.getChildren().addAll(titleLabel, detailLabel);
        return box;
    }

    private VBox footerColumn(final String title, final String content) {
        final VBox box = new VBox(8);
        box.setPrefWidth(320);

        final Label heading = new Label(title);
        heading.setFont(Font.font("System", FontWeight.BOLD, 22));
        heading.setTextFill(Color.WHITE);

        final Label body = new Label(content);
        body.setWrapText(true);
        body.setTextFill(Color.rgb(255, 255, 255, 0.93));
        body.setFont(Font.font("System", FontWeight.NORMAL, 13));

        box.getChildren().addAll(heading, body);
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
        button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.2), new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(Color.rgb(255, 255, 255, 0.45), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        return button;
    }

    private Button redButton(final String text) {
        final Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(10, 18, 10, 18));
        button.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(10), Insets.EMPTY)));
        return button;
    }

    public static void run(final String[] args) {
        launch(args);
    }
}
