package it.unibo.samplejavafx.unibotutoring;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UniBoTutoringStatisticApp {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color PAGE_BG = Color.web("#EFEFEF");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");

    public static Scene createScene() {
        final VBox root = new VBox();
        root.setBackground(new Background(new BackgroundFill(PAGE_BG, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setAlignment(Pos.CENTER);

        final Label title = new Label("Statistiche");
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 44));
        title.setTextFill(TEXT_DARK);

        final Label subtitle = new Label("Le tue statistiche di tutoraggio verranno visualizzate qui");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.web("#6A6A6A"));

        root.getChildren().addAll(title, subtitle);

        return new Scene(root, 1320, 920);
    }
}
