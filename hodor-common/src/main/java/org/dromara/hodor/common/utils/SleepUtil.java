package org.dromara.hodor.common.utils;

/**
 * @author tomgs
 * @since 2020/7/6
 */
public final class SleepUtil {

    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
