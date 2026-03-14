package it.unibo.samplejavafx.mvcexample.handler;

import it.unibo.samplejavafx.mvcexample.DrawNumberViewObserver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ResetButtonActionHandler implements EventHandler<ActionEvent> {
    private final DrawNumberViewObserver observer;

    public ResetButtonActionHandler(final DrawNumberViewObserver observer) {
        this.observer = observer;
    }

    @Override
    public void handle(ActionEvent event) {
        observer.resetGame();
    }
}
