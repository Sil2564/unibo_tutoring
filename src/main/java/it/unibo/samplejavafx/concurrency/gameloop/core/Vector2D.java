package it.unibo.samplejavafx.concurrency.gameloop.core;

public record Vector2D(double x, double y) {
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }
    public Vector2D divide(double scalar) {
        return new Vector2D(this.x / scalar, this.y / scalar);
    }
}
