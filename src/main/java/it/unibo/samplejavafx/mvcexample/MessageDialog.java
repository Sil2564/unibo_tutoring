package it.unibo.samplejavafx.mvcexample;

import java.util.concurrent.CompletableFuture;

import it.unibo.samplejavafx.mvcexample.handler.CloseStageActionListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Utility classes for message dialogs.
 */
public final class MessageDialog {
    private MessageDialog() {
    }

    /**
     * Shows a message dialog.
     * @param owner the owner stage (where to center the dialog)
     * @param title the dialog title
     * @param text the content of the dialog
     */
    public static void showMessageDialog(final Stage owner, final String title, final String text) {
        final Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        final Label label = new Label(text);
        final Button closeButton = new Button("Close");
        closeButton.setOnAction(new CloseStageActionListener(stage));
        final VBox root = new VBox();
        root.getChildren().addAll(label, closeButton);
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}
