package it.unibo.tutoring.view.box;

import javafx.scene.Scene;

public final class CreateAnnouncementViewApp {

    private CreateAnnouncementViewApp() {
    }

    public static Scene createScene() {

        return new Scene(
            new javafx.scene.layout.VBox(),
            1000,
            700
        );
    }
}