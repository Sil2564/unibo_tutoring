package it.unibo.tutoring.view.components;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Utility per uniformare l'aspetto e il comportamento delle finestre in tutta
 * l'applicazione: ogni pagina deve aprirsi a schermo intero (finestra
 * massimizzata) e deve avere lo stesso comportamento di scorrimento, ovvero
 * scroll verticale quando il contenuto non entra nell'altezza disponibile e
 * mai scroll/overflow orizzontale.
 */
public final class WindowUtil {

    private WindowUtil() {
    }

    /**
     * Applica a uno {@link ScrollPane} la policy di scorrimento standard
     * usata in tutte le pagine dell'applicazione: il contenuto si adatta
     * sempre alla larghezza disponibile (niente scroll orizzontale e niente
     * elementi che sbordano), mentre lo scroll verticale compare solo quando
     * serve.
     */
    public static void applyStandardScrollPolicy(final ScrollPane scrollPane) {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setBorder(Border.EMPTY);
    }

    public static void applyFullWindowScene(final Stage stage, final javafx.scene.Parent root) {
        final ScrollPane scrollPane = new ScrollPane(root);
        applyStandardScrollPolicy(scrollPane);
        final javafx.scene.Scene scene = new javafx.scene.Scene(scrollPane);
        scene.getStylesheets().add(WindowUtil.class.getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        maximize(stage);
    }

    /**
     * Porta la finestra a schermo intero (massimizzata) in modo uniforme:
     * va chiamato ogni volta che una pagina viene mostrata, cosi' tutte le
     * schermate dell'applicazione occupano sempre l'intero schermo del PC.
     */
    public static void maximize(final Stage stage) {
        if (stage == null) {
            return;
        }

        Platform.runLater(() -> {
            stage.setResizable(true);
            final Screen screen = Screen.getPrimary();
            final Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setMaximized(true);
        });
    }
}
