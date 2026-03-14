package it.unibo.samplejavafx.concurrency.gameloop;

import it.unibo.samplejavafx.concurrency.gameloop.core.Ball;
import it.unibo.samplejavafx.concurrency.gameloop.core.GameLoop;
import it.unibo.samplejavafx.concurrency.gameloop.core.Vector2D;

import java.util.Set;

public class JumpingBallGame extends GameLoop<Set<Command>, Ball> {
    private static final double GRAVITY = 9.81 * 100; // pixels per second squared (approx)
    private static final double JUMP_STRENGTH = -500; // pixels per second
    private static final double MOVE_SPEED = 200;
    private static final double GROUND_Y = 500;
    private static final double BALL_RADIUS = 20;
    private static final double INITIAL_X = 400;
    private static final long FRAME_TIME = 16;
    private static final double GROUND_TOLERANCE = 1.0;

    private Ball ball;

    public JumpingBallGame() {
        super(FRAME_TIME); // ~60 FPS
        this.ball = new Ball(INITIAL_X, GROUND_Y, BALL_RADIUS, 0, 0);
    }

    @Override
    protected void processInput(Set<Command> input) {
        handleMovement(input);
        handleJump(input);
    }

    private void handleMovement(Set<Command> input) {
        double dx = 0;
        if (input.contains(Command.LEFT)) {
            dx -= MOVE_SPEED;
        }
        if (input.contains(Command.RIGHT)) {
            dx += MOVE_SPEED;
        }
        ball = ball.updateVelocity(new Vector2D(dx, ball.getVelocity().y()));
    }

    private void handleJump(Set<Command> input) {
        if (input.contains(Command.JUMP) && Math.abs(ball.getPosition().y() - GROUND_Y) < GROUND_TOLERANCE) {
             ball = ball.updateVelocity(new Vector2D(ball.getVelocity().x(), JUMP_STRENGTH));
        }
    }

    @Override
    protected Ball logic(float deltaTimeMillis) {
        double deltaTimeSeconds = deltaTimeMillis / 1000.0;
        applyGravity(deltaTimeSeconds);
        moveBall(deltaTimeSeconds);
        checkGroundCollision();
        return ball;
    }

    private void applyGravity(double deltaTimeSeconds) {
        Vector2D velocity = ball.getVelocity().add(new Vector2D(0, GRAVITY * deltaTimeSeconds));
        ball = ball.updateVelocity(velocity);
    }

    private void moveBall(double deltaTimeSeconds) {
        ball = ball.updatePosition(deltaTimeSeconds);
    }

    private void checkGroundCollision() {
        if (ball.getPosition().y() > GROUND_Y) {
            ball = new Ball(new Vector2D(ball.getPosition().x(), GROUND_Y), ball.getRadius(), new Vector2D(ball.getVelocity().x(), 0));
        }
    }
}
