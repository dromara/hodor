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

package org.dromara.hodor.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.dromara.hodor.admin.service.ActuatorOperatorService;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.api.ActuatorApi;
import org.dromara.hodor.client.api.SchedulerApi;
import org.dromara.hodor.client.model.KillJobRequest;
import org.dromara.hodor.client.model.KillJobResult;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.BindingInfo;
import org.springframework.stereotype.Service;

/**
 * ActuatorOperatorServiceImpl
 *
 * @author tomgs
 * @since 1.0
 */
@Service
public class ActuatorOperatorServiceImpl implements ActuatorOperatorService {

    private final ActuatorApi actuatorApi;

    private final SchedulerApi schedulerApi;

    public ActuatorOperatorServiceImpl(final HodorApiClient hodorApiClient) {
        this.actuatorApi = hodorApiClient.createApi(ActuatorApi.class);
        this.schedulerApi = hodorApiClient.createApi(SchedulerApi.class);
    }

    @Override
    public LogQueryResult queryLog(LogQueryRequest request) throws Exception {
        return actuatorApi.queryLog(request);
    }

    @Override
    public void binding(String clusterName, String group) throws Exception {
        actuatorApi.binding(clusterName, group);
    }

    @Override
    public void unbinding(String clusterName, String group) throws Exception {
        actuatorApi.unbinding(clusterName, group);
    }

    @Override
    public List<BindingInfo> listBinding() throws Exception {
        return actuatorApi.listBinding();
    }

    @Override
    public List<ActuatorInfo> getActuatorClusterInfos() throws Exception {
        return actuatorApi.actuatorInfos();
    }

    @Override
    public List<String> allClusters() throws Exception {
        return actuatorApi.allClusters();
    }

    @Override
    public List<ActuatorInfo> getActuatorByName(String name) throws Exception {
        // TODO: 可以考虑增加ActuatorApi相应接口进行优化
        List<ActuatorInfo> res = new ArrayList<>();
        // 1. 获取所有执行节点
        List<ActuatorInfo> actuatorInfos = actuatorApi.actuatorInfos();
        // 2. 根据name匹配
        for (ActuatorInfo actuatorInfo : actuatorInfos) {
            if (actuatorInfo.getName().indexOf(name) != -1) {
                res.add(actuatorInfo);
            }
        }
        return res;
    }

    @Override
    public KillJobResult killRunningJob(KillJobRequest request) throws Exception {
        Utils.Assert.notNull(request.getRequestId(), "requestId must be not null");
        Utils.Assert.notNull(request.getGroupName(), "groupName must be not null");
        Utils.Assert.notNull(request.getJobName(), "jobName must be not null");
        Utils.Assert.notNull(request.getActuatorEndpoint(), "actuator endpoint must be not null");
        Utils.Assert.notNull(request.getTimeout(), "timeout must be not null");
        return actuatorApi.killRunningJob(request);
    }

    @Override
    public List<String> getJobTypeNames(String clusterName) throws Exception {
        return schedulerApi.getJobTypeNames(clusterName);
    }
}
