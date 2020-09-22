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

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * disruptor 管理器.
 *
 * @author xiaoyu
 */
@SuppressWarnings("all")
public class QueueProviderManager<T> {


    /**
     * 分区大小.
     */
    private final Integer size;

    /**
     * 处理器.
     */
    private QueueConsumerFactory<T> queueConsumerFactory;

    /**
     * 线程组处理器.
     */
    private ExecutorService executor;

    /**
     * 提供者.
     */
    private QueueProvider<T> provider;

    /**
     * 线程的大小.
     */
    private Integer threadSize;

    /**
     * 消费端的数量.
     */
    private Integer consumerSize;

    /**
     * 处理.
     *
     * @param queueConsumerFactory 处理；
     */
    public QueueProviderManager(final QueueConsumerFactory<T> queueConsumerFactory) {
        this(queueConsumerFactory, 4096 * 2 * 2);
    }

    /**
     * 初始化数据.
     *
     * @param queueConsumerFactory the queue consumer factory
     * @param ringBufferSize       the ring buffer size
     */
    public QueueProviderManager(final QueueConsumerFactory<T> queueConsumerFactory, final int ringBufferSize) {
        this(queueConsumerFactory, Runtime.getRuntime().availableProcessors() << 1, Runtime.getRuntime().availableProcessors() << 1, ringBufferSize);
    }

    /**
     * 初始化数据.
     *
     * @param queueConsumerFactory the queue consumer factory
     * @param exeThreadSize        the thread size 执行线程的数量.
     * @param ringBufferSize       the ring buffer size
     */
    public QueueProviderManager(final QueueConsumerFactory<T> queueConsumerFactory, final int exeThreadSize, final int consumerSize, final int ringBufferSize) {
        this.queueConsumerFactory = queueConsumerFactory;
        this.threadSize = exeThreadSize;
        this.size = ringBufferSize;
        this.consumerSize = consumerSize;
        executor = new ThreadPoolExecutor(threadSize, threadSize, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                HodorThreadFactory.create("disruptor_queue_exe-" + queueConsumerFactory.fixName(), false),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 启动数据.
     */
    public void start() {
        QueueEventFactory<T> factory = new QueueEventFactory<>();
        /*
         * 处理器；
         */
        Disruptor<QueueEvent<T>> disruptor = new Disruptor<>(factory,
                size,
                HodorThreadFactory.create("disruptor_queue_consumer-" + queueConsumerFactory.fixName(), false),
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        QueueConsumer<T>[] consumers = new QueueConsumer[this.consumerSize];
        for (int i = 0; i < consumerSize; i++) {
            consumers[i] = new QueueConsumer<>(executor, queueConsumerFactory);
        }
        disruptor.handleEventsWithWorkerPool(consumers);
        disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
        disruptor.start();
        RingBuffer<QueueEvent<T>> buffer = disruptor.getRingBuffer();
        provider = new QueueProvider<>(buffer);

    }

    /**
     * 得到当前的提供者.
     *
     * @return 返回提供者 ；
     */
    public QueueProvider<T> getProvider() {
        return provider;
    }
}
