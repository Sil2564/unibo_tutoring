package it.unibo.samplejavafx.stages;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Sample JavaFX application with more than on stage.
 */
public class JavaFXAppMoreStages extends Application {

    @Override
    public final void start(final Stage primaryStage) throws Exception {
        final Button button = new Button("Create a new stage!");
        button.setFont(new Font(100));
        button.setOnMouseClicked(new ButtonHandler());
        primaryStage.setScene(new Scene(button));
        primaryStage.setTitle("Hello");
        primaryStage.show();
    }

    public static void run(final String[] args) {
        launch(args);
    }
}
