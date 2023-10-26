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

package org.dromara.hodor.cache.redis;

import org.dromara.hodor.cache.api.CacheClient;
import org.dromara.hodor.cache.api.CacheSourceConfig;
import org.dromara.hodor.cache.api.HodorCacheSource;
import org.dromara.hodor.common.extension.Join;

/**
 * RedisCacheSource
 *
 * @author tomgs
 * @version 1.0
 */
@Join(order = 1)
public class RedisCacheSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    public RedisCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
    }
    @Override
    public String getCacheType() {
        return "redis";
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        return null;
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return null;
    }

}
