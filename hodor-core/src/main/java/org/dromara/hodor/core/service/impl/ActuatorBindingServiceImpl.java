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

package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.entity.ActuatorBinding;
import org.dromara.hodor.core.mapper.ActuatorBindingMapper;
import org.dromara.hodor.core.service.ActuatorBindingService;
import org.springframework.stereotype.Service;

/**
 * ActuatorBindingServiceImpl
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActuatorBindingServiceImpl implements ActuatorBindingService {

    private final ActuatorBindingMapper actuatorBindingMapper;

    @Override
    public boolean bind(String clusterName, String groupName) {
        ActuatorBinding actuatorBinding = new ActuatorBinding();
        actuatorBinding.setClusterName(clusterName);
        actuatorBinding.setGroupName(groupName);
        actuatorBinding.setUpdateTime(new Date());
        int update = actuatorBindingMapper.update(actuatorBinding, Wrappers.<ActuatorBinding>lambdaUpdate()
            .eq(ActuatorBinding::getClusterName, clusterName)
            .eq(ActuatorBinding::getGroupName, groupName));
        if (update > 0) {
            return true;
        }
        actuatorBinding.setCreateTime(new Date());
        int insert = actuatorBindingMapper.insert(actuatorBinding);
        return insert > 0;
    }

    @Override
    public boolean unbind(String clusterName, String groupName) {
        int delete = actuatorBindingMapper.delete(Wrappers.<ActuatorBinding>lambdaQuery()
            .eq(ActuatorBinding::getClusterName, clusterName)
            .eq(ActuatorBinding::getGroupName, groupName));
        return delete > 0;
    }

    @Override
    public List<ActuatorBinding> listBinding() {
        return actuatorBindingMapper.selectList(null);
    }

}
