package org.dromara.hodor.core.service;

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

}
