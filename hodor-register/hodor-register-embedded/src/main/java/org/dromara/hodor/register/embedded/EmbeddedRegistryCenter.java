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

package org.dromara.hodor.register.embedded;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;

/**
 * EmbeddedRegistryCenter
 *
 * @author tomgs
 * @since 2022/3/29
 */
@Join
@Slf4j
public class EmbeddedRegistryCenter implements RegistryCenter {

    @Override
    public void init(RegistryConfig config) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean checkExists(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public List<String> getChildren(String key) {
        return null;
    }

    @Override
    public void createPersistent(String key, String value) {

    }

    @Override
    public void createPersistentSequential(String path, String value) {

    }

    @Override
    public void createEphemeral(String key, String value) {

    }

    @Override
    public void createEphemeralSequential(String key, String value) {

    }

    @Override
    public String makePath(String parent, String firstChild, String... restChildren) {
        return null;
    }

    @Override
    public void makeDirs(String path) {

    }

    @Override
    public void update(String key, String value) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void addDataCacheListener(String path, DataChangeListener listener) {

    }

    @Override
    public void addConnectionStateListener(ConnectionStateChangeListener listener) {

    }

    @Override
    public void executeInLeader(String latchPath, LeaderExecutionCallback callback) {

    }

}
