package it.unibo.tutoring;

import java.nio.file.Path;
import java.util.List;

import it.unibo.tutoring.controller.profile.ProfileController;
import it.unibo.tutoring.model.credit.CreditRecord;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.user.UserRepository;
import it.unibo.tutoring.model.session.SessionRepository;
import it.unibo.tutoring.model.session.TutoringSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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

    private UniBoTutoringProfileApp() {
    }

    public static Scene createScene() {

        final ProfileController controller = new ProfileController(new UserRepository());
        final UserAccount user = controller.getCurrentUser();

        final VBox root = new VBox();
        root.setBackground(new Background(
            new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        final VBox scrollContent = new VBox();
        final VBox mainContent = createContent(user, controller);
        scrollContent.getChildren().addAll(
            mainContent,
            createFooterSection()
        );
        scrollContent.setMinHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        final ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setBorder(Border.EMPTY);

        root.getChildren().addAll(
            createHeader(user),
            scrollPane
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return new Scene(root, 1320, 920);
    }

    private static HBox createHeader(final UserAccount user) {

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

        final Button dashboardButton = new Button("Vai alla Dashboard");
        dashboardButton.setFont(Font.font("System", FontWeight.BOLD, 13));
        dashboardButton.setTextFill(Color.WHITE);
        dashboardButton.setPadding(new Insets(8, 16, 8, 16));
        dashboardButton.setBackground(new Background(
            new BackgroundFill(PRIMARY_RED, new CornerRadii(6), Insets.EMPTY)
        ));
        dashboardButton.setBorder(Border.EMPTY);
        dashboardButton.setOnMouseEntered(e -> dashboardButton.setBackground(new Background(
            new BackgroundFill(PRIMARY_RED.darker(), new CornerRadii(6), Insets.EMPTY)
        )));
        dashboardButton.setOnMouseExited(e -> dashboardButton.setBackground(new Background(
            new BackgroundFill(PRIMARY_RED, new CornerRadii(6), Insets.EMPTY)
        )));

        dashboardButton.setOnAction(event -> {
            final Stage stage =
                (Stage) dashboardButton.getScene().getWindow();

            stage.setScene(UniBoTutoringDashboardApp.createScene());
            stage.setTitle("UniBo Tutoring - Dashboard");
        });

        final HBox rightSide = new HBox(
            10,
            userName,
            separator,
            dashboardButton
        );

        rightSide.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(
            brandBlock,
            spacer,
            rightSide
        );

        return header;
    }

    private static VBox createContent(final UserAccount user, final ProfileController controller) {

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
            surnameLabel,
            matricolaLabel,
            emailLabel
        );

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
    "4.9★",
    "Rating"
);

statsRow.getChildren().addAll(
    hoursCard,
    creditsCard,
    badgeCard,
    ratingCard
);
leftColumn.getChildren().addAll(
    statsRow,
    card
);

final VBox rightColumn = new VBox(20);
rightColumn.getChildren().addAll(
    creditCard
);

        final VBox calendarColumn = new VBox(20);
        calendarColumn.getChildren().addAll(
                createCalendarCard()
        );

        final javafx.scene.layout.FlowPane columns = new javafx.scene.layout.FlowPane();
        columns.setHgap(24);
        columns.setVgap(24);
        columns.setAlignment(Pos.TOP_LEFT);


        columns.getChildren().addAll(
                leftColumn,
                rightColumn,
                calendarColumn
        );

        content.getChildren().addAll(
                heroSection,
                columns
        );

        return content;
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

                // Inserisce nome del tutor
                String persona = session.getTutorMatricola().equals(miaMatricola) ? "Sessione da Tutor" : "Tutor: " + session.getTutorMatricola();

                VBox agendaRow = createAgendaItem(
                        session.getMateria(),
                        "Data da concordare",
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

    private static VBox createFooterSection() {
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

    private static VBox footerColumn(final String title, final String content) {
        final VBox box = new VBox(8);
        box.setPrefWidth(320);

        final Label heading = new Label(title);
        heading.setFont(Font.font("System", FontWeight.BOLD, 22));
        heading.setTextFill(Color.WHITE);

        final Label body = new Label(content);
        body.setWrapText(true);
        body.setMinHeight(Region.USE_PREF_SIZE);
        body.setTextFill(Color.rgb(255, 255, 255, 0.93));
        body.setFont(Font.font("System", FontWeight.NORMAL, 13));

        box.getChildren().addAll(heading, body);
        return box;
    }

}