package duber.engine.utilities;

public class Timer {
    private double lastRecordedTime;

    public Timer() {
        lastRecordedTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000000000.0;
    }

    public float getElapsedTime() {
        double currTime = getTime();
        return (float) (currTime - lastRecordedTime);
    }

    public void updateTime() {
        lastRecordedTime = getTime();
    }

    public float getElapsedTimeAndUpdate() {
        double currTime = getTime();
        float elapsed = (float) (currTime - lastRecordedTime);
        lastRecordedTime = currTime;
        return elapsed;
    }

    public double getLastLoopTime() {
        return lastRecordedTime;
    }

    public boolean secondHasPassed() {
        double currTime = getTime();
        return currTime - lastRecordedTime >= 1.0;
    }
}