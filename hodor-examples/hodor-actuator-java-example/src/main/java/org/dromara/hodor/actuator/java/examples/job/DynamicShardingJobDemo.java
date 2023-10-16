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
import org.dromara.hodor.actuator.java.examples.entity.ShardData;
import org.springframework.stereotype.Component;

/**
 * DynamicShardingJobDemo
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Component
public class DynamicShardingJobDemo {

    @Job(command = "splitStage")
    public List<ShardData> split(JobExecutionContext context) {
        ShardData shardData = ShardData.builder().id(0).params("hello").build();
        ShardData shardData1 = ShardData.builder().id(1).params("world").build();
        return Lists.newArrayList(shardData, shardData1);
    }

    @Job(command = "parallelJob")
    public String parallelJob(JobExecutionContext context) {
        log.info("Job [parallelJob] execute, context: {}", context);
        JobLogger logger = context.getJobLogger();
        final ShardData parentJobData = context.getParentJobData(ShardData.class);
        logger.info("start executor job parallelJob, parentJobData: {}", parentJobData);
        log.info("start executor job parallelJob, parentJobData: {}", parentJobData);
        return "a=123";
    }

    @Job(command = "reduceJob")
    public String reduceJob(JobExecutionContext context) {
        log.info("Job [reduceJob] execute, context: {}", context);
        JobLogger logger = context.getJobLogger();
        logger.info("start executor job test1");
        logger.info("job argument: {}", context.getJobParameter());
        logger.info("executing......");
        logger.info("executed");
        return "a=123";
    }

}
