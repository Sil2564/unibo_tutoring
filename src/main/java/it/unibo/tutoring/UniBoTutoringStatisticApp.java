package it.unibo.tutoring;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import it.unibo.tutoring.model.credit.CreditRepository;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.credit.ReviewRepository;
import it.unibo.tutoring.model.credit.ReviewRepository.Review;
import it.unibo.tutoring.model.credit.CompletedSessionRepository;
import it.unibo.tutoring.model.credit.CompletedSessionRepository.CompletedSession;
import it.unibo.tutoring.view.components.AppHeader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class UniBoTutoringStatisticApp extends Application {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#F8F9FA"); 
    private static final Color TEXT_DARK = Color.web("#111111");
    private static final Color TEXT_MEDIUM = Color.web("#707070"); 
    @Override
    public void start(final Stage stage) {
        stage.setTitle("UniBo Tutoring - Statistiche");
        stage.setScene(createScene());
        stage.show();
    }

    public static Scene createScene() {
        final UniBoTutoringStatisticApp app = new UniBoTutoringStatisticApp();
        final UserAccount user = CurrentSession.getUser();
        final String userDisplayName = user != null ? user.getName() + " " + user.getSurname() : "Utente";

        final VBox root = new VBox();
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        root.getChildren().addAll(
            new AppHeader(userDisplayName, () -> {
                CurrentSession.clear();
                final Stage stage = (Stage) root.getScene().getWindow();
                stage.setScene(UniBoTutoringLoginApp.createScene(stage));
                stage.setTitle("UniBo Tutoring - Login");
            }),
            app.createMainArea(),
            app.createFooterSection()
        );
        VBox.setVgrow(root.getChildren().get(1), Priority.ALWAYS);

        return new Scene(root);
    }

 private HBox createMainArea() {

    final HBox main = new HBox();
    main.setAlignment(Pos.TOP_LEFT);
    main.setFillHeight(true);

    final VBox sidebar = createSidebar();
    final VBox content = createContent();

    content.setMaxWidth(Double.MAX_VALUE);
    content.setPrefWidth(Region.USE_COMPUTED_SIZE);

    HBox.setHgrow(content, Priority.ALWAYS);

    main.getChildren().addAll(sidebar, content);

    return main;
}
    private VBox createSidebar() {
        final VBox sidebar = new VBox(14);
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setPadding(new Insets(14, 10, 14, 10));
        sidebar.setBackground(new Background(new BackgroundFill(Color.web("#F6F6F6"), CornerRadii.EMPTY, Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(Color.web("#D2D2D2"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        final HBox navHeader = new HBox();
        navHeader.setAlignment(Pos.CENTER_LEFT);
        final Label navTitle = new Label("Navigazione");
        navTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 15));
        navTitle.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        final ImageView collapseIcon = icon("arrow-left.png", 10, 10);
        final Button collapse = new Button();
        collapse.setGraphic(collapseIcon);
        collapse.setPadding(new Insets(0));
        collapse.setPrefSize(20, 20);
        collapse.setMinSize(20, 20);
        collapse.setMaxSize(20, 20);
        collapse.setBackground(new Background(new BackgroundFill(Color.web("#F0F0F0"), new CornerRadii(50), Insets.EMPTY)));
        collapse.setBorder(new Border(new BorderStroke(Color.web("#D7D7D7"), BorderStrokeStyle.SOLID, new CornerRadii(50), new BorderWidths(1))));
        navHeader.getChildren().addAll(navTitle, spacer, collapse);

        final VBox menu = new VBox(8);
        
        final List<Node> nodesToHide = new ArrayList<>();
        nodesToHide.add(navTitle);

        final Button dashboardBtn = navItem("home.png", "Dashboard", "Bacheca annunci", false, nodesToHide);
        dashboardBtn.setOnAction(event -> {
            final Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(UniBoTutoringDashboardApp.createScene());
            stage.setTitle("UniBo Tutoring - Dashboard");
        });
        
        final Button statisticsBtn = navItem("graph_white.png", "Statistiche", "Ore e recensioni", true, nodesToHide);
        
        menu.getChildren().addAll(dashboardBtn, statisticsBtn);

        sidebar.getChildren().addAll(navHeader, menu);

        final boolean[] isSidebarOpen = {true};
        final Image arrowLeftImg = new Image(Path.of("src", "icons", "arrow-left.png").toUri().toString());
        final Image arrowRightImg = new Image(Path.of("src", "icons", "arrow-right.png").toUri().toString());

        collapse.setOnAction(event -> {
            isSidebarOpen[0] = !isSidebarOpen[0];
            final boolean open = isSidebarOpen[0];

            final double targetWidth = open ? 250 : 60;
            
            collapseIcon.setImage(open ? arrowLeftImg : arrowRightImg);

            if (!open) {
                nodesToHide.forEach(n -> {
                    n.setVisible(false);
                    n.setManaged(false);
                });
            }

            final Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(250),
                    new KeyValue(sidebar.prefWidthProperty(), targetWidth),
                    new KeyValue(sidebar.minWidthProperty(), targetWidth),
                    new KeyValue(sidebar.maxWidthProperty(), targetWidth)
                )
            );
            
            timeline.setOnFinished(e -> {
                if (open) {
                    nodesToHide.forEach(n -> {
                        n.setVisible(true);
                        n.setManaged(true);
                    });
                }
            });
            
            timeline.play();
        });

        return sidebar;
    }

    private Button navItem(final String iconName, final String title, final String subtitle, final boolean active, final List<Node> nodesToHide) {
        final ImageView icon = icon(iconName, 14, 14);
        final Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 13));

        final Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));

        if (active) {
            titleLabel.setTextFill(Color.WHITE);
            subtitleLabel.setTextFill(Color.rgb(255, 255, 255, 0.85));
        } else {
            titleLabel.setTextFill(TEXT_DARK);
            subtitleLabel.setTextFill(TEXT_MEDIUM);
            icon.setOpacity(0.72);
        }

        final VBox text = new VBox(0, titleLabel, subtitleLabel);
        nodesToHide.add(text);
        final HBox content = new HBox(8, icon, text);
        content.setAlignment(Pos.CENTER_LEFT);

        final Button item = new Button();
        item.setGraphic(content);
        item.setPrefWidth(Double.MAX_VALUE);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 10, 8, 10));
        item.setBackground(new Background(new BackgroundFill(active ? PRIMARY_RED : Color.TRANSPARENT, new CornerRadii(6), Insets.EMPTY)));
        item.setBorder(Border.EMPTY);

        return item;
    }

    private VBox createContent() {
        final VBox content = new VBox();
        content.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final UserAccount user = CurrentSession.getUser();
        final String matricola = user != null ? user.getMatricola() : "";

        // Intestazione Titoli
        final Label title = new Label("Statistiche e Recensioni");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(TEXT_DARK);

        final Label subtitle = new Label("Monitora le tue performance e i tuoi progressi");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(TEXT_MEDIUM);

        final VBox titleBox = new VBox(6, title, subtitle);
        titleBox.setPadding(new Insets(0, 0, 12, 0));

        // Blocchi KPI superiori
        final HBox kpiCards = createKpiCards(matricola);

        // Sezioni Centrali dell'interfaccia
        final VBox reviewsSection = createReviewsSection(matricola);
        final VBox sessionsSection = createSessionsSection(matricola);
        final VBox chartSection = createMonthlySessionsChart(matricola);

        // Configurazione reattiva dello ScrollPane per l'adattamento al riquadro
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); // Forzatura orizzontale del contenuto
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setPrefWidth(0);
        scrollPane.setFitToHeight(true); // Mantenuto per garantire che il contenuto principale si adatti all'altezza
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Blocca lo scroll orizzontale
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F8F9FA; -fx-padding: 0;");

        final VBox scrollContent = new VBox(24);
        scrollContent.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Margini proporzionati: evita che i box tocchino i bordi del riquadro esterno
        scrollContent.setPadding(new Insets(24, 32, 24, 32)); 

        // Garantisce che tutti i macroblocchi si allunghino orizzontalmente insieme al riquadro
        titleBox.setMaxWidth(Double.MAX_VALUE);
        kpiCards.setMaxWidth(Double.MAX_VALUE);
        reviewsSection.setMaxWidth(Double.MAX_VALUE);
        sessionsSection.setMaxWidth(Double.MAX_VALUE);

        scrollContent.getChildren().addAll(titleBox, kpiCards, reviewsSection, sessionsSection, chartSection);
        
        scrollPane.setContent(scrollContent);
        content.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }
    private HBox createKpiCards(final String matricola) {
        final HBox kpiBox = new HBox(20);
        kpiBox.setAlignment(Pos.CENTER_LEFT);

        List<CompletedSession> sessions = CompletedSessionRepository.loadCompletedSessionsForTutor(matricola);
        int totalHours = sessions.stream().mapToInt(CompletedSession::hours).sum();
        int totalCredits = sessions.stream().mapToInt(CompletedSession::creditsGiven).sum();
        double avgRating = CreditRepository.loadRecord(matricola).map(r -> r.getRating()).orElse(4.6);
        
        int totalSessions = sessions.size();

        final VBox card1 = createKpiCard("clock_red.png", "Ore totali", totalHours + "h", "di tutoraggio svolto");
        final VBox card2 = createKpiCard("coccarda.png", "Crediti", totalCredits + "", "crediti accumulati");
        final VBox card3 = createKpiCard("graph_red.png", "Sessioni", totalSessions + "", "completate con successo");
        final VBox card4 = createKpiCard("star_red.png", "Valutazioni", String.format("%.1f/5", avgRating), "recensioni");

        kpiBox.getChildren().addAll(card1, card2, card3, card4);
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        return kpiBox;
    }

    private VBox createKpiCard(final String iconName, final String title, final String value, final String subtitle) {
        final VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1;");

        final HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        final Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final ImageView iconView = icon(iconName, 16, 16);
        headerBox.getChildren().addAll(titleLabel, spacer, iconView);

        final Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setTextFill(TEXT_DARK);

        final Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitleLabel.setTextFill(TEXT_MEDIUM);

        card.getChildren().addAll(headerBox, valueLabel, subtitleLabel);
        return card;
    }

    private VBox createReviewsSection(final String matricola) {
        final VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1;");

        final HBox sectionHeader = new HBox(8);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        final ImageView msgIcon = icon("mex_red.png", 18, 18);
        final Label sectionTitle = new Label("Recensioni ricevute");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(TEXT_DARK);
        sectionHeader.getChildren().addAll(msgIcon, sectionTitle);

        final VBox reviewsList = new VBox(12);
        reviewsList.setMaxWidth(Double.MAX_VALUE);
        
        // Carica TUTTE le recensioni associate alla matricola dell'utente corrente
        List<Review> reviews = ReviewRepository.loadReviewsForRecipient(matricola);

        if (reviews.isEmpty()) {
            final Label none = new Label("Ancora nessuna recensione");
            none.setFont(Font.font("System", FontWeight.NORMAL, 12));
            none.setTextFill(TEXT_MEDIUM);
            reviewsList.getChildren().add(none);
        } else {
            // Ordina tutte le recensioni in ordine cronologico inverso (dalla più recente)
            List<Review> sorted = reviews.stream().collect(Collectors.toList());
            sorted.sort((a, b) -> {
                LocalDate da = parseDateSafe(a.date());
                LocalDate db = parseDateSafe(b.date());
                return db.compareTo(da);
            });
            
            // Cicla ed inserisce l'intera lista delle recensioni all'interno del box scorrevole
            for (final Review review : sorted) {
                final String starsString = "★".repeat(Math.max(0, Math.min(5, review.stars()))) + "☆".repeat(Math.max(0, 5 - review.stars()));
                VBox reviewCard = createCustomReviewCard(review.reviewerName(), review.subject(), starsString, review.date(), review.comment());
                reviewCard.setMaxWidth(Double.MAX_VALUE);
                reviewsList.getChildren().add(reviewCard);
            }
        }

        // Creazione dello ScrollPane interno per contenere tutte le recensioni
        final ScrollPane reviewsScroll = new ScrollPane(reviewsList);
        reviewsScroll.setFitToWidth(true);
        reviewsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        reviewsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Forza l'altezza e il ridimensionamento per essere speculare al box delle sessioni recenti
        reviewsScroll.setPrefHeight(215); 
        reviewsScroll.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(reviewsScroll, Priority.ALWAYS);
        
        // Rende lo sfondo dello ScrollPane trasparente per uniformarsi alla card
        reviewsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        container.getChildren().addAll(sectionHeader, reviewsScroll);
        
        // Imposta l'altezza preferita del container principale per pareggiare le sessioni recenti
        container.setPrefHeight(301);
        container.setMinHeight(301);
        
        return container;
    }
    
        private VBox createCustomReviewCard(String name, String subjectStr, String stars, String dateStr, String comment) {
        final VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-color: white; -fx-background-radius: 8; -fx-border-width: 1;");

        final HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        final Label reviewerName = new Label(name);
        reviewerName.setFont(Font.font("System", FontWeight.BOLD, 13));
        reviewerName.setTextFill(TEXT_DARK);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final HBox starsBox = new HBox(2);
        for (char ch : stars.toCharArray()) {
            Label star = new Label(String.valueOf(ch));
            star.setFont(Font.font("System", 14));
            star.setTextFill(Color.web("#FFC107"));
            starsBox.getChildren().add(star);
        }
        topRow.getChildren().addAll(reviewerName, spacer, starsBox);

        final HBox middleRow = new HBox();
        middleRow.setAlignment(Pos.CENTER_LEFT);
        final Label subLabel = new Label(subjectStr);
        subLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subLabel.setTextFill(TEXT_MEDIUM);

        final Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        final Label dateLabel = new Label(dateStr);
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        dateLabel.setTextFill(TEXT_MEDIUM);
        middleRow.getChildren().addAll(subLabel, spacer2, dateLabel);

        final Label commentLabel = new Label(comment);
        commentLabel.setWrapText(true);
        commentLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        commentLabel.setTextFill(TEXT_DARK);

        card.getChildren().addAll(topRow, middleRow, commentLabel);
        return card;
    }

    private static LocalDate parseDateSafe(final String s) {
        if (s == null) return LocalDate.MIN;
        final String t = s.trim();
        final DateTimeFormatter[] fmts = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("dd MMMM yyyy")
        };
        for (final DateTimeFormatter f : fmts) {
            try {
                return LocalDate.parse(t, f);
            } catch (DateTimeParseException e) {
                // try next
            }
        }
        // fallback: try ISO
        try {
            return LocalDate.parse(t);
        } catch (Exception e) {
            return LocalDate.MIN;
        }
    }

    private VBox createSessionsSection(final String matricola) {
        final VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1;");

        final HBox sectionHeader = new HBox(8);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        final ImageView clockIcon = icon("clock_red.png", 18, 18);
        final Label sectionTitle = new Label("Sessioni recenti");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(TEXT_DARK);
        sectionHeader.getChildren().addAll(clockIcon, sectionTitle);

        final VBox sessionsList = new VBox(12);
        List<CompletedSession> sessions = CompletedSessionRepository.loadCompletedSessionsForTutor(matricola);

        if (sessions.isEmpty()) {
            final Label none = new Label("Non ci sono sessioni recenti");
            none.setFont(Font.font("System", FontWeight.NORMAL, 13));
            none.setTextFill(TEXT_MEDIUM);
            sessionsList.getChildren().add(none);
        } else {
            // Ordina per data decrescente e mostra sempre le ultime 3
            List<CompletedSession> sorted = sessions.stream().collect(Collectors.toList());
            sorted.sort((a, b) -> {
                LocalDate da = parseDateSafe(a.date());
                LocalDate db = parseDateSafe(b.date());
                return db.compareTo(da);
            });
            final int limit = Math.min(3, sorted.size());
            for (int i = 0; i < limit; i++) {
                final CompletedSession session = sorted.get(i);
                sessionsList.getChildren().add(createCustomSessionCard(session.studentName(), session.subject(), session.hours() + "h", session.date(), "+" + session.creditsGiven() + " crediti"));
            }
        }

        // La lista sessioni si espande per adattarsi al contenuto
        container.getChildren().addAll(sectionHeader, sessionsList);
        return container;
    }

    private VBox createMonthlySessionsChart(final String matricola) {
        final VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1;");

        final HBox sectionHeader = new HBox(8);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        final ImageView chartIcon = icon("graph_red.png", 18, 18);
        final Label sectionTitle = new Label("Sessioni mensili");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(TEXT_DARK);
        sectionHeader.getChildren().addAll(chartIcon, sectionTitle);

        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mese");

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sessioni");
        yAxis.setMinorTickVisible(false);
        yAxis.setForceZeroInRange(true);

        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        final XYChart.Series<String, Number> series = new XYChart.Series<>();
        final Map<String, Integer> countsByMonth = new LinkedHashMap<>();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ITALIAN);

        List<CompletedSession> sessions = CompletedSessionRepository.loadCompletedSessionsForTutor(matricola);
        sessions.stream()
            .map(session -> parseDateSafe(session.date()))
            .filter(date -> !date.equals(LocalDate.MIN))
            .sorted()
            .forEach(date -> {
                final String monthLabel = date.format(formatter);
                countsByMonth.put(monthLabel, countsByMonth.getOrDefault(monthLabel, 0) + 1);
            });

        if (countsByMonth.isEmpty()) {
            countsByMonth.put("Nessun dato", 0);
        }

        countsByMonth.forEach((month, count) -> {
            final XYChart.Data<String, Number> data = new XYChart.Data<>(month, count);
            series.getData().add(data);
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    final Tooltip tip = new Tooltip(month + ": " + count + " sessioni");
                    Tooltip.install(newNode, tip);
                    newNode.setStyle("-fx-background-color: #D91E43, white; -fx-background-insets: 0, 2; -fx-background-radius: 8;");
                }
            });
        });

        chart.getData().add(series);
        chart.setPrefHeight(280);
        chart.setMinHeight(240);
        chart.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(chart, Priority.ALWAYS);
        container.getChildren().addAll(sectionHeader, chart);
        return container;
    }

    private HBox createCustomSessionCard(String student, String subject, String hours, String date, String credits) {
        final HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-color: white; -fx-background-radius: 8; -fx-border-width: 1;");

        final VBox details = new VBox(4);
        final Label studentName = new Label(student);
        studentName.setFont(Font.font("System", FontWeight.BOLD, 13));
        studentName.setTextFill(TEXT_DARK);

        final Label subjectLabel = new Label(subject);
        subjectLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subjectLabel.setTextFill(TEXT_MEDIUM);
        details.getChildren().addAll(studentName, subjectLabel);

        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final VBox rightInfo = new VBox(4);
        rightInfo.setAlignment(Pos.CENTER_RIGHT);
        final Label hoursAndDate = new Label(hours + "    " + date);
        hoursAndDate.setFont(Font.font("System", FontWeight.NORMAL, 12));
        hoursAndDate.setTextFill(TEXT_DARK);

        final Label creditsLabel = new Label(credits);
        creditsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        creditsLabel.setTextFill(PRIMARY_RED);
        rightInfo.getChildren().addAll(hoursAndDate, creditsLabel);

        card.getChildren().addAll(details, spacer, rightInfo);
        return card;
    }


    private VBox createFooterSection() {
        final VBox section = new VBox(20);
        section.setPadding(new Insets(26, 40, 18, 40));
        section.setBackground(new Background(new BackgroundFill(PRIMARY_RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox cols = new HBox(50);
        final VBox col1 = footerColumn("Università di Bologna", "UniBo Tutoring è la piattaforma ufficiale per il supporto tra studenti dell'Università di Bologna.\n\nVia Zamboni, 33\n40126 Bologna, Italia");
        final VBox col2 = footerColumn("Documenti", "Privacy Policy\nTermini e Condizioni\nCodice di Condotta");
        final VBox col3 = footerColumn("Contatti e Assistenza", "Email di supporto:\ntutoring@unibo.it\n\nHai bisogno di aiuto?\nApri box assistenza");

        cols.getChildren().addAll(col1, col2, col3);
        HBox.setHgrow(col1, Priority.ALWAYS);
        HBox.setHgrow(col2, Priority.ALWAYS);
        HBox.setHgrow(col3, Priority.ALWAYS);

        final Label copyright = new Label("© 2026 Università di Bologna - UniBo Tutoring. Tutti i diritti riservati.");
        copyright.setAlignment(Pos.CENTER);
        HBox.setHgrow(copyright, Priority.ALWAYS);
        copyright.setTextFill(Color.rgb(255, 255, 255, 0.94));
        copyright.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

        section.getChildren().addAll(cols, copyright);
        return section;
    }

    private VBox footerColumn(final String title, final String content) {
        final VBox box = new VBox(8);

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

    private ImageView icon(final String iconName, final double w, final double h) {
        final Image image = new Image(Path.of("src", "icons", iconName).toUri().toString());
        final ImageView view = new ImageView(image);
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    public static void run(final String[] args) {
        launch(args);
    }
}