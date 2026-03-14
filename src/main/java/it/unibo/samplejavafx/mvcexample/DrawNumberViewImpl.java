package it.unibo.samplejavafx.mvcexample;

import it.unibo.samplejavafx.mvcexample.handler.GoButtonActionHandler;
import it.unibo.samplejavafx.mvcexample.handler.QuitButtonActionHandler;
import it.unibo.samplejavafx.mvcexample.handler.ResetButtonActionHandler;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Graphical {@link DrawNumberView} implementation.
 */
public final class DrawNumberViewImpl implements DrawNumberView {
    private static final String FRAME_NAME = "Draw Number App";
    private static final String QUIT = "Quit";
    private static final String RESET = "Reset";
    private static final String GO = "Go";
    private static final String NEW_GAME = ": a new game starts!";
    private DrawNumberViewObserver observer;
    private Stage frame;
    private Label messageLabel;
    private final DrawNumberObservable model;
    private final Bounds initialBounds;

    /**
     * Initialises a view implementation for a drawnumber game.
     * @param model
     * @param initialBounds
     */
    public DrawNumberViewImpl(final DrawNumberObservable model, final Bounds initialBounds) {
        this.model = model;
        this.initialBounds = initialBounds;
    }

    @Override
    public void start() {
        frame = new Stage();
        frame.setTitle(FRAME_NAME);
        if (initialBounds != null) {
            frame.setX(initialBounds.getMinX());
            frame.setY(initialBounds.getMinY());
        }
        final VBox vbox = new VBox();
        final HBox playControlsLayout = new HBox();
        final TextField inputNumber = new TextField();
        final Button goButton = new Button(GO);
        messageLabel = new Label();
        playControlsLayout.getChildren().addAll(inputNumber, goButton, messageLabel);
        final HBox gameControlsLayout = new HBox();
        final Button resetButton = new Button(RESET);
        final Button quitButton = new Button(QUIT);
        gameControlsLayout.getChildren().addAll(resetButton, quitButton);
        final Label stateMessage = new Label();
        setUpStateMessage(stateMessage.textProperty(), model);
        vbox.getChildren().addAll(playControlsLayout, gameControlsLayout, stateMessage);
        goButton.setOnAction(new GoButtonActionHandler(observer, inputNumber, frame));
        quitButton.setOnAction(new QuitButtonActionHandler(observer));
        resetButton.setOnAction(new ResetButtonActionHandler(observer));
        final int sceneWidth = 600;
        final int sceneHeight = 200;
        final Scene scene = new Scene(vbox, sceneWidth, sceneHeight);
        this.frame.setScene(scene);
        this.frame.show();
    }

    @Override
    public void setObserver(final DrawNumberViewObserver observer) {
        this.observer = observer;
    }

    @Override
    public void numberIncorrect() {
        displayError("Incorrect Number... try again");
    }

    @Override
    public void result(final DrawResult result) {
        switch (result) {
            case YOURS_HIGH, YOURS_LOW:
                plainMessage(result.getDescription());
                return;
            case YOU_WON:
                plainMessage(result.getDescription() + NEW_GAME);
                break;
            case YOU_LOST:
                plainMessage(result.getDescription() + NEW_GAME);
                break;
            default:
                throw new IllegalStateException("Unexpected result: " + result);
        }
        observer.resetGame();
    }

    private void plainMessage(final String message) {
        this.messageLabel.setText(message);
    }

    @Override
    public void displayError(final String message) {
        this.messageLabel.setText(message);
    }

    private void setUpStateMessage(Property<String> stateMessage, DrawNumberObservable model) {
        stateMessage.bind(new SimpleStringProperty("Min=")
            .concat(model.minProperty())
            .concat("; Max=")
            .concat(model.maxProperty())
            .concat("\nMaxAttempts=")
            .concat(model.attemptsProperty())
            .concat("; Remaining attempts=")
            .concat(model.remainingAttemptsProperty())
            .concat("\nLast guess:")
            .concat(model.lastGuessProperty())
            .concat("; Last outcome:")
            .concat(model.lastGuessResult())
        );
    }
}
