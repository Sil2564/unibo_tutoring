package it.unibo.samplejavafx.bounds;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Sample JavaFX application for playing with bounds.
 */
public final class PlayingWithBounds extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final VBox vbox = new VBox(); 
        final BoundManagerFields fields = new BoundManagerFields();
        fields.addElementsTo(vbox);
        final Button go = new Button("Go!");
        vbox.getChildren().add(go);
        go.setOnAction(new PopupStageHandler(fields));
        primaryStage.setScene(new Scene(vbox));
        primaryStage.setTitle("Playing with bounds");
        primaryStage.show();
    }

    public static void run(final String... args) {
        launch(args);
    }
}
