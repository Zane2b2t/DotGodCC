package me.pignol.swift.api.util.objects;

public class StopWatch {

    private long current;

    public StopWatch() {
        this.current = System.currentTimeMillis();
    }
    public boolean passedS(double s) {
        return getMs(System.nanoTime() - this.current) >= ((long) (s * 1000.0));
    }
    public boolean passedMs(long ms) {
        return getMs(System.nanoTime() - this.current) >= ms;
    }
    public long getMs(long time) {
        return time / 1000000;
    }

    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public void reset() {
        this.current = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - this.current;
    }

    public long getCurrent() {
        return current;
    }

    public boolean sleep(final long time) {
        if (getTime() >= time) {
            reset();
            return true;
        }
        return false;
    }


    public void setMs(long ms) {
        current = System.currentTimeMillis() - ms;
    }


}
