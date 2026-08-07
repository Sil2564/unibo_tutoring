package it.unibo.tutoring;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unibo.tutoring.model.box.BoxRepository;
import it.unibo.tutoring.model.box.BoxTutoraggio;
import it.unibo.tutoring.model.box.BoxType;
import it.unibo.tutoring.controller.session.TutoringSessionController;
import it.unibo.tutoring.view.box.AnnouncementDetailViewApp;
import it.unibo.tutoring.view.box.CreateAnnouncementViewApp;
import it.unibo.tutoring.view.components.AppHeader;
import it.unibo.tutoring.view.session.SessionLinkUtil;
import it.unibo.tutoring.view.session.TutoringSessionViewApp;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
		it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
	}

	public static Scene createScene() {
		final UniBoTutoringDashboardApp app = new UniBoTutoringDashboardApp();
		final UserAccount user = CurrentSession.getUser();
		final String userDisplayName = user != null ? user.getName() + " " + user.getSurname() : "Utente";

		final VBox root = new VBox();
		root.getStyleClass().addAll("app-shell", "content-shell");
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
		it.unibo.tutoring.view.components.WindowUtil.applyStandardScrollPolicy(scrollPane);

		root.getChildren().addAll(
			new AppHeader(userDisplayName, () -> {
				CurrentSession.clear();
				final Stage stage = (Stage) root.getScene().getWindow();
				stage.setScene(UniBoTutoringLoginApp.createScene(stage));
				stage.setTitle("UniBo Tutoring - Login");
				it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
			}),
			scrollPane
		);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		final Scene scene = new Scene(root);
		scene.getStylesheets().add(UniBoTutoringDashboardApp.class.getResource("/styles.css").toExternalForm());
		return scene;
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
		sidebar.getStyleClass().add("sidebar-pane");
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
			it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
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
		item.getStyleClass().add("sidebar-btn");
		if (active) {
			item.getStyleClass().add("active");
		}
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
		content.getStyleClass().add("content-shell");
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
    it.unibo.tutoring.view.components.WindowUtil.maximize(stage);
});
		createAnnouncement.getStyleClass().add("primary-btn");
		createAnnouncement.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
		createAnnouncement.setTextFill(Color.WHITE);
		createAnnouncement.setPadding(new Insets(9, 16, 9, 16));
		createAnnouncement.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(8), Insets.EMPTY)));
		createAnnouncement.setBorder(Border.EMPTY);
		createAnnouncement.setCursor(Cursor.HAND);
		createAnnouncement.setStyle("-fx-padding: 9 16 9 16;");
		createAnnouncement.setOnMouseEntered(event -> {
			createAnnouncement.setScaleX(1.0);
			createAnnouncement.setScaleY(1.0);
			createAnnouncement.setTranslateX(0);
			createAnnouncement.setTranslateY(0);
		});
		createAnnouncement.setOnMouseExited(event -> {
			createAnnouncement.setScaleX(1.0);
			createAnnouncement.setScaleY(1.0);
			createAnnouncement.setTranslateX(0);
			createAnnouncement.setTranslateY(0);
		});

		titleRow.getChildren().addAll(titleBlock, spacer, createAnnouncement);

		final HBox filtersRow = new HBox(16);
		filtersRow.setAlignment(Pos.CENTER_LEFT);

		final HBox searchBox = new HBox(8);
		searchBox.setAlignment(Pos.CENTER_LEFT);
		searchBox.getStyleClass().add("search-box");
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

		final List<BoxTutoraggio> allBoxes = BoxRepository.getAllBoxes();

		final List<String> corsiDisponibili = it.unibo.tutoring.model.box.CorsiDiStudio.TUTTI;
		final java.util.LinkedHashSet<String> courseItems = new java.util.LinkedHashSet<>();
		courseItems.add("Tutti i corsi");
		courseItems.addAll(corsiDisponibili);

		final ComboBox<String> courseCombo = new ComboBox<>();
		courseCombo.getItems().addAll(courseItems);
		courseCombo.getSelectionModel().selectFirst();
		courseCombo.setPrefHeight(44);
		courseCombo.setPrefWidth(260);
		courseCombo.setStyle("-fx-background-color: white; -fx-border-color: #CFCFCF; -fx-border-radius: 7; -fx-background-radius: 7; -fx-font-family: 'System'; -fx-font-weight: 600; -fx-font-size: 14px;");

		filtersRow.getChildren().addAll(searchBox, courseCombo);

		final HBox tabs = new HBox(0);
		tabs.setAlignment(Pos.CENTER_LEFT);
		tabs.setPrefHeight(28);
		tabs.setMaxWidth(430);
		tabs.getStyleClass().add("chip-group");
		tabs.setBackground(new Background(new BackgroundFill(Color.web("#D7D7D7"), new CornerRadii(6), Insets.EMPTY)));
		tabs.setPadding(new Insets(2));

		final String me = CurrentSession.getUser() != null ? CurrentSession.getUser().getMatricola() : null;

		// Nelle tab Tutte/Offerte/Richieste compaiono solo gli annunci ancora
		// "aperti" (nessun candidato confermato): appena l'autore conferma
		// qualcuno, l'annuncio sparisce da qui e resta solo in "Le mie sessioni".
		final List<BoxTutoraggio> openBoxes = allBoxes.stream()
			.filter(b -> b.getConfermato() == null)
			.toList();
		final List<BoxTutoraggio> mySessionsBoxes = allBoxes.stream()
			.filter(b -> isVisibleInMieSessioni(b, me))
			.toList();

		final long offerCount = openBoxes.stream().filter(b -> b.getTipo() == BoxType.OFFER).count();
		final long requestCount = openBoxes.size() - offerCount;

		final Button tabAll = tab("Tutte (" + openBoxes.size() + ")", true);
		final Button tabOffers = tab("Offerte (" + offerCount + ")", false);
		final Button tabRequests = tab("Richieste (" + requestCount + ")", false);
		final Button tabMySessions = tab("Le mie sessioni (" + mySessionsBoxes.size() + ")", false);

		final List<Button> allTabs = List.of(tabAll, tabOffers, tabRequests, tabMySessions);

		// Modalita' di visualizzazione: ALL/OFFERS/REQUESTS filtrano tra gli
		// annunci ancora aperti; MY_SESSIONS mostra invece i miei annunci e le
		// mie candidature/conferme, a prescindere dal tipo.
		final BoxType[] selectedType = { null };
		final boolean[] mySessionsMode = { false };

		final FlowPane cards = new FlowPane();
		cards.setHgap(14);
		cards.setVgap(14);
		cards.prefWrapLengthProperty().bind(
			content.widthProperty().subtract(40)
		);

		final Runnable refreshCards = () -> {
			final String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ITALIAN);
			final String selectedCourse = courseCombo.getValue();
			final boolean allCourses = selectedCourse == null || "Tutti i corsi".equals(selectedCourse);

			final List<BoxTutoraggio> base = mySessionsMode[0]
				? allBoxes.stream().filter(b -> isVisibleInMieSessioni(b, me)).toList()
				: openBoxes;

			final List<BoxTutoraggio> filtered = base.stream()
				.filter(b -> mySessionsMode[0] || selectedType[0] == null || b.getTipo() == selectedType[0])
				.filter(b -> allCourses || selectedCourse.equalsIgnoreCase(b.getCorso()))
				.filter(b -> {
					if (query.isEmpty()) {
						return true;
					}
					final String haystack = String.join(" ",
						nullToEmpty(b.getMateria()),
						nullToEmpty(b.getCorso()),
						nullToEmpty(b.getArgomento()),
						nullToEmpty(b.getTitolo())
					).toLowerCase(Locale.ITALIAN);
					return haystack.contains(query);
				})
				.toList();

			cards.getChildren().clear();
			if (filtered.isEmpty()) {
				final Label emptyLabel = new Label(allBoxes.isEmpty()
					? "Nessun annuncio disponibile. Crea il primo con \"+Crea Annuncio\"."
					: mySessionsMode[0]
						? "Non hai ancora nessuna sessione: pubblica un annuncio o candidati a uno esistente."
						: "Nessun annuncio corrisponde ai filtri selezionati.");
				emptyLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
				emptyLabel.setTextFill(TEXT_MEDIUM);
				cards.getChildren().add(emptyLabel);
			} else {
				for (final BoxTutoraggio box : filtered) {
					cards.getChildren().add(announcementCard(box));
				}
			}
		};

		for (final Button t : allTabs) {
			t.setOnAction(e -> {
				for (final Button other : allTabs) {
					final boolean isActive = (other == t);
					other.getStyleClass().remove("chip-button-active");
					if (isActive) {
						other.getStyleClass().add("chip-button-active");
					}
					other.setBackground(new Background(new BackgroundFill(isActive ? Color.WHITE : Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
				}
				mySessionsMode[0] = (t == tabMySessions);
				if (t == tabAll) {
					selectedType[0] = null;
				} else if (t == tabOffers) {
					selectedType[0] = BoxType.OFFER;
				} else if (t == tabRequests) {
					selectedType[0] = BoxType.REQUEST;
				}
				refreshCards.run();
			});
		}

		searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCards.run());
		courseCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshCards.run());

		tabs.getChildren().addAll(tabAll, tabOffers, tabRequests, tabMySessions);

		refreshCards.run();

		content.getChildren().addAll(titleRow, filtersRow, tabs, cards);
		return content;
	}

	private static String nullToEmpty(final String s) {
		return s == null ? "" : s;
	}

	/**
	 * Un annuncio compare in "Le mie sessioni" se l'utente ne e' l'autore,
	 * si e' candidato (in attesa di conferma), oppure e' il candidato
	 * confermato. Se la sessione confermata risulta pero' gia' completata da
	 * entrambe le parti, sparisce da qui (resta visibile solo nelle Statistiche).
	 */
	private static boolean isVisibleInMieSessioni(final BoxTutoraggio box, final String me) {
		if (me == null) {
			return false;
		}

		final boolean isAutore = me.equals(box.getAutoreMatricola());
		final boolean isCandidato = box.isCandidato(me);
		final boolean isConfermato = me.equals(box.getConfermato());

		if (!isAutore && !isCandidato && !isConfermato) {
			return false;
		}

		if (box.getConfermato() != null) {
			final TutoringSessionController controller =
				SessionLinkUtil.buildController(box, box.getConfermato(), box.getAutoreMatricola());
			return !controller.isCompletataDaEntrambi();
		}

		return true;
	}

	private Button tab(final String text, final boolean active) {
		final Button tab = new Button(text);
		tab.getStyleClass().add("chip-button");
		tab.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
		tab.setTextFill(Color.web("#222222"));
		tab.setPrefHeight(24);
		tab.setPrefWidth(104);
		tab.setBackground(new Background(new BackgroundFill(active ? Color.WHITE : Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
		tab.setBorder(Border.EMPTY);
		return tab;
	}

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);

	private VBox announcementCard(final BoxTutoraggio box) {
		final boolean offer = box.getTipo() == BoxType.OFFER;

		final VBox card = new VBox(8);
		card.getStyleClass().add("announcement-card");
		card.setPrefWidth(250);
		card.setPadding(new Insets(10, 12, 10, 12));
		card.setCursor(Cursor.HAND);
		card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(6), Insets.EMPTY)));
		card.setBorder(new Border(new BorderStroke(Color.web("#CFCFCF"), BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));
		card.setOnMouseClicked(event -> {
			final Stage win = (Stage) card.getScene().getWindow();
			win.setScene(AnnouncementDetailViewApp.createScene(win, box));
			win.setTitle("UniBo Tutoring - Dettaglio Annuncio");
			it.unibo.tutoring.view.components.WindowUtil.maximize(win);
		});

		final Label tag = new Label(offer ? "Offerta tutoraggio" : "Cerco tutor");
		tag.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 9));
		tag.setTextFill(Color.WHITE);
		tag.setPadding(new Insets(2, 7, 2, 7));
		tag.setBackground(new Background(new BackgroundFill(offer ? PRIMARY_RED : Color.web("#A1A1A1"), new CornerRadii(999), Insets.EMPTY)));

		final HBox tagRow = new HBox(6, tag);
		tagRow.setAlignment(Pos.CENTER_LEFT);
		final String statusText = statusChipText(box);
		if (statusText != null) {
			final Label statusChip = new Label(statusText);
			statusChip.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 9));
			statusChip.setTextFill(Color.WHITE);
			statusChip.setPadding(new Insets(2, 7, 2, 7));
			statusChip.setBackground(new Background(new BackgroundFill(
				box.getConfermato() != null ? Color.web("#28A745") : Color.web("#3D7CC9"),
				new CornerRadii(999), Insets.EMPTY)));
			tagRow.getChildren().add(statusChip);
		}

		final String autoreNome = estraiNomeAutore(box.getTitolo());
		final Label titleLabel = new Label(box.getTitolo() + (offer ? " (Tutor)" : " (Studente)"));
		titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 12));
		titleLabel.setTextFill(TEXT_DARK);
		titleLabel.setWrapText(true);

		final Label courseLabel = new Label(box.getCorso() + " · " + box.getMateria());
		courseLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		courseLabel.setTextFill(TEXT_MEDIUM);

		final Label descriptionLabel = new Label("Argomenti: " + box.getArgomento());
		descriptionLabel.setWrapText(true);
		descriptionLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		descriptionLabel.setTextFill(Color.web("#2D2D2D"));

		final Line divider = new Line(0, 0, 230, 0);
		divider.setStroke(Color.web("#E3E3E3"));

		final ImageView userIcon = icon("user_gray.png", 11, 11);
		final Label userLabel = new Label(autoreNome);
		userLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		userLabel.setTextFill(TEXT_MEDIUM);

		final ImageView dateIcon = icon("calendar_gray.png", 11, 11);
		final String dateText = box.getData().format(DATE_FORMAT) + " - " + box.getOra() + " - " + box.getDurataOre() + "h";
		final Label dateLabel = new Label(dateText);
		dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
		dateLabel.setTextFill(TEXT_MEDIUM);

		final VBox meta = new VBox(2,
			new HBox(4, userIcon, userLabel),
			new HBox(4, dateIcon, dateLabel)
		);

		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		final String me = CurrentSession.getUser() != null ? CurrentSession.getUser().getMatricola() : null;
		final boolean isAutore = me != null && me.equals(box.getAutoreMatricola());

		final HBox bottom = new HBox(8, meta, spacer);
		bottom.setAlignment(Pos.BOTTOM_LEFT);

		// Il pulsante "Contatta" non compare sui propri annunci (non ha senso
		// aprire una chat con se stessi): resta visibile solo sugli annunci
		// altrui, sia per chi si e' gia' candidato sia per chi non l'ha ancora fatto.
		if (!isAutore) {
			final Button contact = new Button("Contatta");
			contact.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 10));
			contact.setTextFill(Color.WHITE);
			contact.setPadding(new Insets(4, 10, 4, 10));
			contact.setBackground(new Background(new BackgroundFill(PRIMARY_RED, new CornerRadii(7), Insets.EMPTY)));
			contact.setBorder(Border.EMPTY);
			contact.setCursor(Cursor.HAND);
			contact.setOnAction(event -> {
				box.aggiungiContatto(me);
				BoxRepository.saveAll();
				final Stage win = (Stage) contact.getScene().getWindow();
				win.setScene(TutoringSessionViewApp.createScene(win, box, box.getAutoreMatricola()));
				win.setTitle("UniBo Tutoring - Dettaglio Sessione");
				it.unibo.tutoring.view.components.WindowUtil.maximize(win);
			});
			contact.addEventHandler(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
			bottom.getChildren().add(contact);
		}

		card.getChildren().addAll(tagRow, titleLabel, courseLabel, descriptionLabel, divider, bottom);
		return card;
	}

	/** Testo del chip di stato mostrato accanto al tag Offerta/Richiesta, o null se non applicabile. */
	private static String statusChipText(final BoxTutoraggio box) {
		if (box.getConfermato() != null) {
			return "Confermata";
		}
		final int n = box.getCandidati().size();
		if (n > 0) {
			return n + " candidat" + (n == 1 ? "o" : "i");
		}
		return null;
	}

	private static String estraiNomeAutore(final String titolo) {
		final String prefix = "Sessione con ";
		return titolo != null && titolo.startsWith(prefix) ? titolo.substring(prefix.length()) : titolo;
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