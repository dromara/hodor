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
 * TimeType
 *
 * @author tomgs
 * @since 1.0
 */
public enum TimeType {

    /**
     * none类型，用于手动执行或者api调用执行
     */
    NONE(0, "none"),

    /**
     * cron表达式类型
     */
    CRON(1, "cron"),

    /**
     * 固定延迟时间类型
     */
    FIXED_DELAY(2, "fixed_delay"),

    /**
     * 固定速率类型
     */
    FIXED_RATE(3, "fixed_rate"),
    /**
     * 一次性类型
     */
    ONCE_TIME(4, "once_time");

    private int value;

    private String description;

    private TimeType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static TimeType of(int value) throws IllegalArgumentException {
        for (TimeType timeType : TimeType.values()) {
            if (timeType.getValue() == value) {
                return timeType;
            }
        }
        throw new IllegalArgumentException("TimeType value is invalid. value:" + value);
    }

    public static TimeType ofName(String name) throws IllegalArgumentException {
        for (TimeType timeType : TimeType.values()) {
            if (timeType.name().equals(name)) {
                return timeType;
            }
        }
        throw new IllegalArgumentException("TimeType name is invalid. name:" + name);
    }


}
