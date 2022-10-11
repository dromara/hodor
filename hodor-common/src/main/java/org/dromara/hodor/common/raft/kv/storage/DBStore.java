package org.dromara.hodor.common.raft.kv.storage;

import com.codahale.metrics.Timer;
import org.dromara.hodor.common.metrics.KVMetrics;
import org.dromara.hodor.common.raft.kv.core.KVOperate;

import static org.dromara.hodor.common.metrics.KVMetricNames.DB_TIMER;

/**
 * DBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
public interface DBStore extends KVOperate {

    static Timer.Context getTimeContext(final String opName) {
        return KVMetrics.timer(DB_TIMER, opName).time();
    }

    void init();

}
