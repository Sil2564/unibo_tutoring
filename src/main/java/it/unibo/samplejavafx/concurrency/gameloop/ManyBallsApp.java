package it.unibo.samplejavafx.concurrency.gameloop;

import it.unibo.samplejavafx.concurrency.gameloop.core.Ball;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class ManyBallsApp extends Application {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;
    private static final double TEXT_X = 10;
    private static final double TEXT_Y = 20;

    private ManyBallsGame game;
    private RenderLoop<List<Ball>> renderLoop;

    @Override
    public void start(Stage primaryStage) {
        setupGame();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = setupScene(canvas);
        setupInputHandlers(scene);
        primaryStage.setTitle("Many Balls Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        startRenderLoop(gc);
    }

    private void setupGame() {
        game = new ManyBallsGame(WIDTH, HEIGHT);
        Thread gameThread = new Thread(game);
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private Scene setupScene(Canvas canvas) {
        StackPane root = new StackPane(canvas);
        return new Scene(root, WIDTH, HEIGHT);
    }

    private void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                game.sendInput("ADD");
            }
        });
    }

    private void startRenderLoop(GraphicsContext gc) {
        renderLoop = new RenderLoop<>(game.getOutputBuffer(), balls -> render(gc, balls));
        new Thread(renderLoop).start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        game.stop();
        renderLoop.stopLoop();
    }

    private void render(GraphicsContext gc, List<Ball> balls) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.RED);
        for (Ball ball : balls) {
            gc.fillOval(ball.getPosition().x() - ball.getRadius(), ball.getPosition().y() - ball.getRadius(), ball.getRadius() * 2, ball.getRadius() * 2);
        }
        gc.setFill(Color.BLACK);
        gc.fillText("Press SPACE to add a ball", TEXT_X, TEXT_Y);
    }

    static class Main {
        static void main(String[] args) {
            Application.launch(ManyBallsApp.class, args);
        }
    }
}
