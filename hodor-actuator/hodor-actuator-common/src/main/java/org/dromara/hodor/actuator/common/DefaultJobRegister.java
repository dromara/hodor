package org.dromara.hodor.actuator.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.core.JobInstance;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;

/**
 * job registrar
 *
 * @author tomgs
 * @since 2021/1/4
 */
@Slf4j
public class DefaultJobRegister implements JobRegister {

    private final Map<JobKey, JobDesc> jobCache = new ConcurrentHashMap<>(32);

    private final Map<JobKey, JobRunnable> runnableJobCache = new ConcurrentHashMap<>(32);

    private final Set<String> groupNames = new HashSet<>();

    public DefaultJobRegister() {
    }

    @Override
    public Set<String> bindingGroup() {
        return groupNames;
    }

    @Override
    public String bindingCluster() {
        return null;
    }

    @Override
    public List<JobDesc> registerJobs() {
        log.info("register jobs.");
        return new ArrayList<>(jobCache.values());
    }

    @Override
    public void registerJob(JobInstance jobInstance) {
        JobDesc jobDesc = jobInstance.getJobDesc();
        JobRunnable runnableJob = jobInstance.getJobRunnable();

        log.info("add job {}", jobInstance);

        JobKey jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        groupNames.add(jobDesc.getGroupName());
        jobCache.putIfAbsent(jobKey, jobDesc);
        runnableJobCache.putIfAbsent(jobKey, runnableJob);
    }

    @Override
    public JobRunnable provideJobRunnable(ExecutableJobContext executableJobContext) {
        return runnableJobCache.get(executableJobContext.getJobKey());
    }

    @Override
    public void clear() {
        jobCache.clear();
        runnableJobCache.clear();
        groupNames.clear();
    }

}
