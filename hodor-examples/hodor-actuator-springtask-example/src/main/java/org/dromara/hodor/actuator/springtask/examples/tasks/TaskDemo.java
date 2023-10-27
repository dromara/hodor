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

package org.dromara.hodor.actuator.springtask.examples.tasks;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TaskDemo
 *
 * @author tomgs
 * @since 1.0
 */
@Component
public class TaskDemo {

    @Scheduled(fixedRate = 3000)
    public void task1() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        System.out.println("执行 fixedRate 任务的时间：" + new Date(System.currentTimeMillis()));
    }

    @Scheduled(fixedDelay = 4000)
    public void task2() throws InterruptedException {
        TimeUnit.SECONDS.sleep(7);
        System.out.println("执行 fixedDelay 任务的时间：" + new Date(System.currentTimeMillis()));
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void task3() {
        System.out.println("执行 cron 任务的时间：" + new Date(System.currentTimeMillis()));
    }

}
