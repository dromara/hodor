package org.dromara.hodor.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * stopwatch
 *
 * @author tomgs
 * @since 2021/8/31
 */
public class Stopwatch {

    private long startTimeNanos;

    private long elapsedNanos;

    public static Stopwatch create() {
        return new Stopwatch();
    }

    public void start() {
        startTimeNanos = System.nanoTime();
    }

    public void stop() {
        elapsedNanos = System.nanoTime() - this.startTimeNanos;
    }

    public long elapsedMillis() {
        return TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
    }

    public long elapsedNanos() {
        return elapsedNanos;
    }

}
