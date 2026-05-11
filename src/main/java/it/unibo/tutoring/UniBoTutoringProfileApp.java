package it.unibo.tutoring;

import java.nio.file.Path;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class UniBoTutoringProfileApp  {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");
    private static final Color TEXT_MEDIUM = Color.web("#6A6A6A");

    private UniBoTutoringProfileApp() {
    }

    public static Scene createScene() {

        final UserAccount user = CurrentSession.getUser();

        final VBox root = new VBox();
        root.setBackground(new Background(
            new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        root.getChildren().addAll(
            createHeader(user),
            createContent(user)
        );

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

        final Label userName = new Label(
            user.getName() + " " + user.getSurname()
        );

        userName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userName.setTextFill(TEXT_DARK);

        final Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setPrefHeight(16);

        final Button dashboardButton = new Button("Dashboard");

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

    private static VBox createContent(final UserAccount user) {

        final VBox content = new VBox(20);

        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        final Label pageTitle = new Label("Profilo Utente");

        pageTitle.setFont(
            Font.font("System", FontWeight.EXTRA_BOLD, 34)
        );

        pageTitle.setTextFill(TEXT_DARK);

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

        content.getChildren().addAll(
            pageTitle,
            card
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
}