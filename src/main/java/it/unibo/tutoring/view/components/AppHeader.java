package it.unibo.tutoring.view.components;

import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppHeader extends HBox {

    private static final Color TEXT_DARK = Color.web("#1B1B1B");

    public AppHeader() {
        this("Mario Rossi", null);
    }

    public AppHeader(final String userDisplayName, final Runnable onLogout) {
        super(12);

        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10, 18, 10, 18));
        this.setPrefHeight(64);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

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

        final Label userName = new Label(userDisplayName);
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
        if (onLogout != null) {
            logoutButton.setOnAction(event -> onLogout.run());
        }

        final HBox rightSide = new HBox(8, userIcon, userName, separator, logoutButton);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(brandBlock, spacer, rightSide);
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
}


