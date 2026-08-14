package it.unibo.tutoring;

import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import it.unibo.tutoring.controller.profile.ProfileController;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.credit.CreditRecord;
import it.unibo.tutoring.model.credit.ReviewRepository;
import it.unibo.tutoring.model.credit.ReviewRepository.Review;
import it.unibo.tutoring.model.user.UserRepository;
import it.unibo.tutoring.model.session.SessionRepository;
import it.unibo.tutoring.model.session.TutoringSession;
import it.unibo.tutoring.view.components.NavigationHelper;
import it.unibo.tutoring.view.components.AppFooter;
import it.unibo.tutoring.view.components.AppHeader;
import it.unibo.tutoring.view.components.AppCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;


public final class UniBoTutoringProfileApp  {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");
    private static final DateTimeFormatter SESSION_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.ITALIAN);

    private UniBoTutoringProfileApp() {
    }

    public static Scene createScene() {
        final UserAccount currentUser = CurrentSession.getUser();
        return buildScene(currentUser != null ? currentUser.getMatricola() : null, currentUser, null);
    }

    /**
     * Mostra il profilo di un altro utente (es. cliccando sul nome
     * dell'autore di un annuncio). Nasconde la matricola e "I Tuoi Prossimi
     * Impegni", visibili solo sul proprio profilo.
     */
    public static Scene createScene(final String matricolaDaVedere) {
        final UserAccount currentUser = CurrentSession.getUser();
        return buildScene(matricolaDaVedere, currentUser, null);
    }

    /**
     * Come {@link #createScene(String)}, ma ricordando anche da quale
     * annuncio si e' arrivati al profilo: mostra un pulsante "← Annuncio"
     * nell'intestazione, che riporta esattamente ai dettagli dell'annuncio di
     * partenza invece di scaricare l'utente sempre in dashboard.
     */
    public static Scene createScene(final String matricolaDaVedere, final BoxTutoraggio annuncioDiProvenienza) {
        final UserAccount currentUser = CurrentSession.getUser();
        return buildScene(matricolaDaVedere, currentUser, annuncioDiProvenienza);
    }

    private static Scene buildScene(
            final String matricolaDaVedere,
            final UserAccount currentUser,
            final BoxTutoraggio annuncioDiProvenienza) {

        final ProfileController controller = new ProfileController(new UserRepository());
        final boolean isOwnProfile = matricolaDaVedere == null
            || (currentUser != null && matricolaDaVedere.equals(currentUser.getMatricola()));

        final UserAccount viewedUser = isOwnProfile
            ? controller.getCurrentUser()
            : it.unibo.tutoring.AuthService.getInstance().getUser(matricolaDaVedere);

        final VBox root = new VBox();
        root.setBackground(new Background(
            new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        final VBox scrollContent = new VBox();
        final VBox mainContent = viewedUser != null
            ? createContent(viewedUser, controller, isOwnProfile)
            : createNotFoundContent();
        scrollContent.getChildren().addAll(
            mainContent,
            new AppFooter()
        );
        scrollContent.setMinHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        final ScrollPane scrollPane = new ScrollPane(scrollContent);
        it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(scrollPane);

        root.getChildren().addAll(
            createHeader(currentUser, annuncioDiProvenienza),
            scrollPane
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final Scene scene = new Scene(root);
        scene.getStylesheets().add(UniBoTutoringProfileApp.class.getResource("/styles.css").toExternalForm());
        return scene;
    }

    private static VBox createNotFoundContent() {
        final VBox content = new VBox(12);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        final Label label = new Label("Profilo non trovato.");
        label.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
        label.setTextFill(TEXT_DARK);
        content.getChildren().add(label);
        return content;
    }

    private static HBox createHeader(final UserAccount user, final BoxTutoraggio annuncioDiProvenienza) {

        final HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 18, 10, 18));
        header.setPrefHeight(64);

        header.setBackground(new Background(
            new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        header.setBorder(new Border(
            new BorderStroke(
                Color.web("#D6D6D6"),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(0, 0, 1, 0)
            )
        ));

        final ImageView logo = icon("logo.png", 30, 30);

        final Label title = new Label("UniBo Tutoring");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 31));
        title.setTextFill(TEXT_DARK);

        final Label subtitle = new Label("Università di Bologna");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#535353"));

        final VBox brand = new VBox(1, title, subtitle);

        final HBox brandBlock = new HBox(8, logo, brand);
        brandBlock.setAlignment(Pos.CENTER_LEFT);
        brandBlock.setCursor(Cursor.HAND);
        brandBlock.setOnMouseClicked(event -> {
            final Stage stage = (Stage) brandBlock.getScene().getWindow();
            NavigationHelper.goToHomeOrDashboard(stage);
        });

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final Label userName = new Label(
            user.getName() + " " + user.getSurname()
        );

        userName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userName.setTextFill(TEXT_DARK);

        final Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setPrefHeight(16);

        // Pulsante "← Annuncio": presente solo quando si e' arrivati al
        // profilo cliccando sul nome dell'autore/candidato di un annuncio.
        // Riporta esattamente a quell'annuncio invece di forzare il ritorno
        // in dashboard, cosi' come "← Dashboard" fa gia' per la bacheca.
        final Button backToAnnouncementButton;
        if (annuncioDiProvenienza != null) {
            final Button annuncioButton = new Button("← Annuncio");
            annuncioButton.getStyleClass().add("text-link");
            annuncioButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
            annuncioButton.setTextFill(PRIMARY_RED);
            annuncioButton.setPadding(new Insets(8, 14, 8, 14));
            annuncioButton.setCursor(javafx.scene.Cursor.HAND);
            annuncioButton.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
            ));
            annuncioButton.setBorder(new Border(
                new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)
            ));

            final BoxTutoraggio box = annuncioDiProvenienza;
            annuncioButton.setOnAction(event -> {
                final Stage stage = (Stage) annuncioButton.getScene().getWindow();
                stage.setScene(it.unibo.tutoring.view.box.AnnouncementDetailViewApp.createScene(stage, box));
                stage.setTitle("UniBo Tutoring - Dettaglio Annuncio");
                it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
            });
            backToAnnouncementButton = annuncioButton;
        } else {
            backToAnnouncementButton = null;
        }

        final Button dashboardButton = new Button("← Dashboard");
        dashboardButton.getStyleClass().add("text-link");
        dashboardButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        dashboardButton.setTextFill(TEXT_DARK);
        dashboardButton.setPadding(new Insets(8, 14, 8, 14));
        dashboardButton.setCursor(javafx.scene.Cursor.HAND);
        dashboardButton.setBackground(new Background(
            new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        dashboardButton.setBorder(new Border(
            new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)
        ));

        dashboardButton.setOnAction(event -> {
            final Stage stage =
                (Stage) dashboardButton.getScene().getWindow();

            stage.setScene(UniBoTutoringDashboardApp.createScene());
            stage.setTitle("UniBo Tutoring - Dashboard");
            it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
        });

        final HBox rightSide = new HBox(10);
        rightSide.setAlignment(Pos.CENTER_RIGHT);
        rightSide.getChildren().add(userName);
        if (backToAnnouncementButton != null) {
            final Separator announcementSeparator = new Separator();
            announcementSeparator.setOrientation(javafx.geometry.Orientation.VERTICAL);
            announcementSeparator.setPrefHeight(16);
            rightSide.getChildren().addAll(announcementSeparator, backToAnnouncementButton);
        }
        rightSide.getChildren().addAll(separator, dashboardButton);

        header.getChildren().addAll(
            brandBlock,
            spacer,
            rightSide
        );

        return header;
    }

    private static VBox createContent(final UserAccount user, final ProfileController controller, final boolean isOwnProfile) {

        final VBox content = new VBox(20);
        final CreditRecord creditRecord = controller.getCreditRecord(user.getMatricola());

    final Color badgeColor;

switch (creditRecord.getBadge()) {

    case EXPERT:
        badgeColor = Color.web("#D4AF37");
        break;

    case INTERMEDIATE:
        badgeColor = Color.web("#3B82F6");
        break;

    default:
        badgeColor = PRIMARY_RED;
}

        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        if (!isOwnProfile) {
            final Label viewingBanner = new Label("Stai visualizzando il profilo di " + user.getName() + " " + user.getSurname());
            viewingBanner.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
            viewingBanner.setTextFill(Color.WHITE);
            viewingBanner.setPadding(new Insets(10, 16, 10, 16));
            viewingBanner.setMaxWidth(Double.MAX_VALUE);
            viewingBanner.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(8), Insets.EMPTY)));
            content.getChildren().add(viewingBanner);
        }

        final HBox heroSection = new HBox(20);

heroSection.setAlignment(Pos.CENTER_LEFT);

heroSection.setPadding(new Insets(24));

heroSection.setBackground(
    new Background(
        new BackgroundFill(
            Color.WHITE,
            new CornerRadii(16),
            Insets.EMPTY
        )
    )
);

heroSection.setBorder(
    new Border(
        new BorderStroke(
            Color.web("#DADADA"),
            BorderStrokeStyle.SOLID,
            new CornerRadii(16),
            BorderWidths.DEFAULT
        )
    )
);

final Label avatar = new Label(
    user.getName().substring(0, 1)
);

avatar.setMinSize(82, 82);
avatar.setMaxSize(82, 82);

avatar.setAlignment(Pos.CENTER);

avatar.setFont(
    Font.font("System", FontWeight.EXTRA_BOLD, 34)
);

avatar.setTextFill(Color.WHITE);

avatar.setBackground(
    new Background(
        new BackgroundFill(
            PRIMARY_RED,
            new CornerRadii(999),
            Insets.EMPTY
        )
    )
);

final Label heroName = new Label(
    user.getName() + " " + user.getSurname()
);

heroName.setFont(
    Font.font("System", FontWeight.EXTRA_BOLD, 34)
);

heroName.setTextFill(TEXT_DARK);

final Label heroSubtitle = new Label(
    "Studente UniBo • Informatica"
);

heroSubtitle.setFont(
    Font.font("System", FontWeight.NORMAL, 17)
);

heroSubtitle.setTextFill(TEXT_MEDIUM);

final Label heroBadge = new Label(
    creditRecord.getBadge().getDisplayName()
        + " Tutor"
);

heroBadge.setPadding(
    new Insets(6, 14, 6, 14)
);

heroBadge.setFont(
    Font.font("System", FontWeight.EXTRA_BOLD, 13)
);

heroBadge.setTextFill(Color.WHITE);

heroBadge.setBackground(
    new Background(
        new BackgroundFill(
            badgeColor,
            new CornerRadii(999),
            Insets.EMPTY
        )
    )
);

final VBox heroTexts = new VBox(8);

heroTexts.getChildren().addAll(
    heroName,
    heroSubtitle,
    heroBadge
);

heroSection.getChildren().addAll(
    avatar,
    heroTexts
);

        final VBox card = new VBox(16);

        card.setPadding(new Insets(24));

        card.setMaxWidth(500);

        card.setBackground(new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(12),
                Insets.EMPTY
            )
        ));

        card.setBorder(new Border(
            new BorderStroke(
                Color.web("#D6D6D6"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                BorderWidths.DEFAULT
            )
        ));

        final Label nameLabel = createInfoLabel(
            "Nome: " + user.getName()
        );

        final Label surnameLabel = createInfoLabel(
            "Cognome: " + user.getSurname()
        );

        final Label matricolaLabel = createInfoLabel(
            "Matricola: " + user.getMatricola()
        );

        final Label emailLabel = createInfoLabel(
            "Email: " + user.getEmail()
        );

        card.getChildren().addAll(
            nameLabel,
            surnameLabel
        );
        if (isOwnProfile) {
            card.getChildren().add(matricolaLabel);
        }
        card.getChildren().add(emailLabel);

        final VBox bioCard = createBioCard(user, isOwnProfile);

        final VBox creditCard = new VBox(14);

creditCard.setPadding(new Insets(24));

creditCard.setMaxWidth(500);

creditCard.setBackground(new Background(
    new BackgroundFill(
        Color.WHITE,
        new CornerRadii(12),
        Insets.EMPTY
    )
));

creditCard.setBorder(new Border(
    new BorderStroke(
        Color.web("#D6D6D6"),
        BorderStrokeStyle.SOLID,
        new CornerRadii(12),
        BorderWidths.DEFAULT
    )
));

final Label creditTitle = new Label("Progressione Tutor");

creditTitle.setFont(
    Font.font("System", FontWeight.EXTRA_BOLD, 24)
);

creditTitle.setTextFill(TEXT_DARK);

final Label totalHoursLabel = createInfoLabel(
    "Ore completate: "
        + creditRecord.getTotalHours()
);

final Label totalCreditsLabel = createInfoLabel(
    "CFU ottenuti: "
        + creditRecord.getTotalCredits()
);
final int nextLevelHours = creditRecord.getNextLevelHours();
final int currentHours = creditRecord.getTotalHours();
final int remainingHours = Math.max(0, nextLevelHours - currentHours);
final double progress = nextLevelHours == 0 ? 1.0 : (double) currentHours / nextLevelHours;

final Label badgeLabel = new Label(
    creditRecord.getBadge().getDisplayName()
);

badgeLabel.setFont(
    Font.font("System", FontWeight.EXTRA_BOLD, 18)
);

badgeLabel.setPadding(
    new Insets(6, 14, 6, 14)
);

badgeLabel.setTextFill(Color.WHITE);

badgeLabel.setBackground(
    new Background(
        new BackgroundFill(
            badgeColor,
            new CornerRadii(999),
            Insets.EMPTY
        )
    )
);
final Label badgeTitle = createInfoLabel(
    "Badge Tutor:"
);
final VBox progressContainer = new VBox(8);

final Label progressLabel = new Label(
    currentHours + " / " + nextLevelHours + " ore"
);

progressLabel.setFont(
    Font.font("System", FontWeight.SEMI_BOLD, 14)
);

progressLabel.setTextFill(TEXT_MEDIUM);

final Region progressBarBg = new Region();

progressBarBg.setPrefSize(260, 14);

progressBarBg.setBackground(
    new Background(
        new BackgroundFill(
            Color.web("#E2E2E2"),
            new CornerRadii(999),
            Insets.EMPTY
        )
    )
);

final Region progressBarFill = new Region();

progressBarFill.setPrefSize(
    260 * progress,
    14
);

progressBarFill.setBackground(
    new Background(
        new BackgroundFill(
            PRIMARY_RED,
            new CornerRadii(999),
            Insets.EMPTY
        )
    )
);

final StackPane progressBar = new StackPane(
    progressBarBg,
    progressBarFill
);

progressBar.setAlignment(Pos.CENTER_LEFT);

final Label remainingLabel = new Label(
    remainingHours
        + " ore al prossimo livello"
);

remainingLabel.setFont(
    Font.font("System", FontWeight.NORMAL, 13)
);

remainingLabel.setTextFill(TEXT_MEDIUM);

progressContainer.getChildren().addAll(
    progressLabel,
    progressBar,
    remainingLabel
);

creditCard.getChildren().addAll(
    creditTitle,
    totalHoursLabel,
    totalCreditsLabel,
    badgeTitle,
    badgeLabel,
    progressContainer
);

        final VBox leftColumn = new VBox(20);
        final HBox statsRow = new HBox(16);

final List<Review> reviews = ReviewRepository.loadReviewsForRecipient(user.getMatricola());
final double avgRating = reviews.stream().mapToInt(Review::stars).average().orElse(0.0);

final VBox hoursCard = createStatCard(
    String.valueOf(
        creditRecord.getTotalHours()
    ),
    "Ore completate"
);

final VBox creditsCard = createStatCard(
    String.valueOf(
        creditRecord.getTotalCredits()
    ),
    "CFU ottenuti"
);
final VBox badgeCard = createStatCard(
    creditRecord.getBadge().getDisplayName(),
    "Badge"
);

final VBox ratingCard = createStatCard(
    reviews.isEmpty() ? "N/D" : String.format(Locale.ITALIAN, "%.1f★", avgRating),
    reviews.isEmpty() ? "Rating" : "Rating (" + reviews.size() + ")"
);

statsRow.getChildren().addAll(
    hoursCard,
    creditsCard,
    badgeCard,
    ratingCard
);
leftColumn.getChildren().addAll(
    statsRow,
    card,
    bioCard
);
// "Recensioni ricevute" compare solo sul profilo di un altro utente: sul
// proprio profilo restano visibili solo nella pagina Statistiche, per non
// mostrarle due volte nello stesso posto.
if (!isOwnProfile) {
    leftColumn.getChildren().add(createReviewsCard(user.getMatricola()));
}

final VBox rightColumn = new VBox(20);
rightColumn.getChildren().addAll(
    creditCard
);

        final javafx.scene.layout.FlowPane columns = new javafx.scene.layout.FlowPane();
        columns.setHgap(24);
        columns.setVgap(24);
        columns.setAlignment(Pos.TOP_LEFT);

        columns.getChildren().addAll(
                leftColumn,
                rightColumn
        );
        if (isOwnProfile) {
            final VBox calendarColumn = new VBox(20);
            calendarColumn.getChildren().add(createCalendarCard());
            columns.getChildren().add(calendarColumn);
        }

        content.getChildren().addAll(
                heroSection,
                columns
        );

        return content;
    }

    /**
     * Card "Presentazione": testo libero mostrato sul profilo di chiunque,
     * ma modificabile solo dal proprietario del profilo (isOwnProfile).
     */
    private static VBox createBioCard(final UserAccount user, final boolean isOwnProfile) {

        final VBox bioCard = new VBox(10);
        bioCard.setPadding(new Insets(24));
        bioCard.setMaxWidth(500);
        bioCard.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        bioCard.setBorder(new Border(
            new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(12), BorderWidths.DEFAULT)
        ));

        final Label bioTitle = new Label("Presentazione");
        bioTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        bioTitle.setTextFill(TEXT_DARK);

        final VBox bioBody = new VBox(10);

        final Runnable[] showDisplay = new Runnable[1];
        final Runnable[] showEdit = new Runnable[1];

        showDisplay[0] = () -> {
            bioBody.getChildren().clear();

            final String testo = user.getPresentazione();
            final boolean vuota = testo == null || testo.isBlank();

            final javafx.scene.text.Text bioText = new javafx.scene.text.Text(vuota
                ? (isOwnProfile ? "Non hai ancora scritto una presentazione." : "Nessuna presentazione disponibile.")
                : testo);
            bioText.setFont(Font.font("System", FontWeight.NORMAL, 14));
            bioText.setFill(vuota ? TEXT_MEDIUM : TEXT_DARK);
            // Larghezza di wrapping esplicita (card larga 500px - 24px di
            // padding per lato): garantisce che il testo vada sempre a capo
            // e non venga MAI troncato con i puntini, per quanto sia lungo.
            bioText.setWrappingWidth(452);

            bioBody.getChildren().add(bioText);

            if (isOwnProfile) {
                final Button editButton = new Button(vuota ? "✎ Aggiungi presentazione" : "✎ Modifica");
                editButton.getStyleClass().add("text-link");
                editButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                editButton.setTextFill(PRIMARY_RED);
                editButton.setBackground(Background.EMPTY);
                editButton.setBorder(Border.EMPTY);
                editButton.setPadding(new Insets(2, 0, 0, 0));
                editButton.setCursor(Cursor.HAND);
                editButton.setOnAction(e -> showEdit[0].run());
                bioBody.getChildren().add(editButton);
            }
        };

        showEdit[0] = () -> {
            bioBody.getChildren().clear();

            final TextArea textArea = new TextArea(user.getPresentazione());
            textArea.setWrapText(true);
            textArea.setPrefRowCount(4);
            textArea.setPromptText(
                "Scrivi qualcosa su di te, es: \"Sono uno studente di 24 anni di Cesena, "
                + "mi sto laureando in Ingegneria e Scienze Informatiche...\""
            );

            final Button saveButton = new Button("Salva");
            saveButton.setFont(Font.font("System", FontWeight.BOLD, 13));
            saveButton.setTextFill(Color.WHITE);
            saveButton.setPadding(new Insets(7, 16, 7, 16));
            saveButton.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(6), Insets.EMPTY)));
            saveButton.setBorder(Border.EMPTY);
            saveButton.setCursor(Cursor.HAND);
            saveButton.setOnAction(e -> {
                final String nuovoTesto = textArea.getText() == null ? "" : textArea.getText().trim();
                it.unibo.tutoring.AuthService.getInstance().updatePresentazione(user.getMatricola(), nuovoTesto);
                showDisplay[0].run();
            });

            final Button cancelButton = new Button("Annulla");
            cancelButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
            cancelButton.setTextFill(TEXT_DARK);
            cancelButton.setPadding(new Insets(7, 16, 7, 16));
            cancelButton.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(6), Insets.EMPTY)));
            cancelButton.setBorder(new Border(
                new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)
            ));
            cancelButton.setCursor(Cursor.HAND);
            cancelButton.setOnAction(e -> showDisplay[0].run());

            final HBox actions = new HBox(8, saveButton, cancelButton);
            bioBody.getChildren().addAll(textArea, actions);
        };

        showDisplay[0].run();

        bioCard.getChildren().addAll(bioTitle, bioBody);
        return bioCard;
    }

    /**
     * Card "Recensioni ricevute": mostra a chiunque visiti il profilo (sia il
     * proprietario sia altri utenti) l'elenco delle recensioni ricevute dalla
     * persona in questione, cosi' che anche chi guarda il profilo di un altro
     * utente possa vederle (prima erano visibili solo nella pagina
     * Statistiche, accessibile pero' solo per il proprio account).
     */
    private static VBox createReviewsCard(final String matricola) {
        final VBox reviewsCard = new VBox(14);
        reviewsCard.setPadding(new Insets(24));
        reviewsCard.setMaxWidth(500);
        reviewsCard.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        reviewsCard.setBorder(new Border(
            new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(12), BorderWidths.DEFAULT)
        ));

        final Label title = new Label("Recensioni ricevute");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        title.setTextFill(TEXT_DARK);
        reviewsCard.getChildren().add(title);

        final List<Review> reviews = ReviewRepository.loadReviewsForRecipient(matricola);
        if (reviews.isEmpty()) {
            final Label none = new Label("Ancora nessuna recensione ricevuta.");
            none.setFont(Font.font("System", FontWeight.NORMAL, 13));
            none.setTextFill(TEXT_MEDIUM);
            reviewsCard.getChildren().add(none);
            return reviewsCard;
        }

        final List<Review> sorted = new java.util.ArrayList<>(reviews);
        sorted.sort((a, b) -> parseReviewDate(b.date()).compareTo(parseReviewDate(a.date())));

        final VBox listBox = new VBox(10);
        for (final Review review : sorted) {
            listBox.getChildren().add(createReviewRow(review));
        }

        final ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefHeight(Math.min(280, 70 + sorted.size() * 70));
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        reviewsCard.getChildren().add(scroll);
        return reviewsCard;
    }

    /** Singola recensione mostrata nella card "Recensioni ricevute" del profilo. */
    private static VBox createReviewRow(final Review review) {
        final VBox row = new VBox(4);
        row.setPadding(new Insets(12));
        row.setBackground(new Background(new BackgroundFill(Color.web("#F8F9FA"), new CornerRadii(8), Insets.EMPTY)));

        final HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        final Label reviewer = new Label(review.reviewerName());
        reviewer.setFont(Font.font("System", FontWeight.BOLD, 13));
        reviewer.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final String starsString = "★".repeat(Math.max(0, Math.min(5, review.stars())))
            + "☆".repeat(Math.max(0, 5 - review.stars()));
        final Label stars = new Label(starsString);
        stars.setFont(Font.font("System", 13));
        stars.setTextFill(Color.web("#FFC107"));

        topRow.getChildren().addAll(reviewer, spacer, stars);

        final Label subjectDate = new Label(review.subject() + " • " + review.date());
        subjectDate.setFont(Font.font("System", FontWeight.NORMAL, 11));
        subjectDate.setTextFill(TEXT_MEDIUM);

        row.getChildren().addAll(topRow, subjectDate);

        if (review.comment() != null && !review.comment().isBlank()) {
            final Label comment = new Label(review.comment());
            comment.setWrapText(true);
            comment.setFont(Font.font("System", FontWeight.NORMAL, 12));
            comment.setTextFill(TEXT_DARK);
            row.getChildren().add(comment);
        }

        return row;
    }

    /**
     * Interpreta la data testuale di una recensione (salvata come "dd-MM-yyyy")
     * per poterle ordinare cronologicamente. Non e' un confronto lessicografico
     * di stringhe: "05-01-2026" e "12-12-2025" andrebbero ordinate male se
     * confrontate come semplice testo.
     */
    private static java.time.LocalDate parseReviewDate(final String raw) {
        if (raw == null || raw.isBlank()) {
            return java.time.LocalDate.MIN;
        }
        final String trimmed = raw.trim();
        final DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        };
        for (final DateTimeFormatter format : formats) {
            try {
                return java.time.LocalDate.parse(trimmed, format);
            } catch (final java.time.format.DateTimeParseException ignored) {
                // prova il formato successivo
            }
        }
        try {
            return java.time.LocalDate.parse(trimmed);
        } catch (final java.time.format.DateTimeParseException ignored) {
            return java.time.LocalDate.MIN;
        }
    }

    private static Label createInfoLabel(final String text) {

        final Label label = new Label(text);

        label.setFont(
            Font.font("System", FontWeight.SEMI_BOLD, 18)
        );

        label.setTextFill(TEXT_DARK);

        return label;
    }
private static VBox createStatCard(
    final String value,
    final String label
) {

    final VBox card = new VBox(8);

    card.setAlignment(Pos.CENTER);

    card.setPadding(new Insets(18));

    card.setPrefWidth(140);

    card.setBackground(
        new Background(
            new BackgroundFill(
                Color.WHITE,
                new CornerRadii(14),
                Insets.EMPTY
            )
        )
    );

    card.setBorder(
        new Border(
            new BorderStroke(
                Color.web("#D9D9D9"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(14),
                BorderWidths.DEFAULT
            )
        )
    );

    final Label valueLabel = new Label(value);

    valueLabel.setFont(
        Font.font("System", FontWeight.EXTRA_BOLD, 28)
    );

    valueLabel.setTextFill(PRIMARY_RED);

    final Label textLabel = new Label(label);

    textLabel.setFont(
        Font.font("System", FontWeight.SEMI_BOLD, 13)
    );

    textLabel.setTextFill(TEXT_MEDIUM);

    card.getChildren().addAll(
        valueLabel,
        textLabel
    );

    return card;
}
    private static ImageView icon(
        final String iconName,
        final double w,
        final double h
    ) {

        final Image image = new Image(
            Path.of("src", "icons", iconName)
                .toUri()
                .toString()
        );

        final ImageView view = new ImageView(image);

        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(true);
        view.setSmooth(true);

        return view;
    }

    // Calendario personale (Andrea)

    private static VBox createCalendarCard() {
        final VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setMaxWidth(500);
        card.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)
        ));
        card.setBorder(new Border(
                new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, new CornerRadii(12), BorderWidths.DEFAULT)
        ));

        final Label title = new Label("I Tuoi Prossimi Impegni");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(TEXT_DARK);
        card.getChildren().add(title);

        String miaMatricola = CurrentSession.getUser().getMatricola();
        SessionRepository repository = new SessionRepository();

        // Riceve la lista delle sessioni confermate
        List<TutoringSession> impegni = repository.getConfirmedSessionsForUser(miaMatricola);

        if (impegni.isEmpty()) {
            Label emptyMsg = new Label("Nessuna sessione programmata.");
            emptyMsg.setFont(Font.font("System", 14));
            emptyMsg.setTextFill(TEXT_MEDIUM);
            card.getChildren().add(emptyMsg);
        } else {
            for (int i = 0; i < impegni.size(); i++) {
                TutoringSession session = impegni.get(i);

                final String tutorMatricola = session.getTutorMatricola();
                final UserAccount tutor = AuthService.getInstance().getUser(tutorMatricola);
                final String persona = formatTutorLabel(miaMatricola, tutorMatricola, tutor);

                VBox agendaRow = createAgendaItem(
                        session.getMateria(),
                        session.getDataOra().format(SESSION_DATE_FORMAT)
                                + " • " + formatDuration(session.getDurata()),
                        persona
                );

                card.getChildren().add(agendaRow);

                if (i < impegni.size() - 1) {
                    card.getChildren().add(new Separator());
                }
            }
        }

        return card;
    }

    static String formatTutorLabel(
            final String miaMatricola,
            final String tutorMatricola,
            final UserAccount tutor) {
        if (tutorMatricola.equals(miaMatricola)) {
            return "Sessione da Tutor";
        }
        if (tutor != null) {
            return "Tutor: " + tutor.getName() + " " + tutor.getSurname();
        }
        return "Tutor: " + tutorMatricola;
    }

    private static String formatDuration(final Duration duration) {
        final long totalMinutes = duration.toMinutes();
        final long hours = totalMinutes / 60;
        final long minutes = totalMinutes % 60;
        if (hours == 0) {
            return minutes + " min";
        }
        if (minutes == 0) {
            return hours + (hours == 1 ? " ora" : " ore");
        }
        return hours + " h " + minutes + " min";
    }

    private static VBox createAgendaItem(final String materia, final String data, final String persona) {
        final VBox itemBox = new VBox(4);

        final Label lblMateria = new Label(materia);
        lblMateria.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblMateria.setTextFill(TEXT_DARK);

        final Label lblData = new Label(data);
        lblData.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        lblData.setTextFill(PRIMARY_RED);

        final Label lblPersona = new Label(persona);
        lblPersona.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lblPersona.setTextFill(TEXT_MEDIUM);

        itemBox.getChildren().addAll(lblMateria, lblData, lblPersona);
        return itemBox;
    }

}