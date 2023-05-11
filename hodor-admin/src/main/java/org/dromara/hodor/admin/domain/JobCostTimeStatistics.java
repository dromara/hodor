package org.dromara.hodor.admin.domain;

import lombok.Data;

import java.io.Serializable;


@Data
public class JobCostTimeStatistics implements Serializable {

    private static final long serialVersionUID = 4242455475576975495L;

    private String jobName;//job的名称

    private int costTime;//耗时

}

