package it.unibo.tutoring.view.box;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalTime;

import it.unibo.tutoring.model.box.BoxRepository;
import it.unibo.tutoring.model.box.BoxTutoraggioImpl;
import it.unibo.tutoring.model.box.BoxType;






public final class CreateAnnouncementViewApp {

    private static final Color PRIMARY_RED =
        Color.web("#D91E43");

    private static final Color PAGE_BG =
        Color.web("#EFEFEF");

    private static final Color TEXT_DARK =
        Color.web("#1B1B1B");

    private CreateAnnouncementViewApp() {
    }

    public static Scene createScene() {

        final UserAccount user =
            CurrentSession.getUser();

        final VBox root = new VBox(24);

        root.setPadding(new Insets(30));

        root.setBackground(
            new Background(
                new BackgroundFill(
                    PAGE_BG,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        );

        final HBox header = new HBox();

        final Label title = new Label(
            "Nuovo Annuncio"
        );

        title.setFont(
            Font.font(
                "System",
                FontWeight.EXTRA_BOLD,
                32
            )
        );

        title.setTextFill(TEXT_DARK);

        final Region spacer = new Region();

        HBox.setHgrow(
            spacer,
            javafx.scene.layout.Priority.ALWAYS
        );

        final Button dashboardButton =
            new Button("← Dashboard");

        dashboardButton.setOnAction(event -> {

            final Stage stage =
                (Stage) dashboardButton
                    .getScene()
                    .getWindow();

            stage.setScene(
                UniBoTutoringDashboardApp
                    .createScene()
            );

            stage.setTitle(
                "UniBo Tutoring - Dashboard"
            );
        });

        header.getChildren().addAll(
            title,
            spacer,
            dashboardButton
        );

        final VBox card = new VBox(18);

        card.setPadding(new Insets(30));

        card.setAlignment(Pos.TOP_LEFT);

        card.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(16),
                    Insets.EMPTY
                )
            )
        );

        card.setBorder(
            new Border(
                new BorderStroke(
                    Color.web("#D6D6D6"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(16),
                    BorderWidths.DEFAULT
                )
            )
        );

        final Label sessionLabel =
            new Label(
                "Sessione con "
                + user.getName()
                + " "
                + user.getSurname()
            );

        sessionLabel.setFont(
            Font.font(
                "System",
                FontWeight.EXTRA_BOLD,
                24
            )
        );

        sessionLabel.setTextFill(TEXT_DARK);

        final ComboBox<String> corsoBox =
    new ComboBox<>();

corsoBox.getItems().addAll(
    "Tecnologie dei Sistemi Informatici",
    "Informatica",
    "Ingegneria Informatica",
    "Ingegneria Elettronica"
);

corsoBox.setValue(
    "Tecnologie dei Sistemi Informatici"
);

final TextField materiaField =
    new TextField();

materiaField.setPromptText(
    "Es. Programmazione ad Oggetti"
);

final TextField argomentoField =
    new TextField();

argomentoField.setPromptText(
    "Es. Pattern MVC"
);

final DatePicker dataPicker =
    new DatePicker();

final TextField oraField =
    new TextField();

oraField.setPromptText(
    "HH:mm"
);

final Spinner<Integer> durataSpinner =
    new Spinner<>(1, 8, 2);

final ToggleGroup tipoGroup =
    new ToggleGroup();

final RadioButton offertaRadio =
    new RadioButton(
        "Offerta Tutoraggio"
    );

offertaRadio.setToggleGroup(
    tipoGroup
);

offertaRadio.setSelected(true);

final RadioButton richiestaRadio =
    new RadioButton(
        "Richiesta Tutoraggio"
    );

richiestaRadio.setToggleGroup(
    tipoGroup
);

final Button publishButton =
    new Button(
        "Pubblica Annuncio"
    );

publishButton.setBackground(
    new Background(
        new BackgroundFill(
            PRIMARY_RED,
            new CornerRadii(8),
            Insets.EMPTY
        )
    )
);

publishButton.setTextFill(
    Color.WHITE
);

publishButton.setOnAction(event -> {

    if (materiaField.getText().isBlank()
        || argomentoField.getText().isBlank()
        || dataPicker.getValue() == null
        || oraField.getText().isBlank()) {

        System.out.println(
            "Compila tutti i campi"
        );

        return;
    }

    try {

        final LocalTime ora =
            LocalTime.parse(
                oraField.getText()
            );

        final BoxType tipo =
            offertaRadio.isSelected()
                ? BoxType.OFFER
                : BoxType.REQUEST;

        final String titolo =
            "Sessione con "
            + user.getName()
            + " "
            + user.getSurname();

        final BoxTutoraggioImpl box =
            new BoxTutoraggioImpl(
                titolo,
                corsoBox.getValue(),
                materiaField.getText(),
                argomentoField.getText(),
                dataPicker.getValue(),
                ora,
                durataSpinner.getValue(),
                user.getMatricola(),
                tipo
            );

        BoxRepository.addBox(box);

        final Stage stage =
            (Stage) publishButton
                .getScene()
                .getWindow();

        stage.setScene(
            UniBoTutoringDashboardApp
                .createScene()
        );

        stage.setTitle(
            "UniBo Tutoring - Dashboard"
        );

    } catch (final Exception exception) {

        System.out.println(
            "Formato orario non valido. Usa HH:mm"
        );
    }
});
card.getChildren().addAll(
    sessionLabel,

    new Label("Corso"),
    corsoBox,

    new Label("Materia"),
    materiaField,

    new Label("Argomento"),
    argomentoField,

    new Label("Data"),
    dataPicker,

    new Label("Orario"),
    oraField,

    new Label("Durata (ore)"),
    durataSpinner,

    new Label("Tipo"),
    offertaRadio,
    richiestaRadio,

    publishButton
);

        root.getChildren().addAll(
            header,
            card
        );

        return new Scene(
            root,
            1320,
            920
        );
    }
}