/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.dromara.hodor.remoting.api;

import java.util.concurrent.ExecutionException;

/**
 * HodorChannelFuture。
 *
 * @author xiaoyu
 */
public interface HodorChannelFuture {


    /**
     * Channel hodor channel.
     *
     * @return the hodor channel
     */
    HodorChannel channel();

    /**
     * Is done boolean.
     *
     * @return the boolean
     */
    boolean isDone();

    /**
     * Returns {@code true} if and only if the I/O operation was completed
     * successfully.
     */
    boolean isSuccess();

    /**
     * Cause boolean.
     *
     * @return the boolean
     */
    Throwable cause();

    Void get() throws ExecutionException, InterruptedException;

    /**
     * Invoked when the operation associated with the {@link HodorChannelFuture} has been completed.
     *
     * @param listener  the source {@link HodorChannelFutureListener} which called this callback
     */
    void operationComplete(HodorChannelFutureListener<HodorChannelFuture> listener);

}
