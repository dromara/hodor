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
     * 新增任务
     *
     * @param jobInfo 任务信息
     */
    void addJob(JobInfo jobInfo);

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
    Long queryJobHashIdByOffset(Integer offset);

    /**
     * 获取指定位置任务的id
     *
     * @param offset 任务位置
     * @return hash id
     */
    Long queryJobIdByOffset(Integer offset);

    /**
     * 获取指定位置数据
     *
     * @param startHashId 起始位置
     * @param endHashId 结束位置
     * @return job info list
     */
    List<JobInfo> queryJobInfoByHashIdOffset(Long startHashId, Long endHashId);
}
