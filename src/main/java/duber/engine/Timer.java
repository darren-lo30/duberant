package duber.engine;

public class Timer {
    private double lastRecordedTime;

    public void init() {
        lastRecordedTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000000000.0;
    }

    public float getElapsedTime() {
        double currTime = getTime();
        float elapsed =  (float) (currTime - lastRecordedTime);
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