package org.dromara.hodor.common.loadbalance;

import java.util.Collection;
import java.util.Random;
import org.dromara.hodor.common.Host;

/**
 *
 * @author tomgs
 * @since 1.0
 */
public class RandomLoadBalance implements LoadBalance {

    private final Random random = new Random();

    @Override
    public Host select(Collection<Host> source) {
        if (source == null || source.size() == 0) {
            throw new IllegalArgumentException("Empty source.");
        }

        if (source.size() == 1) {
            return (Host) source.toArray()[0];
        }

        int size = source.size();
        int randomIndex = random.nextInt(size);

        return (Host) source.toArray()[randomIndex];
    }

}
