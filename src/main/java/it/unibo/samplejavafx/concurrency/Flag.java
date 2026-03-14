package it.unibo.samplejavafx.concurrency;

public class Flag {
    private boolean value = false;
    public Flag(boolean value) {
        this.value = value;
    }

    public synchronized void setValue(boolean value) {
        this.value = value;
    }

    public synchronized boolean getValue() {
        return this.value;
    }
}
