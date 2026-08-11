package it.unibo.tutoring.view.components;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** Pulsante condiviso con varianti primaria e secondaria. */
public class AppButton extends Button {

    private static final Color PRIMARY_RED = Color.web("#D91E43");
    private static final Color TEXT_DARK = Color.web("#1B1B1B");

    public AppButton() {
        this("");
    }

    public AppButton(final String text) {
        super(text);
        setCursor(Cursor.HAND);
    }

    public static AppButton primary(final String text) {
        return primary(text, PRIMARY_RED);
    }

    public static AppButton primary(final String text, final Color color) {
        return new AppButton(text).asPrimary(color);
    }

    public static AppButton secondary(final String text) {
        return new AppButton(text).asSecondary();
    }

    public AppButton asPrimary(final Color color) {
        getStyleClass().remove("primary-btn");
        getStyleClass().add("primary-btn");
        setFont(Font.font("System", FontWeight.EXTRA_BOLD, 13));
        setTextFill(Color.WHITE);
        setPadding(new Insets(9, 18, 9, 18));
        setBackground(new Background(new BackgroundFill(
                color, new CornerRadii(8), Insets.EMPTY)));
        setBorder(Border.EMPTY);
        return this;
    }

    public AppButton asSecondary() {
        getStyleClass().remove("primary-btn");
        setFont(Font.font("System", FontWeight.EXTRA_BOLD, 13));
        setTextFill(TEXT_DARK);
        setPadding(new Insets(9, 18, 9, 18));
        setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                Color.web("#CFCFCF"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                BorderWidths.DEFAULT)));
        return this;
    }
}
