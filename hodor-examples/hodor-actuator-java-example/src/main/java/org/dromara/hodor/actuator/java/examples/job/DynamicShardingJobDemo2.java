/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.actuator.java.examples.job;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.JobExecutionContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.java.annotation.Job;
import org.springframework.stereotype.Component;

/**
 * DynamicShardingJobDemo
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Component
public class DynamicShardingJobDemo2 {

    @Job(command = "splitStage2")
    public List<String> split(JobExecutionContext context) {
        return Lists.newArrayList("hello", "world", "hodor");
    }

    @Job(command = "parallelJob2")
    public String parallelJob(JobExecutionContext context) {
        log.info("Job [parallelJob] execute, context: {}", context);
        JobLogger logger = context.getJobLogger();
        final String parentJobData = (String) context.getParentJobData();
        logger.info("start executor job parallelJob, parentJobData: {}", parentJobData);
        log.info("start executor job parallelJob, parentJobData: {}", parentJobData);
        return "a=123";
    }

    @Job(command = "reduceJob2")
    public String reduceJob(JobExecutionContext context) {
        log.info("Job [reduceJob] execute, context: {}", context);
        JobLogger logger = context.getJobLogger();
        logger.info("job argument: {}", context.getJobParameter());
        logger.info("executed");
        logger.info("Job [reduceJob] execute, context: {}", context);
        logger.info("Job [reduceJob] execute, results {}", context.getParentJobExecuteResults()
        );
        logger.info("Job [reduceJob] execute, statues {}", context.getParentJobExecuteStatuses());
        return "a=123";
    }

}
