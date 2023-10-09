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
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.dispatch.JobDispatcher;
import org.dromara.hodor.server.executor.handler.HodorBroadcastJobRequestHandler;

/**
 * ShardingJobExecutor
 *
 * @author tomgs
 * @since 1.0
 */
public class BroadcastJobExecutor extends CommonJobExecutor {

    private final JobDispatcher dispatcher;

    public BroadcastJobExecutor() {
        this.dispatcher = new JobDispatcher(new HodorBroadcastJobRequestHandler());
    }

    @Override
    public void process(HodorJobExecutionContext context) {
        final List<Host> hosts = context.getHosts();
        for (int i = 0; i < hosts.size(); i++) {
            final HodorJobExecutionContext shardingContext = Utils.Beans.copyProperties(context, HodorJobExecutionContext.class);
            shardingContext.setShardingCount(hosts.size());
            shardingContext.setShardingId(i);
            Host selected = hosts.get(i);
            shardingContext.refreshHosts(selected);

            dispatcher.dispatch(shardingContext);
        }
    }

}
