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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JumpingBallApp extends Application {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;
    private static final double TEXT_X = 10;
    private static final double TEXT_Y = 20;

    private final Set<Command> pressedKeys = new HashSet<>();
    private JumpingBallGame game;
    private RenderLoop<Ball> renderLoop;

    @Override
    public void start(Stage primaryStage) {
        setupGame();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = setupScene(canvas);
        setupInputHandlers(scene);
        primaryStage.setTitle("Jumping Ball Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        startRenderLoop(gc);
    }

    private void setupGame() {
        game = new JumpingBallGame();
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private Scene setupScene(Canvas canvas) {
        StackPane root = new StackPane(canvas);
        return new Scene(root, WIDTH, HEIGHT);
    }

    private void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            mapInput(e.getCode()).ifPresent(pressedKeys::add);
            game.sendInput(new HashSet<>(pressedKeys));
        });
        scene.setOnKeyReleased(e -> {
            mapInput(e.getCode()).ifPresent(pressedKeys::remove);
            game.sendInput(new HashSet<>(pressedKeys));
        });
    }

    private void startRenderLoop(GraphicsContext gc) {
        renderLoop = new RenderLoop<>(game.getOutputBuffer(), ball -> render(gc, ball));
        new Thread(renderLoop).start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        game.stop();
        renderLoop.stopLoop();
    }

    private Optional<Command> mapInput(KeyCode code) {
        return switch (code) {
            case LEFT -> java.util.Optional.of(Command.LEFT);
            case RIGHT -> java.util.Optional.of(Command.RIGHT);
            case SPACE -> java.util.Optional.of(Command.JUMP);
            default -> java.util.Optional.empty();
        };
    }

    private void render(GraphicsContext gc, Ball ball) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.BLUE);
        gc.fillOval(ball.getPosition().x() - ball.getRadius(), ball.getPosition().y() - ball.getRadius(), ball.getRadius() * 2, ball.getRadius() * 2);
        gc.setFill(Color.BLACK);
        gc.fillText("Press SPACE to jump, LEFT/RIGHT to move", TEXT_X, TEXT_Y);
    }

    static class Main {
        static void main(String[] args) {
            Application.launch(JumpingBallApp.class, args);
        }
    }
}
