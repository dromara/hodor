package org.dromara.hodor.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

/**
 * job execution context
 *
 * @author tomgs
 * @since 2021/3/1
 */
@Getter
@AllArgsConstructor
public class JobExecutionContext {

    private final Logger logger;

    private final JobParameter jobParameter;

    @Override
    public String toString() {
        return "JobExecutionContext{" +
            "logger=" + logger +
            ", jobParameter=" + jobParameter +
            '}';
    }
}
