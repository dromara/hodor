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

package org.dromara.hodor.admin.service;

import java.util.List;

import org.dromara.hodor.client.model.KillJobRequest;
import org.dromara.hodor.client.model.KillJobResult;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.BindingInfo;

/**
 * ActuatorOperatorService
 *
 * @author tomgs
 * @since 1.0
 */
public interface ActuatorOperatorService {

    LogQueryResult queryLog(LogQueryRequest request) throws Exception;

    void binding(String clusterName, String group) throws Exception;

    void unbinding(String clusterName, String group) throws Exception;

    List<BindingInfo> listBinding() throws Exception;

    List<ActuatorInfo> getActuatorClusterInfos() throws Exception;

    List<String> allClusters() throws Exception;

    List<ActuatorInfo> getActuatorByName(String name) throws Exception;

    /**
     * 杀死正在执行的任务
     * @param killJobRequest 终止任务请求
     * @return 终止任务响应结果
     */
    KillJobResult killRunningJob(KillJobRequest killJobRequest) throws Exception;


}
