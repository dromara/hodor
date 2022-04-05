package org.dromara.hodor.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.JobInstance;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;

/**
 * job registrar
 *
 * @author tomgs
 * @since 2021/1/4
 */
@Slf4j
public class JobRegistrar {

    private final HodorApiClient hodorApiClient;

    private final Map<String, JobInstance> jobCache = new ConcurrentHashMap<>(32);

    private final Map<String, ScheduledMethodRunnable> jobRunnableCache = new ConcurrentHashMap<>(32);

    private final Set<String> groupNames = new HashSet<>();

    public JobRegistrar(final HodorApiClient hodorApiClient) {
        this.hodorApiClient = hodorApiClient;
    }

    public void registerJobs() throws Exception {
        log.info("register jobs.");
        Collection<JobInstance> jobs = jobCache.values();
        hodorApiClient.registerJobs(jobs);
    }

    public ScheduledMethodRunnable getJobRunnable(String groupName, String jobName) {
        String jobKey = createJobKey(groupName, jobName);
        return jobRunnableCache.get(jobKey);
    }

    public void addJob(JobInstance jobInstance, ScheduledMethodRunnable runnable) {
        log.info("add job {}", jobInstance);
        String jobKey = createJobKey(jobInstance.getGroupName(), jobInstance.getJobName());
        groupNames.add(jobInstance.getGroupName());
        jobCache.putIfAbsent(jobKey, jobInstance);
        jobRunnableCache.putIfAbsent(jobKey, runnable);
    }

    private String createJobKey(String groupName, String jobName) {
        return groupName + "-" + jobName;
    }

    public Set<String> getGroupNames() {
        return groupNames;
    }

}
