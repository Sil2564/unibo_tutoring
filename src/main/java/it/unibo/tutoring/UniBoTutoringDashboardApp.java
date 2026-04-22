package it.unibo.tutoring;

import java.nio.file.Path;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UniBoTutoringDashboardApp extends Application {

	private static final Color PRIMARY_RED = Color.web("#D91E43");
	private static final Color PAGE_BG = Color.web("#EFEFEF");
	private static final Color TEXT_DARK = Color.web("#1B1B1B");
	private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");

	@Override
	public void start(final Stage stage) {
		stage.setTitle("UniBo Tutoring - Dashboard");
		stage.setScene(createScene());
		stage.show();
	}

	public static Scene createScene() {
		final UniBoTutoringDashboardApp app = new UniBoTutoringDashboardApp();

		final VBox root = new VBox();
		root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

		root.getChildren().addAll(
			app.createHeader(),
			app.createMainArea(),
			app.createFooterSection()
		);
		VBox.setVgrow(root.getChildren().get(1), Priority.ALWAYS);

		return new Scene(root, 1320, 920);
	}

	private HBox createHeader() {
		final HBox header = new HBox(12);
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(new Insets(10, 18, 10, 18));
		header.setPrefHeight(64);
		header.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		header.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

		final ImageView logo = icon("unibo.png", 30, 30);

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

		final ImageView userIcon = icon("user.png", 16, 16);
		UserAccount user = CurrentSession.getUser();

		final Label userName;
		if (user != null) {
    	userName = new Label(user.getName() + " " + user.getSurname());
		} else {
    	userName = new Label("Utente");
	}
		userName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
		userName.setTextFill(TEXT_DARK);

		final Separator separator = new Separator();
		separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
		separator.setPrefHeight(16);

		final ImageView logoutIcon = icon("logout.png", 14, 14);
final Button logoutButton = new Button("Logout", logoutIcon);
logoutButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
logoutButton.setTextFill(TEXT_DARK);
logoutButton.setBackground(Background.EMPTY);
logoutButton.setBorder(Border.EMPTY);

// LOGICA LOGOUT
logoutButton.setOnAction(event -> {
    CurrentSession.clear();

    Stage stage = (Stage) logoutButton.getScene().getWindow();
    stage.setScene(UniBoTutoringLoginApp.createScene(stage));
    stage.setTitle("UniBo Tutoring - Login");
});

final HBox rightSide = new HBox(8, userIcon, userName, separator, logoutButton);
		rightSide.setAlignment(Pos.CENTER_RIGHT);

		header.getChildren().addAll(brandBlock, spacer, rightSide);
		return header;
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
		final Button collapse = new Button("<");
		collapse.setFont(Font.font("System", FontWeight.BOLD, 11));
		collapse.setTextFill(Color.web("#8A8A8A"));
		collapse.setPrefSize(20, 20);
		collapse.setMinSize(20, 20);
		collapse.setMaxSize(20, 20);
		collapse.setBackground(new Background(new BackgroundFill(Color.web("#F0F0F0"), new CornerRadii(50), Insets.EMPTY)));
		collapse.setBorder(new Border(new BorderStroke(Color.web("#D7D7D7"), BorderStrokeStyle.SOLID, new CornerRadii(50), new BorderWidths(1))));
		navHeader.getChildren().addAll(navTitle, spacer, collapse);

		final VBox menu = new VBox(8);
		
		final Button dashboardBtn = navItem("home_white.png", "Dashboard", "Bacheca annunci", true);
		
		final Button statisticsBtn = navItem("graphic.png", "Statistiche", "Ore e recensioni", false);
		statisticsBtn.setOnAction(event -> {
			final Stage stage = (Stage) statisticsBtn.getScene().getWindow();
			stage.setScene(UniBoTutoringStatisticApp.createScene());
			stage.setTitle("UniBo Tutoring - Statistiche");
		});
		
		menu.getChildren().addAll(dashboardBtn, statisticsBtn);

		sidebar.getChildren().addAll(navHeader, menu);
		return sidebar;
	}

	private Button navItem(final String iconName, final String title, final String subtitle, final boolean active) {
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
		content.setPadding(new Insets(18, 24, 16, 24));

		final HBox titleRow = new HBox();
		titleRow.setAlignment(Pos.CENTER_LEFT);

		final VBox titleBlock = new VBox(0);
		final Label title = new Label("Bacheca Tutoraggio");
		title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 30));
		title.setTextFill(TEXT_DARK);

		final Label subtitle = new Label("Trova o offri aiuto per le tue materie");
		subtitle.setFont(Font.font("System", FontWeight.NORMAL, 20));
		subtitle.setTextFill(Color.web("#5F5F5F"));
		titleBlock.getChildren().addAll(title, subtitle);

		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		final Button createAnnouncement = new Button("+Crea Annuncio");
		createAnnouncement.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
		createAnnouncement.setTextFill(Color.WHITE);
		createAnnouncement.setPadding(new Insets(9, 16, 9, 16));
		createAnnouncement.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(8), Insets.EMPTY)));
		createAnnouncement.setBorder(Border.EMPTY);

		titleRow.getChildren().addAll(titleBlock, spacer, createAnnouncement);

		final HBox filtersRow = new HBox(16);
		filtersRow.setAlignment(Pos.CENTER_LEFT);

		final HBox searchBox = new HBox(8);
		searchBox.setAlignment(Pos.CENTER_LEFT);
		searchBox.setPadding(new Insets(0, 10, 0, 10));
		searchBox.setPrefHeight(44);
		searchBox.setPrefWidth(410);
		searchBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7), Insets.EMPTY)));
		searchBox.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(7), BorderWidths.DEFAULT)));

		final ImageView searchIcon = icon("lent.png", 15, 15);
		searchIcon.setOpacity(0.7);
		final TextField searchField = new TextField();
		searchField.setPromptText("Cerca per materia, corso o descrizione...");
		searchField.setFont(Font.font("System", FontWeight.NORMAL, 14));
		searchField.setBackground(Background.EMPTY);
		searchField.setBorder(Border.EMPTY);
		HBox.setHgrow(searchField, Priority.ALWAYS);
		searchBox.getChildren().addAll(searchIcon, searchField);

		final HBox comboBox = new HBox();
		comboBox.setAlignment(Pos.CENTER_LEFT);
		comboBox.setPadding(new Insets(0, 12, 0, 12));
		comboBox.setPrefHeight(44);
		comboBox.setPrefWidth(260);
		comboBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7), Insets.EMPTY)));
		comboBox.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(7), BorderWidths.DEFAULT)));

		final Label comboLabel = new Label("Tutte le materie");
		comboLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
		final Region comboSpacer = new Region();
		HBox.setHgrow(comboSpacer, Priority.ALWAYS);
		final Label chevron = new Label("v");
		chevron.setTextFill(Color.web("#777777"));
		chevron.setFont(Font.font("System", FontWeight.BOLD, 13));
		comboBox.getChildren().addAll(comboLabel, comboSpacer, chevron);

		filtersRow.getChildren().addAll(searchBox, comboBox);

		final HBox tabs = new HBox(0);
		tabs.setAlignment(Pos.CENTER_LEFT);
		tabs.setPrefHeight(28);
		tabs.setMaxWidth(315);
		tabs.setBackground(new Background(new BackgroundFill(Color.web("#D7D7D7"), new CornerRadii(6), Insets.EMPTY)));
		tabs.setPadding(new Insets(2));

		tabs.getChildren().addAll(
			tab("Tutte (5)", true),
			tab("Offerte (3)", false),
			tab("Richieste (2)", false)
		);

		final FlowPane cards = new FlowPane();
		cards.setHgap(14);
		cards.setVgap(14);
		cards.setPrefWrapLength(860);

		cards.getChildren().addAll(
			announcementCard(true, "Analisi Matematica I", "Ingegneria Informatica", "Disponibile per spiegazioni e aiuto per preparazione esame.", "Mario Rossi", "15 Dic 2025"),
			announcementCard(false, "Programmazione ad Oggetti", "Informatica", "Cerco aiuto per ripetizioni su classi, ereditarieta e Java.", "Laura Bianchi", "16 Dic 2025"),
			announcementCard(true, "Fisica Generale", "Ingegneria Elettronica", "Offro ripetizioni su cinematica, dinamica e termodinamica.", "Giuseppe Verdi", "10 Dic 2025"),
			announcementCard(false, "Basi di Dati", "Informatica per il Management", "Cerco ripetizioni di SQL e progettazione database.", "Luca Ferrari", "17 Dic 2025"),
			announcementCard(true, "Algoritmi e Strutture Dati", "Ingegneria Informatica", "Disponibile per spiegare alberi, grafi e algoritmi di ordinamento.", "Laura Colonna", "10 Dic 2025")
		);

		content.getChildren().addAll(titleRow, filtersRow, tabs, cards);
		return content;
	}

	private Button tab(final String text, final boolean active) {
		final Button tab = new Button(text);
		tab.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
		tab.setTextFill(Color.web("#222222"));
		tab.setPrefHeight(24);
		tab.setPrefWidth(104);
		tab.setBackground(new Background(new BackgroundFill(active ? Color.WHITE : Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
		tab.setBorder(Border.EMPTY);
		return tab;
	}

	private VBox announcementCard(
		final boolean offer,
		final String title,
		final String course,
		final String description,
		final String user,
		final String date
	) {
		final VBox card = new VBox(8);
		card.setPrefWidth(250);
		card.setPadding(new Insets(10, 12, 10, 12));
		card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(6), Insets.EMPTY)));
		card.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));

		final Label tag = new Label(offer ? "Offerta tutoraggio" : "Cerco tutor");
		tag.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 9));
		tag.setTextFill(Color.WHITE);
		tag.setPadding(new Insets(2, 7, 2, 7));
		tag.setBackground(new Background(new BackgroundFill(offer ? PRIMARY_RED : Color.web("#A1A1A1"), new CornerRadii(999), Insets.EMPTY)));

		final Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 12));
		titleLabel.setTextFill(TEXT_DARK);
		titleLabel.setWrapText(true);

		final Label courseLabel = new Label(course);
		courseLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		courseLabel.setTextFill(TEXT_MEDIUM);

		final Label descriptionLabel = new Label(description);
		descriptionLabel.setWrapText(true);
		descriptionLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		descriptionLabel.setTextFill(Color.web("#2D2D2D"));

		final Line divider = new Line(0, 0, 230, 0);
		divider.setStroke(Color.web("#E3E3E3"));

		final ImageView userIcon = icon("user_gray.png", 11, 11);
		final Label userLabel = new Label(user);
		userLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		userLabel.setTextFill(TEXT_MEDIUM);

		final ImageView dateIcon = icon("calendar_gray.png", 11, 11);
		final Label dateLabel = new Label(date);
		dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		dateLabel.setTextFill(TEXT_MEDIUM);

		final VBox meta = new VBox(2,
			new HBox(4, userIcon, userLabel),
			new HBox(4, dateIcon, dateLabel)
		);

		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		final Button contact = new Button("Contatta");
		contact.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 10));
		contact.setTextFill(Color.WHITE);
		contact.setPadding(new Insets(4, 10, 4, 10));
		contact.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(7), Insets.EMPTY)));
		contact.setBorder(Border.EMPTY);

		final HBox bottom = new HBox(8, meta, spacer, contact);
		bottom.setAlignment(Pos.BOTTOM_LEFT);

		card.getChildren().addAll(tag, titleLabel, courseLabel, descriptionLabel, divider, bottom);
		return card;
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
