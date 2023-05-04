package org.dromara.hodor.admin.domain;

import lombok.Data;

import java.io.Serializable;


@Data
public class CostTimeRankJob implements Serializable {

    private String jobName;//job的名称

    private int costTime;//耗时

}

