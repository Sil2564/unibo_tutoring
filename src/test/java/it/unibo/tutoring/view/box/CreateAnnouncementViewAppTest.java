package it.unibo.tutoring.view.box;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

class CreateAnnouncementViewAppTest {

    @BeforeAll
    static void initJavaFx() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not start");
    }

    @Test
    void createSceneKeepsRadioButtonTextVisible() throws Exception {
        Scene scene = CreateAnnouncementViewApp.createScene();

        List<RadioButton> radios = findNodes(scene.getRoot(), RadioButton.class);

        assertEquals(2, radios.size(), "Expected two radio buttons in the create announcement form");
        assertTrue(radios.stream().anyMatch(r -> r.getText().contains("Offerta")), "The offer option should be present");
        assertTrue(radios.stream().anyMatch(r -> r.getText().contains("Richiesta")), "The request option should be present");
        assertTrue(radios.stream().allMatch(r -> !r.getText().isBlank()), "Each radio button should keep its visible label");
    }

    private static <T extends Node> List<T> findNodes(Node root, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (type.isInstance(root)) {
            result.add(type.cast(root));
        }
        if (root instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                result.addAll(findNodes(child, type));
            }
        } else if (root instanceof ScrollPane scrollPane) {
            // Lo ScrollPane non e' un Pane: il suo contenuto va letto tramite
            // getContent(), altrimenti la ricorsione si ferma qui e non trova
            // mai i nodi (es. i RadioButton) racchiusi al suo interno.
            final Node content = scrollPane.getContent();
            if (content != null) {
                result.addAll(findNodes(content, type));
            }
        }
        return result;
    }
}