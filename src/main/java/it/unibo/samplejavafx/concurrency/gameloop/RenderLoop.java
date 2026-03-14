package it.unibo.samplejavafx.concurrency.gameloop;

import it.unibo.samplejavafx.concurrency.Flag;
import it.unibo.samplejavafx.concurrency.SynchValue;
import javafx.application.Platform;

import java.util.function.Consumer;

public class RenderLoop<T> implements Runnable {
    private final SynchValue<T> buffer;
    private final Consumer<T> renderer;
    private final Flag running = new Flag(true);

    public RenderLoop(SynchValue<T> buffer, Consumer<T> renderer) {
        this.buffer = buffer;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        while (running.getValue()) {
            try {
                T state = buffer.getValue();
                Platform.runLater(() -> renderer.accept(state));
            } catch (InterruptedException e) {
                running.setValue(false);
            }
        }
    }

    public void stopLoop() {
        running.setValue(false);
    }
}
