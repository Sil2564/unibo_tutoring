package it.unibo.tutoring;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import it.unibo.tutoring.view.box.CreateAnnouncementViewApp;
import it.unibo.tutoring.view.components.AppHeader;
import it.unibo.tutoring.view.session.TutoringSessionViewApp;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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
import javafx.util.Duration;

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
		final UserAccount user = CurrentSession.getUser();
		final String userDisplayName = user != null ? user.getName() + " " + user.getSurname() : "Utente";

		final VBox root = new VBox();
		root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));

		final VBox scrollContent = new VBox();
		final HBox mainArea = app.createMainArea();
		scrollContent.getChildren().addAll(
			mainArea,
			app.createFooterSection()
		);
		scrollContent.setMinHeight(Region.USE_PREF_SIZE);
		VBox.setVgrow(mainArea, Priority.ALWAYS);

		final ScrollPane scrollPane = new ScrollPane(scrollContent);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		scrollPane.setBorder(Border.EMPTY);

		root.getChildren().addAll(
			new AppHeader(userDisplayName, () -> {
				CurrentSession.clear();
				final Stage stage = (Stage) root.getScene().getWindow();
				stage.setScene(UniBoTutoringLoginApp.createScene(stage));
				stage.setTitle("UniBo Tutoring - Login");
			}),
			scrollPane
		);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		final ScrollPane scrollPane =
    new ScrollPane(root);

scrollPane.setFitToWidth(true);

scrollPane.setFitToHeight(true);

scrollPane.setPannable(true);

scrollPane.setHbarPolicy(
    ScrollPane.ScrollBarPolicy.NEVER
);

return new Scene(
    scrollPane,
    1320,
    920
);
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
		
		// Lista che raccoglie tutti i nodi testuali (come i titoli o etichette) da nascondere
		// quando la sidebar viene ristretta, per evitare che si sovrappongano o si deformino.
		final List<Node> nodesToHide = new ArrayList<>();
		nodesToHide.add(navTitle);

		final Button dashboardBtn = navItem("home_white.png", "Dashboard", "Bacheca annunci", true, nodesToHide);
		
		final Button statisticsBtn = navItem("graphic.png", "Statistiche", "Ore e recensioni", false, nodesToHide);
		statisticsBtn.setOnAction(event -> {
			final Stage stage = (Stage) statisticsBtn.getScene().getWindow();
			stage.setScene(UniBoTutoringStatisticApp.createScene());
			stage.setTitle("UniBo Tutoring - Statistiche");
		});
		
		menu.getChildren().addAll(dashboardBtn, statisticsBtn);

		sidebar.getChildren().addAll(navHeader, menu);

		// Stato iniziale della sidebar (aperta di default)
		final boolean[] isSidebarOpen = {true};
		final Image arrowLeftImg = new Image(Path.of("src", "icons", "arrow-left.png").toUri().toString());
		final Image arrowRightImg = new Image(Path.of("src", "icons", "arrow-right.png").toUri().toString());

		collapse.setOnAction(event -> {
			// Inverte lo stato: se era aperta diventa chiusa e viceversa
			isSidebarOpen[0] = !isSidebarOpen[0];
			final boolean open = isSidebarOpen[0];

			// Imposta la larghezza target: 250 pixel se aperta, 60 pixel (solo icone) se chiusa
			final double targetWidth = open ? 250 : 60;
			
			// Cambia immediatamente l'icona del pulsante in base al nuovo stato
			collapseIcon.setImage(open ? arrowLeftImg : arrowRightImg);

			// Se la sidebar si sta chiudendo, nascondiamo i testi PRIMA che inizi l'animazione.
			// In questo modo evitiamo che il testo venga "schiacciato" stringendo l'interfaccia.
			if (!open) {
				nodesToHide.forEach(n -> {
					n.setVisible(false);
					n.setManaged(false); // setManaged(false) impedisce al nodo di occupare spazio nel layout
				});
			}

			// Timeline crea un'animazione basata su fotogrammi chiave (KeyFrame)
			final Timeline timeline = new Timeline(
				// La durata dell'animazione è impostata a 250 millisecondi
				new KeyFrame(Duration.millis(250),
					// Modifichiamo in modo fluido la larghezza (pref, min e max) della sidebar verso la targetWidth
					new KeyValue(sidebar.prefWidthProperty(), targetWidth),
					new KeyValue(sidebar.minWidthProperty(), targetWidth),
					new KeyValue(sidebar.maxWidthProperty(), targetWidth)
				)
			);
			
			// Azione da eseguire quando l'animazione giunge al termine
			timeline.setOnFinished(e -> {
				// Se la sidebar è stata aperta, ripristiniamo la visibilità dei testi
				// SOLO DOPO che la sidebar ha raggiunto la sua larghezza massima, così compaiono con lo spazio adeguato
				if (open) {
					nodesToHide.forEach(n -> {
						n.setVisible(true);
						n.setManaged(true);
					});
				}
			});
			
			// Avvia l'animazione di transizione
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
		// Aggiungiamo il contenitore dei testi di questo bottone alla lista dei nodi da nascondere
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
		createAnnouncement.setOnAction(event -> {

    final Stage stage =
        (Stage) createAnnouncement
            .getScene()
            .getWindow();

    stage.setScene(
        CreateAnnouncementViewApp
            .createScene()
    );

    stage.setTitle(
        "UniBo Tutoring - Nuovo Annuncio"
    );
});
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

		final ComboBox<String> subjectCombo = new ComboBox<>();
		subjectCombo.getItems().addAll(
			"Tutte le materie",
			"Analisi Matematica I",
			"Programmazione ad Oggetti",
			"Fisica Generale",
			"Basi di Dati",
			"Algoritmi e Strutture Dati"
		);
		subjectCombo.getSelectionModel().selectFirst();
		subjectCombo.setPrefHeight(44);
		subjectCombo.setPrefWidth(260);
		subjectCombo.setStyle("-fx-background-color: white; -fx-border-color: #CFCFCF; -fx-border-radius: 7; -fx-background-radius: 7; -fx-font-family: 'System'; -fx-font-weight: 600; -fx-font-size: 14px;");

		filtersRow.getChildren().addAll(searchBox, subjectCombo);

		final HBox tabs = new HBox(0);
		tabs.setAlignment(Pos.CENTER_LEFT);
		tabs.setPrefHeight(28);
		tabs.setMaxWidth(315);
		tabs.setBackground(new Background(new BackgroundFill(Color.web("#D7D7D7"), new CornerRadii(6), Insets.EMPTY)));
		tabs.setPadding(new Insets(2));

		final Button tabAll = tab("Tutte (5)", true);
		final Button tabOffers = tab("Offerte (3)", false);
		final Button tabRequests = tab("Richieste (2)", false);

		final List<Button> allTabs = List.of(tabAll, tabOffers, tabRequests);
		for (final Button t : allTabs) {
			t.setOnAction(e -> {
				for (final Button other : allTabs) {
					final boolean isActive = (other == t);
					other.setBackground(new Background(new BackgroundFill(isActive ? Color.WHITE : Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
				}
			});
		}

		tabs.getChildren().addAll(tabAll, tabOffers, tabRequests);

		final FlowPane cards = new FlowPane();
		cards.setHgap(14);
		cards.setVgap(14);
		cards.prefWrapLengthProperty().bind(
    content.widthProperty().subtract(40)
);


		cards.getChildren().addAll(
			announcementCard(true, "Analisi Matematica I", "Ingegneria Informatica", "Disponibile per spiegazioni e aiuto per preparazione esame.", "Mario Rossi", "15 Dic 2025", "1111111"),
			announcementCard(false, "Programmazione ad Oggetti", "Informatica", "Cerco aiuto per ripetizioni su classi, ereditarieta e Java.", "Laura Bianchi", "16 Dic 2025","2323232"),
			announcementCard(true, "Fisica Generale", "Ingegneria Elettronica", "Offro ripetizioni su cinematica, dinamica e termodinamica.", "Giuseppe Verdi", "10 Dic 2025","3333333"),
			announcementCard(false, "Basi di Dati", "Informatica per il Management", "Cerco ripetizioni di SQL e progettazione database.", "Luca Ferrari", "17 Dic 2025","2323232"),
			announcementCard(true, "Algoritmi e Strutture Dati", "Ingegneria Informatica", "Disponibile per spiegare alberi, grafi e algoritmi di ordinamento.", "Laura Colonna", "10 Dic 2025","4444444")
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
		final String date,
        final String matricolaInserzionista
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
		contact.setOnAction(event -> {
			final Stage win = (Stage) contact.getScene().getWindow();
			win.setScene(TutoringSessionViewApp.createScene(win, title, user, offer, matricolaInserzionista));
			win.setTitle("UniBo Tutoring - Dettaglio Sessione");
		});

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
        body.setMinHeight(Region.USE_PREF_SIZE);
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
