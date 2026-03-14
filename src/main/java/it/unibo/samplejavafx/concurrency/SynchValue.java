package it.unibo.samplejavafx.concurrency;

import java.util.Optional;

public class SynchValue<V> {
    private V value = null;

    public synchronized void setValue(V value) {
        this.value = value;
        this.notifyAll();
    }

    public synchronized V getValue() throws InterruptedException {
        while (value == null) {
            wait();
        }
        var result = this.value;
        this.value = null;
        return result;
    }

    public synchronized Optional<V> tryGetValue() {
        if (value == null) {
            return Optional.empty();
        }
        var result = this.value;
        this.value = null;
        return Optional.of(result);
    }
}
