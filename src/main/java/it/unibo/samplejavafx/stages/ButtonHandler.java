package it.unibo.samplejavafx.stages;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class ButtonHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent mouseEvent) {
        new AnotherStage().show();
    }
}
