/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.dromara.hodor.common.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.util.function.Consumer;


/**
 * The type Queue provider.
 * @author xiaoyu
 */
public class QueueProvider<T> {

    /**
     * 缓冲区.
     */
    private final RingBuffer<QueueEvent<T>> ringBuffer;


    /**
     * 构造器.
     *
     * @param ringBuffer 缓冲区；
     */
    QueueProvider(final RingBuffer<QueueEvent<T>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 放入一个数据.
     *
     * @param function 数据处理信息；
     */
    public void onData(final Consumer<QueueEvent<T>> function) {
        long position = ringBuffer.next();
        try {
            QueueEvent<T> dx = ringBuffer.get(position);
            function.accept(dx);
            ringBuffer.publish(position);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
