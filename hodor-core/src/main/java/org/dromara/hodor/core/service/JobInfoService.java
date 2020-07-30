package org.dromara.hodor.core.service;

import java.util.List;
import org.dromara.hodor.core.entity.JobInfo;

/**
 * job service
 *
 * @author tomgs
 * @since 2020/6/30
 */
public interface JobInfoService {

    /**
     * 查询可分配任务数量
     *
     * @return 可分配任务数量
     */
    Integer queryAssignableJobCount();

    /**
     * 获取指定位置任务的hashId
     *
     * @param offset 任务位置
     * @return hash id
     */
    Integer queryJobHashIdByOffset(Integer offset);

    /**
     * 获取指定位置数据
     *
     * @param start 起始位置
     * @param end 结束位置
     * @return job info list
     */
    List<JobInfo> queryJobInfoByOffset(Integer start, Integer end);
}
