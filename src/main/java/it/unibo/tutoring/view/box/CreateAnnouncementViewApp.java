package it.unibo.tutoring.view.box;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UserAccount;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        final Label placeholder =
            new Label(
                "Form annuncio in costruzione..."
            );

        card.getChildren().addAll(
            sessionLabel,
            placeholder
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