package org.dromara.hodor.actuator.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

/**
 * job execution context
 *
 * @author tomgs
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class JobExecutionContext {

    private final Logger jobLogger;

    private final JobParameter jobParameter;

    @Override
    public String toString() {
        return "JobExecutionContext{" +
            "jobLogger=" + jobLogger +
            ", jobParameter=" + jobParameter +
            '}';
    }
}
