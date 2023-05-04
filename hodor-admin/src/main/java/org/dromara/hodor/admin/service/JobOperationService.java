package org.dromara.hodor.admin.service;

import org.dromara.hodor.admin.domain.Job;
import org.dromara.hodor.core.entity.JobGroup;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.core.entity.JobInfo;

import java.util.HashMap;
import java.util.List;

public interface JobOperationService {

    List<Job> getJobList(String groupName) throws Exception;

    boolean addJob(JobInfo job, String username) throws Exception;

    boolean addOrUpdateJob(List<JobInfo> job, String username,
                           HashMap<String, Object> retuenValus) throws Exception;

    boolean deleteJob(Job job) throws Exception;

    boolean isBindSession(String groupName);

    boolean updateJob(JobInfo job);

    @Deprecated
    boolean updateJob(Job job);

    /**
     * 暂停
     *
     * @param job
     * @return
     */
    boolean suspendJob(Job job);

    /**
     * 恢复
     *
     * @param job
     * @return
     */
    boolean resumeJob(Job job);

    /**
     * 创建任务组
     *
     * @param group
     * @param username
     * @return
     */
    boolean createGroup(JobGroup group, String username);

    /**
     * 查询所有的group列表
     *
     * @return
     */
    List<String> getAllGroup();

    /**
     * 根据组名查询所有的job列表
     *
     * @param groupName
     * @return
     */
    List<Job> getJobListByGroup(String groupName);

    /**
     * 根据组名和job名查询具体的job列表
     *
     * @param groupName
     * @param jobName
     * @return
     */
    Job getJobByGroupAndJobName(String groupName, String jobName);

    /**
     * 根据组名和job名查询具体的job列表
     *
     * @param groupName
     * @param jobName
     * @return
     */
    JobInfo getJobConfig(String groupName, String jobName);

    /**
     * 均分job到集群slave节点
     *
     * @return 0成功 ,1集群中在线的job节点少于2
     * @throws Exception
     */
    int averageDistributeSlaveJob() throws Exception;

    List<JobGroup> getAllGroup(User user);

    boolean saveJob(JobInfo job);

    boolean bindSessionAndGroup(String sessionName, String groupName);

    boolean bindSessionAndGroup(String sessionName, String oldGroup, String newGroup);

    void bindJob(List<String> sessions, List<String> groupNames);

    void reBindJob(List<String> sessions, List<String> groupNames);

    /**
     * 获取所有的job group信息
     *
     * @return
     */
    List<JobGroup> getAllJobGroupForPageList();

    /**
     * 修改一个job节点的权重
     *
     * @param groupName 组名
     * @param jobName   job名
     * @param nodeName  节点名（主机和端口号）
     * @param act       操作类型halve减半 double 倍增
     * @return
     */
    boolean changeJobNodeWeight(String groupName, String jobName, String nodeName, String act);

    /**
     * 修改一个job节点的状态
     *
     * @param groupName 组名
     * @param jobName   job名
     * @param nodeName  节点名（主机和端口号
     * @return
     */
    boolean changeJobNodeStatus(String groupName, String jobName, String nodeName);

    /**
     * 更新任务负载均衡算法
     *
     * @param groupName   组名
     * @param jobName     job名
     * @param loanBalance 负载均衡
     */
    Boolean updateLoadBalance(String groupName, String jobName, String loanBalance);

    /**
     * 获取任务负载均衡算法
     *
     * @param groupName 组名
     * @param jobName   job名
     */
    String getLoadBalance(String groupName, String jobName);

    /**
     * 同步job信息从配置文件到db
     */
    void syncJobInfoFromConfigToDb();

    /**
     * 创建job
     *
     * @param job
     */
    boolean createJob(JobInfo job);

}
