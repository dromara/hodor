package org.dromara.hodor.common.loadbalance;

import java.util.Collection;
import org.dromara.hodor.common.Host;

/**
 * load balance
 *
 * @author tomgs
 * @since 1.0
 */
public interface LoadBalance {

    Host select(Collection<Host> nodes);

}
