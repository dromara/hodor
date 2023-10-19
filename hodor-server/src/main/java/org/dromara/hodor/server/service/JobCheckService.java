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

package org.dromara.hodor.server.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.HodorLifecycle;
import org.springframework.stereotype.Service;

/**
 * JobCheckService
 * <p>
 * 1、任务超时检查（对于超过设定的超时时间的任务如果还在运行期的需要进行kill操作，并且设置超时状态）
 * 2、任务重试（一般来说对一次性任务、执行时间间隔比较长的，比如超过3分钟（支持可配置）才支持重试，保证任务可达，其余没有必要）
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
public class JobCheckService implements HodorLifecycle {

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}
