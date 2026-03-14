package it.unibo.samplejavafx.concurrency.gameloop.core;

import it.unibo.samplejavafx.concurrency.Flag;
import it.unibo.samplejavafx.concurrency.SynchValue;

public abstract class GameLoop<I, O> implements Runnable {
    private final SynchValue<I> inputBuffer;
    private final SynchValue<O> outputBuffer;
    private final Flag running = new Flag(true);
    private final long expectedFrameTimeMillis;
    public GameLoop(long expectedFrameTimeMillis) {
        this.inputBuffer = new SynchValue<>();
        this.outputBuffer = new SynchValue<>();
        this.expectedFrameTimeMillis = expectedFrameTimeMillis;
    }
    @Override
    public void run() {
        while(running.getValue()) {
            var start = System.currentTimeMillis();
            processInput();
            updateGame();
            waitForNextFrame(start);
        }
    }

    private void processInput() {
        var input = this.inputBuffer.tryGetValue();
        input.ifPresent(this::processInput);
    }

    private void updateGame() {
        var result = logic(expectedFrameTimeMillis);
        this.outputBuffer.setValue(result);
    }

    private void waitForNextFrame(long startTime) {
        var end = System.currentTimeMillis();
        var frameTime = end - startTime;
        if (frameTime < expectedFrameTimeMillis) {
            try {
                Thread.sleep(expectedFrameTimeMillis - frameTime);
            } catch (InterruptedException _) { }
        }
    }

    protected abstract void processInput(I input);

    protected abstract O logic(float deltaTimeMillis);

    public void sendInput(I input) {
        this.inputBuffer.setValue(input);
    }

    public SynchValue<O> getOutputBuffer() {
        return outputBuffer;
    }

    public void stop() {
        this.running.setValue(false);
    }
}
