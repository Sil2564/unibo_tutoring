package it.unibo.samplejavafx.stages;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class AnotherStage extends Stage {
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    AnotherStage() {
        super();
        setTitle("New stage created at " + System.currentTimeMillis());
        final VBox pane = new VBox();
        pane.getChildren().add(new Label("First label"));
        pane.getChildren().add(new Label("Second label"));
        setScene(new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT));
    }
}