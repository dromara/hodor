package org.dromara.hodor.common.loadbalance;

import java.util.Collection;
import org.dromara.hodor.common.Host;

/**
 * @author tomgs
 * @since 1.0
 */
public class RoundRobinLoadBalance implements LoadBalance {

    @Override
    public Host select(Collection<Host> nodes) {
        return null;
    }

}
