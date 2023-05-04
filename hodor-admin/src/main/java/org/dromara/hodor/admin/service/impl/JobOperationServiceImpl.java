package org.dromara.hodor.admin.service.impl;

import org.dromara.hodor.admin.domain.Job;
import org.dromara.hodor.core.entity.JobGroup;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.JobOperationService;
import org.dromara.hodor.core.entity.JobInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * job后台操作服务类
 *
 * @author huangyiming
 */
@Service("jobOperationService")
public class JobOperationServiceImpl implements JobOperationService {


    @Override
    public List<Job> getJobList(String groupName) throws Exception {
        return null;
    }

    @Override
    public boolean addJob(JobInfo job, String username) throws Exception {
        return false;
    }

    @Override
    public boolean addOrUpdateJob(List<JobInfo> job, String username, HashMap<String, Object> retuenValus) throws Exception {
        return false;
    }

    @Override
    public boolean deleteJob(Job job) throws Exception {
        return false;
    }

    @Override
    public boolean isBindSession(String groupName) {
        return false;
    }

    @Override
    public boolean updateJob(JobInfo job) {
        return false;
    }

    @Override
    public boolean updateJob(Job job) {
        return false;
    }

    @Override
    public boolean suspendJob(Job job) {
        return false;
    }

    @Override
    public boolean resumeJob(Job job) {
        return false;
    }

    @Override
    public boolean createGroup(JobGroup group, String username) {
        return false;
    }

    @Override
    public List<String> getAllGroup() {
        return null;
    }

    @Override
    public List<Job> getJobListByGroup(String groupName) {
        return null;
    }

    @Override
    public Job getJobByGroupAndJobName(String groupName, String jobName) {
        return null;
    }

    @Override
    public JobInfo getJobConfig(String groupName, String jobName) {
        return null;
    }

    @Override
    public int averageDistributeSlaveJob() throws Exception {
        return 0;
    }

    @Override
    public List<JobGroup> getAllGroup(User user) {
        return null;
    }

    @Override
    public boolean saveJob(JobInfo job) {
        return false;
    }

    @Override
    public boolean bindSessionAndGroup(String sessionName, String groupName) {
        return false;
    }

    @Override
    public boolean bindSessionAndGroup(String sessionName, String oldGroup, String newGroup) {
        return false;
    }

    @Override
    public void bindJob(List<String> sessions, List<String> groupNames) {

    }

    @Override
    public void reBindJob(List<String> sessions, List<String> groupNames) {

    }

    @Override
    public List<JobGroup> getAllJobGroupForPageList() {
        return null;
    }

    @Override
    public boolean changeJobNodeWeight(String groupName, String jobName, String nodeName, String act) {
        return false;
    }

    @Override
    public boolean changeJobNodeStatus(String groupName, String jobName, String nodeName) {
        return false;
    }

    @Override
    public Boolean updateLoadBalance(String groupName, String jobName, String loanBalance) {
        return null;
    }

    @Override
    public String getLoadBalance(String groupName, String jobName) {
        return null;
    }

    @Override
    public void syncJobInfoFromConfigToDb() {

    }

    @Override
    public boolean createJob(JobInfo job) {
        return false;
    }
}
