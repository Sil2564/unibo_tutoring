package it.unibo.tutoring.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Footer condiviso dell'applicazione. Centralizza contenuto e stile per
 * evitare differenze tra varie schermate e ripetizioni di codice.
 */
public final class AppFooter extends VBox {

    private static final Color PRIMARY_RED = Color.web("#D91E43");

    public AppFooter() {
        super(20);
        getStyleClass().add("app-footer");
        setPadding(new Insets(26, 40, 18, 40));
        setBackground(new Background(new BackgroundFill(
                PRIMARY_RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final HBox columns = new HBox(
                50,
                footerColumn(
                        "Università di Bologna",
                        "UniBo Tutoring è la piattaforma ufficiale per il supporto "
                                + "tra studenti dell'Università di Bologna presso la sede di Cesena."
                                + "\n\nVia dell'Università 50\n47521 Cesena, Italia"),
                footerColumn(
                        "Documenti",
                        "Privacy Policy\nTermini e Condizioni\nCodice di Condotta"),
                footerColumn(
                        "Contatti e Assistenza",
                        "Email di supporto:\ntutoring@unibo.it"
                                + "\n\nHai bisogno di aiuto?\nApri box assistenza"));

        final Label copyright = new Label(
                "© 2026 Università di Bologna - UniBo Tutoring. Tutti i diritti riservati.");
        copyright.setTextFill(Color.rgb(255, 255, 255, 0.94));
        copyright.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

        getChildren().addAll(columns, copyright);
    }

    private static VBox footerColumn(final String title, final String content) {
        final VBox column = new VBox(8);
        column.setPrefWidth(320);

        final Label heading = new Label(title);
        heading.setFont(Font.font("System", FontWeight.BOLD, 22));
        heading.setTextFill(Color.WHITE);

        final Label body = new Label(content);
        body.setWrapText(true);
        body.setMinHeight(Region.USE_PREF_SIZE);
        body.setTextFill(Color.rgb(255, 255, 255, 0.93));
        body.setFont(Font.font("System", FontWeight.NORMAL, 13));

        column.getChildren().addAll(heading, body);
        return column;
    }
}
