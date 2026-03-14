package it.unibo.samplejavafx.mvcexample.handler;

import it.unibo.samplejavafx.mvcexample.DrawNumberViewObserver;
import it.unibo.samplejavafx.mvcexample.MessageDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GoButtonActionHandler implements EventHandler<ActionEvent> {
    private final DrawNumberViewObserver observer;
    private final TextField inputNumber;
    private final Stage frame;

    public GoButtonActionHandler(final DrawNumberViewObserver observer, final TextField inputNumber, final Stage frame) {
        this.observer = observer;
        this.inputNumber = inputNumber;
        this.frame = frame;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            observer.newAttempt(Integer.parseInt(inputNumber.getText()));
        } catch (NumberFormatException exception) {
            MessageDialog.showMessageDialog(frame, "Validation error",
                    "You entered " + inputNumber.getText() + ". Provide an integer please...");
        }
    }
}
