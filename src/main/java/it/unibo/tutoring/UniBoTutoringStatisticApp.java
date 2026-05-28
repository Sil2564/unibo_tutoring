package it.unibo.tutoring;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import it.unibo.tutoring.view.components.AppHeader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class UniBoTutoringStatisticApp extends Application {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");

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

        return new Scene(root, 1320, 920);
    }

    private HBox createMainArea() {
        final HBox main = new HBox();
        main.setAlignment(Pos.TOP_LEFT);
        main.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

        final VBox sidebar = createSidebar();
        final VBox content = createContent();

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
        final VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(18, 24, 16, 24));

        final Label title = new Label("Statistiche");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 44));
        title.setTextFill(TEXT_DARK);

        final Label subtitle = new Label("Le tue statistiche di tutoraggio verranno visualizzate qui");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.web("#6A6A6A"));

        content.getChildren().addAll(title, subtitle);
        return content;
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
