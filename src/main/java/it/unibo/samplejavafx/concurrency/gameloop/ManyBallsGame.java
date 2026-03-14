package it.unibo.samplejavafx.concurrency.gameloop;

import it.unibo.samplejavafx.concurrency.gameloop.core.Ball;
import it.unibo.samplejavafx.concurrency.gameloop.core.GameLoop;
import it.unibo.samplejavafx.concurrency.gameloop.core.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManyBallsGame extends GameLoop<String, List<Ball>> {
    private static final int INITIAL_BALLS = 10;
    private static final double MIN_RADIUS = 10;
    private static final double RADIUS_VARIANCE = 20;
    private static final double MAX_SPEED = 200;
    private static final long FRAME_TIME = 16;

    private List<Ball> balls;
    private final double width;
    private final double height;
    private final Random random = new Random();

    public ManyBallsGame(double width, double height) {
        super(FRAME_TIME);
        this.width = width;
        this.height = height;
        balls = new ArrayList<>();
        for (int i = 0; i < INITIAL_BALLS; i++) {
            balls.add(createRandomBall());
        }
    }

    private Ball createRandomBall() {
        return new Ball(
            random.nextDouble() * width,
            random.nextDouble() * height,
            MIN_RADIUS + random.nextDouble() * RADIUS_VARIANCE,
            (random.nextDouble() - 0.5) * MAX_SPEED,
            (random.nextDouble() - 0.5) * MAX_SPEED
        );
    }

    @Override
    protected void processInput(String input) {
        if ("ADD".equals(input)) {
             addNewBall();
        }
    }

    private void addNewBall() {
        balls.add(new Ball(
            width / 2,
            height / 2,
            MIN_RADIUS + random.nextDouble() * RADIUS_VARIANCE,
            (random.nextDouble() - 0.5) * MAX_SPEED,
            (random.nextDouble() - 0.5) * MAX_SPEED
        ));
    }

    @Override
    protected List<Ball> logic(float deltaTimeMillis) {
        double deltaTimeSeconds = deltaTimeMillis / 1000.0;
        updateBalls(deltaTimeSeconds);
        return balls;
    }

    private void updateBalls(double deltaTimeSeconds) {
        List<Ball> nextBalls = new ArrayList<>();
        for (Ball ball : balls) {
            Ball nextBall = ball.updatePosition(deltaTimeSeconds);
            nextBalls.add(bounceBall(nextBall));
        }
        balls = nextBalls;
    }

    private Ball bounceBall(Ball ball) {
        double x = ball.getPosition().x();
        double y = ball.getPosition().y();
        double dx = ball.getVelocity().x();
        double dy = ball.getVelocity().y();
        double r = ball.getRadius();
        if (x < r || x > width - r) {
            dx = -dx;
            x = Math.max(r, Math.min(x, width - r));
        }
        if (y < r || y > height - r) {
            dy = -dy;
            y = Math.max(r, Math.min(y, height - r));
        }
        return new Ball(new Vector2D(x, y), r, new Vector2D(dx, dy));
    }
}
