package it.unibo.samplejavafx.concurrency;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UnresponsiveUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button("Calcola");
        TextField result = new TextField();
        btn.setOnAction(event -> {
            long sum = calculateSum();
            result.setText("Somma: " + sum);
        });
        VBox root = createUI(btn, result);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Esempio JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private long calculateSum() {
        long sum = 0;
        for (long i = 0; i < 1_000_000_000_0L; i++) { sum ++; }
        return sum;
    }

    private VBox createUI(Button btn, TextField result) {
        VBox root = new VBox();
        root.getChildren().addAll(btn, result);
        return root;
    }

    public static class Main {
        static void main(String... args) {
            Application.launch(UnresponsiveUI.class, args);
        }
    }
}
