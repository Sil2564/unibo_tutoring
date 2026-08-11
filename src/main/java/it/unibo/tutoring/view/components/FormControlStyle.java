package it.unibo.tutoring.view.components;

import javafx.scene.control.Control;

/** Configurazione comune dei controlli usati nei form. */
public final class FormControlStyle {

    private static final double DEFAULT_HEIGHT = 38;

    private FormControlStyle() {
    }

    public static <T extends Control> T apply(final T control) {
        return apply(control, DEFAULT_HEIGHT);
    }

    public static <T extends Control> T apply(final T control, final double height) {
        control.setPrefHeight(height);
        control.setMaxWidth(Double.MAX_VALUE);
        if (!control.getStyleClass().contains("form-field")) {
            control.getStyleClass().add("form-field");
        }
        return control;
    }

    public static <T extends Control> T applyOutlined(final T control) {
        apply(control);
        control.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: #CFCFCF;"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-family: 'System';"
                        + "-fx-font-size: 13px;");
        return control;
    }
}
