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

package org.dromara.hodor.server.executor;

import java.util.List;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.dispatch.JobDispatcher;
import org.dromara.hodor.server.executor.exception.IllegalJobExecuteStateException;
import org.dromara.hodor.server.executor.handler.HodorShardingJobRequestHandler;

/**
 * ShardingJobExecutor
 *
 * @author tomgs
 * @since 1.0
 */
public class ShardingJobExecutor extends CommonJobExecutor {

    private final JobDispatcher dispatcher;

    public ShardingJobExecutor() {
        this.dispatcher = new JobDispatcher(new HodorShardingJobRequestHandler());
    }

    @Override
    public void preProcess(HodorJobExecutionContext context) {
        final JobKey jobKey = context.getJobKey();
        final JobDesc jobDesc = context.getJobDesc();
        Props jobProps = new Props();
        jobProps.putAll(jobDesc.getJobParameters());
        // 通过在配置sharding指定分片参数，sharding=0=hello,1=world,2=hodor
        final String sharding = jobProps.getString("hodor.sharding");
        if (StringUtils.isBlank(sharding)) {
            throw new IllegalJobExecuteStateException("The job {} sharding parameter is null", jobKey);
        }

        final List<String> shardings = StringUtils.splitToList(sharding, ",");
        for (final String shard : shardings) {
            final List<String> shards = StringUtils.splitToList(shard, "=");
            if (Utils.Collections.isEmpty(shards)) {
                throw new IllegalJobExecuteStateException("The job {} sharding parameter is invalid", jobKey, sharding);
            }
        }

        context.setShardingParams(sharding);
        context.setShardingCount(shardings.size());
        context.setShardingId(-1);
        context.setShardings(shardings);

        super.preProcess(context);
    }

    @Override
    public void process(HodorJobExecutionContext context) {
        final List<Host> hosts = context.getHosts();
        final List<String> shardings = context.getShardings();
        for (int i = 0; i < shardings.size(); i++) {
            final String shard = shardings.get(i);
            final List<String> shards = StringUtils.splitToList(shard, "=");
            final HodorJobExecutionContext shardingContext = Utils.Beans.copyProperties(context, HodorJobExecutionContext.class);
            shardingContext.setRequestId(IdGenerator.defaultGenerator().nextId());
            shardingContext.setShardingCount(shardings.size());
            shardingContext.setShardingId(Integer.parseInt(shards.get(0)));
            shardingContext.setShardingParams(shards.get(1));

            final int hostSize = hosts.size();
            Host selected = hosts.get(i % hostSize);
            shardingContext.refreshHosts(selected);

            dispatcher.dispatch(shardingContext);
        }
    }

}
