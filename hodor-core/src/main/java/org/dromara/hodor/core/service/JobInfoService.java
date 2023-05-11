package org.dromara.hodor.core.service;

import java.util.List;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.scheduler.DataInterval;

/**
 * job service
 *
 * @author tomgs
 * @since 1.0
 */
public interface JobInfoService {

    /**
     * 新增任务
     *
     * @param jobInfo 任务信息
     */
    JobInfo addJob(JobInfo jobInfo);

    /**
     * 如果不存在则新增任务，存在则跳过
     *
     * @param jobInfo 任务信息
     */
    void addJobIfAbsent(JobInfo jobInfo);

    /**
     * 检查任务是否存在
     *
     * @param jobInfo 任务信息
     * @return true 存在， false不存在
     */
    boolean isExists(JobInfo jobInfo);

    /**
     * 查询任务信息
     * @param groupName 任务组
     * @param jobName 任务名称
     * @return 任务信息
     */
    JobInfo queryJobByKey(String groupName, String jobName);

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
     * 获取指定位置的任务
     *
     * @param dataInterval 数据区间范围
     * @param jobStatus 任务状态
     * @return 任务列表
     */
    List<JobInfo> queryJobInfoByDataInterval(DataInterval dataInterval, JobStatus jobStatus);

    /**
     * 获取指定位置可运行的任务
     *
     * @param dataInterval 数据区间范围
     * @return running job info list
     */
    List<JobInfo> queryRunningJobInfoByDataInterval(DataInterval dataInterval);

    /**
     * 获取指定位置Ready状态的任务
     *
     * @param dataInterval 数据区间范围
     * @return ready job info list
     */
    List<JobInfo> queryReadyJobInfoByDataInterval(DataInterval dataInterval);

    /**
     * 更新任务状态
     *
     * @param jobInfo 任务信息
     * @param jobStatus 任务状态
     */
    void updateJobStatus(JobInfo jobInfo, JobStatus jobStatus);

    /**
     * 是否正在运行的任务
     * @param jobInfo 任务信息
     * @return true正在运行，false为正在运行
     */
    boolean isRunningJob(JobInfo jobInfo);

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 更新任务
     * @param jobInfo 任务信息
     * @return 更新后的任务信息
     */
    JobInfo update(JobInfo jobInfo);

    JobInfo queryById(Long id);

    PageInfo<JobInfo> queryByPage(JobInfo jobInfo, Integer pageNo, Integer pageSize);
}
