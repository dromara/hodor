package org.dromara.hodor.client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * job registrar
 *
 * @author tomgs
 * @since 2021/1/4
 */
@Slf4j
public class JobRegistrar {

    @Autowired
    private HodorApiClient hodorApiClient;

    private final Map<String, JobDesc> jobCache = new ConcurrentHashMap<>(32);

    private final Map<String, ScheduledMethodRunnable> jobRunnableCache = new ConcurrentHashMap<>(32);

    public void registerJobs() {
        log.info("register jobs.");
        Collection<JobDesc> jobs = jobCache.values();
        hodorApiClient.registerJobs(jobs);
    }

    public ScheduledMethodRunnable getJobRunnable(String groupName, String jobName) {
        String jobKey = createJobKey(groupName, jobName);
        return jobRunnableCache.get(jobKey);
    }

    public void addJob(JobDesc jobDesc, ScheduledMethodRunnable runnable) {
        log.info("add job {}", jobDesc);
        String jobKey = createJobKey(jobDesc.getGroupName(), jobDesc.getJobName());
        jobCache.putIfAbsent(jobKey, jobDesc);
        jobRunnableCache.putIfAbsent(jobKey, runnable);
    }

    private String createJobKey(String groupName, String jobName) {
        return groupName + "-" + jobName;
    }

}
