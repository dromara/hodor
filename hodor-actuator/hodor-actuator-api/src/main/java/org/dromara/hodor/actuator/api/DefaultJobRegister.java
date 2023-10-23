package org.dromara.hodor.actuator.api;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobInstance;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
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

    private final Map<String, ExecutableJob> runnableJobCache;

    private final Set<String> groupNames;

    private final HodorProperties properties;

    public DefaultJobRegister(final HodorProperties properties) {
        Assert.notNull(properties, "properties must be not null");
        Assert.notNull(properties.getAppName(), "clusterName must be not null");
        this.properties = properties;
        this.clusterName = properties.getAppName();
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
    public List<String> registerJobType() {
        return properties.getJobTypes() == null ?
            Lists.newArrayList("java") : properties.getJobTypes();
    }

    @Override
    public List<JobDesc> registerJobs() {
        log.info("register jobs.");
        if (properties.getAutoRegistry()) {
            return new ArrayList<>(jobCache.values());
        }
        return new ArrayList<>();
    }

    @Override
    public void registerJob(JobInstance jobInstance) {
        JobDesc jobDesc = jobInstance.getJobDesc();
        ExecutableJob runnableJob = jobInstance.getJobRunnable();

        log.info("add job {}", jobInstance);
        JobKey jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        if (StringUtils.isNotBlank(jobDesc.getGroupName())
            && StringUtils.isNotBlank(jobDesc.getJobName())) {
            groupNames.add(jobDesc.getGroupName());
            jobCache.putIfAbsent(jobKey, jobDesc);
        }
        final String jobCommand = jobDesc.getJobCommand();
        if (StringUtils.isNotBlank(jobCommand)) {
            runnableJobCache.putIfAbsent(jobCommand, runnableJob);
        } else {
            runnableJobCache.putIfAbsent(jobKey.toString(), runnableJob);
        }
    }

    @Override
    public ExecutableJob provideExecutableJob(ExecutableJobContext executableJobContext) {
        if (StringUtils.isBlank(executableJobContext.getJobCommand())) {
            return runnableJobCache.get(executableJobContext.getJobKey().toString());
        }
        return runnableJobCache.get(executableJobContext.getJobCommand());
    }

    @Override
    public void clear() {
        jobCache.clear();
        runnableJobCache.clear();
        groupNames.clear();
    }

}
