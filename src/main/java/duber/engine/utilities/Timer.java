package duber.engine.utilities;

/**
 * A system timer.
 * @author Darren Lo
 * @version 1.0
 */
public class Timer {
    /** The last recorded time */
    private double lastRecordedTime;

    /**
     * Constructs a Timer.
     */
    public Timer() {
        lastRecordedTime = getTime();
    }

    /**
     * Gets the current time in seconds.
     * @return the current time in seconds
     */
    public double getTime() {
        return System.nanoTime() / 1000000000.0;
    }

    /**
     * Gets the elapsed time in seconds since the last update.
     * @return the elapsed time in seconds.
     */
    public float getElapsedTime() {
        double currTime = getTime();
        return (float) (currTime - lastRecordedTime);
    }

    /**
     * Updates the time with the current time.
     */
    public void updateTime() {
        lastRecordedTime = getTime();
    }

    /**
     * Gets the elapsed time in seconds since the last update and then updates.
     * @return the elapsed time in seconds
     */
    public float getElapsedTimeAndUpdate() {
        double currTime = getTime();
        float elapsed = (float) (currTime - lastRecordedTime);
        lastRecordedTime = currTime;
        return elapsed;
    }

    /**
     * The time recoreded at the most recent update.
     * @return the last recorded time
     */
    public double getLastRecordedTime() {
        return lastRecordedTime;
    }

    /**
     * Determines if a second has passed since the last update.
     * @return if a second has passed.
     */
    public boolean secondHasPassed() {
        double currTime = getTime();
        return currTime - lastRecordedTime >= 1.0;
    }
}