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

package org.dromara.hodor.scheduler.quartz;

import cn.hutool.core.date.DateTime;
import java.util.Date;
import org.dromara.hodor.common.cron.CronUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * QuartzTrigger
 *
 * @author tomgs
 * @since 1.0
 */
public class QuartzTrigger {

    public Trigger buildTrigger(JobDesc jobDesc, JobDetail jobDetail) {
        final TimeType timeType = jobDesc.getTimeType();
        switch (timeType) {
            case CRON:
                return buildCronTrigger(jobDesc, jobDetail);
            case ONCE_TIME:
                return buildOnceTrigger(jobDesc, jobDetail);
            case FIXED_RATE:
                return buildFixedRateTrigger(jobDesc, jobDetail);
            case FIXED_DELAY:
                return buildFixedDelayTrigger(jobDesc, jobDetail);
        }
        throw new HodorSchedulerException("TimeType {} is not support by quartz.", timeType.name());
    }

    private Trigger buildCronTrigger(JobDesc jobDesc, JobDetail jobDetail) {
        final TimeType timeType = jobDesc.getTimeType();
        if (!TimeType.CRON.equals(timeType)) {
            throw new IllegalArgumentException("current time type not CRON type, " + timeType.getDescription());
        }
        final String cron = jobDesc.getTimeExp();
        CronUtils.assertValidCron(cron, "The cron {} expression is invalid", cron);

        return buildCronTrigger(jobDesc, jobDetail, cron);
    }

    private Trigger buildFixedDelayTrigger(JobDesc jobDesc, JobDetail jobDetail) {
        final TimeType timeType = jobDesc.getTimeType();
        if (!TimeType.FIXED_DELAY.equals(timeType)) {
            throw new IllegalArgumentException("current time type not FIXED_DELAY type, " + timeType.getDescription());
        }
        final String fixedDelay = jobDesc.getTimeExp();
        final Integer fixedDelayInt = Utils.Assert.validParse(() -> Integer.parseInt(fixedDelay),
            "The fixedDelay {} expression is invalid", fixedDelay);

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(fixedDelayInt)
            .withMisfireHandlingInstructionIgnoreMisfires();
        if (jobDesc.getMisfire()) {
            simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(fixedDelayInt)
                .withMisfireHandlingInstructionFireNow();
        }

        TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(jobDesc.getJobName(), jobDesc.getGroupName())
            .withSchedule(simpleScheduleBuilder)
            .withPriority(jobDesc.getPriority().getValue())
            .forJob(jobDetail);

        triggerBuilder = configTriggerTimeBuilder(jobDesc, triggerBuilder);
        return triggerBuilder.build();
    }

    private Trigger buildFixedRateTrigger(JobDesc jobDesc, JobDetail jobDetail) {
        final TimeType timeType = jobDesc.getTimeType();
        if (!TimeType.FIXED_RATE.equals(timeType)) {
            throw new IllegalArgumentException("current time type not FIXED_RATE type, " + timeType.getDescription());
        }
        final String fixedRate = jobDesc.getTimeExp();
        final Integer fixedRateSecond = Utils.Assert.validParse(() -> Integer.parseInt(fixedRate) / 1000,
            "The fixedDelay {} expression is invalid", fixedRate);

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1, fixedRateSecond)
            .withMisfireHandlingInstructionIgnoreMisfires();
        if (jobDesc.getMisfire()) {
            simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1, fixedRateSecond)
                .withMisfireHandlingInstructionFireNow();
        }

        TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(jobDesc.getJobName(), jobDesc.getGroupName())
            .withSchedule(simpleScheduleBuilder)
            .withPriority(jobDesc.getPriority().getValue())
            .forJob(jobDetail);

        triggerBuilder = configTriggerTimeBuilder(jobDesc, triggerBuilder);
        return triggerBuilder.build();
    }

    private Trigger buildOnceTrigger(JobDesc jobDesc, JobDetail jobDetail) {
        final TimeType timeType = jobDesc.getTimeType();
        if (!TimeType.ONCE_TIME.equals(timeType)) {
            throw new IllegalArgumentException("current time type not ONCE_TIME type, " + timeType.getDescription());
        }
        final String onceTime = jobDesc.getTimeExp();
        // "yyyy-MM-dd HH:mm:ss"
        String cron;
        try {
            final DateTime dateTime = Utils.Dates.parseDateTime(onceTime);
            cron = CronUtils.parseCron(dateTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("time expression is invalid, " + onceTime);
        }

        return buildCronTrigger(jobDesc, jobDetail, cron);
    }

    private static CronTrigger buildCronTrigger(JobDesc jobDesc, JobDetail jobDetail, String cron) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
            .withMisfireHandlingInstructionDoNothing();
        if (jobDesc.getMisfire()) {
            cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
                .withMisfireHandlingInstructionFireAndProceed();
        }

        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(jobDesc.getJobName(), jobDesc.getGroupName())
            .withSchedule(cronScheduleBuilder)
            .withPriority(jobDesc.getPriority().getValue())
            .forJob(jobDetail);

        triggerBuilder = configTriggerTimeBuilder(jobDesc, triggerBuilder);
        return triggerBuilder.build();
    }

    private static <T extends Trigger> TriggerBuilder<T> configTriggerTimeBuilder(JobDesc jobDesc, TriggerBuilder<T> triggerBuilder) {
        Date startTime = jobDesc.getStartTime();
        Date endTime = jobDesc.getEndTime();

        if (jobDesc.getFireNow() && startTime == null) {
            triggerBuilder = triggerBuilder.startNow();
        }

        if (startTime != null) {
            Date currentTime = new Date();
            if (startTime.before(currentTime)) {
                startTime = currentTime;
            }
            triggerBuilder = triggerBuilder.startAt(startTime);
        }
        if (endTime != null) {
            triggerBuilder = triggerBuilder.endAt(endTime);
        }
        return triggerBuilder;
    }
}
