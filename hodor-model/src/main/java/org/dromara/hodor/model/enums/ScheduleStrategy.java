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

package org.dromara.hodor.model.enums;

/**
 * ScheduleStrategy
 *
 * @author tomgs
 * @since 1.0
 */
public enum ScheduleStrategy {

    /**
     * 随机负载
     */
    RANDOM(0, "random"),

    /**
     * 轮询
     */
    ROUND_ROBIN(1, "round_robin"),

    /**
     * 最低负载
     */
    LOWEST_LOAD(2, "lowest_load"),

    /**
     * 指定actuator ip
     */
    SPECIFY(3, "specify");

    private int value;

    private String name;

    private ScheduleStrategy(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static ScheduleStrategy of(int value) throws IllegalArgumentException {
        for (ScheduleStrategy scheduleStrategy : ScheduleStrategy.values()) {
            if (scheduleStrategy.getValue() == value) {
                return scheduleStrategy;
            }
        }
        throw new IllegalArgumentException("TimeType value is invalid. value:" + value);
    }

}
