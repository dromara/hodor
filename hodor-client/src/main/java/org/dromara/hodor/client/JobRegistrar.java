package org.dromara.hodor.client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.config.JobDesc;
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

    public void registerJobs() {
        log.info("register jobs.");
        Collection<JobDesc> jobs = jobCache.values();
        hodorApiClient.registerJobs(jobs);
    }

    public void addJob(JobDesc jobDesc) {
        String jobKey = createJobKey(jobDesc.getGroupName(), jobDesc.getJobName());
        jobCache.putIfAbsent(jobKey, jobDesc);
    }

    private String createJobKey(String groupName, String jobName) {
        return groupName + "-" + jobName;
    }

}
