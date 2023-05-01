package org.dromara.hodor.actuator.api;

import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobInstance;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;

/**
 * job registrar
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class DefaultJobRegister implements JobRegister {

    private final String clusterName;

    private final Map<JobKey, JobDesc> jobCache;

    private final Map<JobKey, ExecutableJob> runnableJobCache;

    private final Set<String> groupNames;

    public DefaultJobRegister(final String clusterName) {
        Assert.notNull(clusterName, "clusterName must be not null");
        this.clusterName = clusterName;
        this.jobCache = new ConcurrentHashMap<>(32);
        this.runnableJobCache = new ConcurrentHashMap<>(32);
        this.groupNames = new HashSet<>();
    }

    @Override
    public Set<String> bindingGroup() {
        return groupNames;
    }

    @Override
    public String bindingCluster() {
        return clusterName;
    }

    @Override
    public List<JobDesc> registerJobs() {
        log.info("register jobs.");
        return new ArrayList<>(jobCache.values());
    }

    @Override
    public void registerJob(JobInstance jobInstance) {
        JobDesc jobDesc = jobInstance.getJobDesc();
        ExecutableJob runnableJob = jobInstance.getJobRunnable();

        log.info("add job {}", jobInstance);

        JobKey jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        groupNames.add(jobDesc.getGroupName());
        jobCache.putIfAbsent(jobKey, jobDesc);
        runnableJobCache.putIfAbsent(jobKey, runnableJob);
    }

    @Override
    public ExecutableJob provideExecutableJob(ExecutableJobContext executableJobContext) {
        return runnableJobCache.get(executableJobContext.getJobKey());
    }

    @Override
    public void clear() {
        jobCache.clear();
        runnableJobCache.clear();
        groupNames.clear();
    }

}
