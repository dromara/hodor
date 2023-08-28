package org.dromara.hodor.admin.dto.job;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class JobCostTimeStatistics implements Serializable {

    private static final long serialVersionUID = 4242455475576975495L;

    private String groupName;

    private String jobName;

    private int costTime;

}

