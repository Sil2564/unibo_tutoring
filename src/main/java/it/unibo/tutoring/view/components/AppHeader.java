package it.unibo.tutoring.view.components;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UniBoTutoringProfileApp;
import it.unibo.tutoring.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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

public class AppHeader extends HBox {

    private static final Color TEXT_DARK = Color.web("#1B1B1B");

    public AppHeader() {
        this(UserSession.getDisplayName(), null, false);
    }

    public AppHeader(final String userDisplayName, final Runnable onLogout) {
        this(userDisplayName, onLogout, false);
    }

    public static AppHeader forProfile(final String userDisplayName) {
        return new AppHeader(userDisplayName, null, true);
    }

    private AppHeader(
            final String userDisplayName,
            final Runnable onLogout,
            final boolean profileView) {
        super(12);

        this.getStyleClass().add("app-header");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10, 18, 10, 18));
        this.setPrefHeight(64);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(new Border(new BorderStroke(Color.web("#D6D6D6"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        final ImageView logo = new AppIcon("logo.png", 30, 30);

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

        final Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setPrefHeight(16);

        final HBox rightSide;
        if (profileView) {
            final Label userName = new Label(userDisplayName);
            userName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
            userName.setTextFill(TEXT_DARK);
            rightSide = new HBox(10, userName, separator, new DashboardButton());
        } else {
            final ImageView userIcon = new AppIcon("user.png", 16, 16);
            final Button userName = profileButton(userDisplayName);
            rightSide = new HBox(8, userIcon, userName);
        }
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(brandBlock, spacer, rightSide);
    }

    private Button profileButton(final String userDisplayName) {
        final Button button = new Button(userDisplayName);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        button.setTextFill(TEXT_DARK);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);
        button.setCursor(Cursor.HAND);
        button.setOnAction(event -> {
            final Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(UniBoTutoringProfileApp.createScene());
            stage.setTitle("UniBo Tutoring - Profilo");
            WindowUtil.maximize(stage);
        });
        return button;
    }
}
