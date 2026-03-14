package it.unibo.samplejavafx.mvcexample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.stage.Stage;

/**
 * JavaFX application for the "draw number" game.
 */
public final class DrawNumberFX extends Application implements DrawNumberViewObserver {
    private static final int MAX_ATTEMPTS = 5;
    private static final int MIN_NUMBER = 10;
    private static final int MAX_NUMBER = 100;
    private DrawNumberObservable model;
    private List<DrawNumberView> views;

    @Override
    public void init() {
        final Parameters params = getParameters();
        final Configuration configuration = new Configuration(MAX_NUMBER, MIN_NUMBER, MAX_ATTEMPTS);
        model = new DrawNumberImpl(configuration);
        views = new ArrayList<>();
        views.addAll(Arrays.asList(
            new DrawNumberViewImpl(model, new BoundingBox(0, 0, 0, 0)),
            new DrawNumberViewImpl(model, null)
        ));
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        for (final DrawNumberView view : views) {
            view.setObserver(this);
            view.start();
        }
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view : views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view : views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        Platform.exit();
    }

    public static void run(final String... args) {
        launch(args);
    }
}
