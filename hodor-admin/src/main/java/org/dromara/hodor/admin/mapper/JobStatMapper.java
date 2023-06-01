package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.hodor.admin.domain.JobStatusStatistics;
import org.dromara.hodor.admin.domain.JobCostTimeStatistics;

import java.util.Date;
import java.util.List;

/**
 * 任务状态统计Mapper
 */
@Mapper
public interface JobStatMapper {

    /**
     * 获取任务堆积数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param groupName 组名
     * @return 堆积数量大小
     */
    int getAccumulateJob(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("groupName") String groupName);

    /**
     * 获取未接受的任务数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param groupName 组名
     * @return 未接受数量大小
     */
    int getUnAcceptedJob(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("groupName") String groupName);

    /**
     * 获取正常调度的任务数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param groupName 组名
     * @return 正常调度数量大小
     */
    int getNormalJob(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("groupName") String groupName);

    /**
     * 获取任务耗时排名
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param groupName 组名
     * @return 任务耗时排名列表
     */
    List<JobCostTimeStatistics> getRankConsumerTimeJob(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("groupName") String groupName);

    /**
     * 获取job的状态统计信息
     *
     * @return job的状态信息
     */
    List<JobStatusStatistics> getJobStatusStatistic();

    /**
     * 获取一段时间的job的执行信息统计数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return job的执行信息统计数据
     */
    List<JobStatusStatistics> getBasicJobStatusStatistic(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}

