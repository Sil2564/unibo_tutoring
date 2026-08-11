package it.unibo.tutoring.view.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

class SharedComponentsTest {

    @BeforeAll
    static void initJavaFx() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (final IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not start");
    }

    @Test
    void appIconAppliesTheStandardImageConfiguration() {
        final AppIcon icon = new AppIcon("logo.png", 30, 24);

        assertEquals(30, icon.getFitWidth(), 0.001);
        assertEquals(24, icon.getFitHeight(), 0.001);
        assertTrue(icon.isPreserveRatio());
        assertTrue(icon.isSmooth());
    }

    @Test
    void appCardAppliesSharedGeometryAndAppearance() {
        final AppCard card = new AppCard(10, new Insets(24), 12).withWidth(500);

        assertEquals(10, card.getSpacing(), 0.001);
        assertEquals(new Insets(24), card.getPadding());
        assertEquals(500, card.getPrefWidth(), 0.001);
        assertFalse(card.getBackground().getFills().isEmpty());
        assertFalse(card.getBorder().getStrokes().isEmpty());
    }

    @Test
    void buttonsAndFormControlsUseTheSharedStyles() {
        final AppButton primary = AppButton.primary("Salva");
        final AppButton secondary = AppButton.secondary("Annulla");
        final TextField field = FormControlStyle.apply(new TextField());

        assertTrue(primary.getStyleClass().contains("primary-btn"));
        assertEquals(Color.WHITE, primary.getTextFill());
        assertFalse(secondary.getBorder().getStrokes().isEmpty());
        assertTrue(field.getStyleClass().contains("form-field"));
        assertEquals(38, field.getPrefHeight(), 0.001);
    }

    @Test
    void profileHeaderUsesTheSharedDashboardButton() {
        final AppHeader header = AppHeader.forProfile("Mario Rossi");

        assertTrue(containsNodeOfType(header, DashboardButton.class));
    }

    @Test
    void footerKeepsTheSharedStyleAndStructure() {
        final AppFooter footer = new AppFooter();

        assertTrue(footer.getStyleClass().contains("app-footer"));
        assertEquals(2, footer.getChildren().size());
    }

    private static boolean containsNodeOfType(final Node root, final Class<? extends Node> type) {
        if (type.isInstance(root)) {
            return true;
        }
        if (root instanceof Pane pane) {
            return pane.getChildren().stream().anyMatch(child -> containsNodeOfType(child, type));
        }
        return false;
    }
}
