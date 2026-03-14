package it.unibo.samplejavafx.mvcexample;


/**
 * Encapsulates the concept of configuration.
 */
public final class Configuration {
    private final int max; 
    private final int min;
    private final int attempts;

    public Configuration(final int max, final int min, final int attempts) {
        this.max = max;
        this.min = min;
        this.attempts = attempts;
    }

    /**
     * @return the maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * @return the minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * @return the number of attempts
     */
    public int getAttempts() {
        return attempts;
    }
}

