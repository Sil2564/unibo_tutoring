package it.unibo.samplejavafx.mvcexample;

/**
 * Controller interface, namely the entity which receives events from the view
 * and acts on the model.
 */
public interface DrawNumberViewObserver {

    /**
     * Makes a guess.
     * 
     * @param n the attempt
     */
    void newAttempt(int n);

    /**
     * Resets the current game (if any is running) and starts a new one.
     */
    void resetGame();

    /**
     * Gracefully quits from the application.
     */
    void quit();
}
