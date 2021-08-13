package org.dromara.hodor.server.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.model.enums.Priority;
import org.dromara.hodor.model.job.JobInstance;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.api.http.HodorRestClient;
import org.dromara.hodor.scheduler.api.exception.CreateJobException;
import org.junit.Test;

/**
 * scheduler service test
 *
 * @author tomgs
 * @since 2021/8/9
 */
public class SchedulerServiceTest {

    @Test
    public void testCreateJob() {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setGroupName("testGroup");
        jobInfo.setJobName("test3");
        jobInfo.setCron("0/5 * * * * ?");
        jobInfo.setPriority(Priority.DEFAULT);
        String server = "127.0.0.1:8081";

        HodorRestClient hodorRestClient = HodorRestClient.getInstance();
        HodorHttpRequest request = new HodorHttpRequest();
        request.setUri("/hodor/scheduler/createJob");
        request.setMethod("POST");
        request.setContent(SerializeUtils.serialize(jobInfo));
        CompletableFuture<HodorHttpResponse> future = hodorRestClient.sendHttpRequest(Host.of(server), request);
        try {
            HodorHttpResponse hodorHttpResponse = future.get();
            System.out.println(hodorHttpResponse);
        } catch (Exception e) {
            throw new CreateJobException(StringUtils.format("create job {} exception, msg: {}.",
                JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), e.getMessage()), e);
        }
    }

    @Test
    public void testBatchCreateJob() {
        List<JobInstance> jobs = new ArrayList<>();

    }

}
