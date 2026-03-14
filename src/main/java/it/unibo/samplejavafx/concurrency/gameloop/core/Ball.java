package it.unibo.samplejavafx.concurrency.gameloop.core;

public class Ball {
    private final Vector2D position;
    private final double radius;
    private final Vector2D velocity;

    public Ball(double x, double y, double radius, double dx, double dy) {
        this.position = new Vector2D(x, y);
        this.radius = radius;
        this.velocity = new Vector2D(dx, dy);
    }

    public Ball(Vector2D position, double radius, Vector2D velocity) {
        this.position = position;
        this.radius = radius;
        this.velocity = velocity;
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Ball updateVelocity(Vector2D newVelocity) {
        return new Ball(this.position, this.radius, newVelocity);
    }

    public Ball updatePosition(double deltaTime) {
        return new Ball(this.position.add(this.velocity.multiply(deltaTime)), this.radius, this.velocity);
    }
}
