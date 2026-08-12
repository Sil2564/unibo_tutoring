package it.unibo.tutoring.view.components;

import java.nio.file.Path;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Icona standard dell'applicazione. Centralizza caricamento, dimensionamento
 * e qualita' delle immagini usate nelle diverse schermate.
 */
public final class AppIcon extends ImageView {

    private static final Path ICONS_DIRECTORY = Path.of("src", "icons");

    public AppIcon(final String iconPath, final double width, final double height) {
        super(load(iconPath));
        setFitWidth(width);
        setFitHeight(height);
        setPreserveRatio(true);
        setSmooth(true);
    }

    public static Image load(final String iconPath) {
        final Path path = iconPath.contains("/") || iconPath.contains("\\")
                ? Path.of(iconPath)
                : ICONS_DIRECTORY.resolve(iconPath);
        return new Image(path.toUri().toString());
    }
}
