package it.unibo.samplejavafx.mvcexample.handler;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class CloseStageActionListener implements EventHandler<ActionEvent> {
    private final Stage stage;

    public CloseStageActionListener(final Stage stage) {
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent event) {
        this.stage.close();
    }
}
