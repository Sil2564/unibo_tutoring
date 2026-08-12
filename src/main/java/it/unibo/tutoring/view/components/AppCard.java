package it.unibo.tutoring.view.components;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/** Card bianca e bordata condivisa dalle principali viste dell'applicazione. */
public final class AppCard extends VBox {

    private static final Color BORDER_COLOR = Color.web("#D6D6D6");

    public AppCard(final double spacing, final Insets padding, final double radius) {
        this(spacing, padding, radius, BORDER_COLOR);
    }

    public AppCard(
            final double spacing,
            final Insets padding,
            final double radius,
            final Color borderColor) {
        super(spacing);
        setPadding(padding);
        setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(radius), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                borderColor,
                BorderStrokeStyle.SOLID,
                new CornerRadii(radius),
                BorderWidths.DEFAULT)));
    }

    public AppCard withWidth(final double width) {
        setMinWidth(0);
        setPrefWidth(width);
        setMaxWidth(width);
        return this;
    }

    public AppCard withMaxWidth(final double width) {
        setMaxWidth(width);
        return this;
    }
}
