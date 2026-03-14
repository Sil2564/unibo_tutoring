package it.unibo.samplejavafx.mvcexample;

/**
 * Possible outcomes of a guess.
 * Here implemented as enum, but a configuration file would be MUCH more effective.
 */
public enum DrawResult {
    YOURS_LOW("Your number is too small"),
    YOURS_HIGH("Your number is too big"),
    YOU_WON("You won"),
    YOU_LOST("You lost");
    private final String message;

    DrawResult(final String message) {
        this.message = message;
    }

    /**
     * @return a description of the draw result
     */
    public String getDescription() {
        return message;
    }
}
