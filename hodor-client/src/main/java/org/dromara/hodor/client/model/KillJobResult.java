package org.dromara.hodor.client.model;

import lombok.Data;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * @author superlit
 * @create 2023/7/28 16:12
 */
@Data
public class KillJobResult {

    private JobExecuteStatus status;

    private String completeTime;
}
