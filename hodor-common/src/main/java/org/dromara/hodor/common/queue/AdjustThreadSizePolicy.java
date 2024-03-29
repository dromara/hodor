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

package org.dromara.hodor.common.queue;

import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AdjustThreadSizePolicy
 *
 * @author tomgs
 * @since 1.0
 */
public class AdjustThreadSizePolicy<T> implements RejectedEnqueueHandler<T> {

    private final ThreadPoolExecutor executor;

    public AdjustThreadSizePolicy(final ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void rejectedExecution(T e, Queue<T> queue) {
        int maximumPoolSize = executor.getMaximumPoolSize();
        if (maximumPoolSize >= 1000) {
            return;
        }
        executor.setMaximumPoolSize(maximumPoolSize + 10);
    }

}
