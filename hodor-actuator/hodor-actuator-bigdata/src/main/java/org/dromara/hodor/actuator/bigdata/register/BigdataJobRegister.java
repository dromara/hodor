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

package org.dromara.hodor.actuator.bigdata.register;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.dromara.hodor.actuator.bigdata.config.HodorActuatorBigdataProperties;
import org.dromara.hodor.actuator.bigdata.core.BigdataJobRunnable;
import org.dromara.hodor.actuator.bigdata.core.JobTypeManager;
import org.dromara.hodor.actuator.bigdata.executor.CommonJobProperties;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.common.JobRegister;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.core.ExecutableJob;
import org.dromara.hodor.actuator.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.job.JobDesc;

import static org.dromara.hodor.actuator.bigdata.core.JobTypeManager.DEFAULT_JOBTYPEPLUGINDIR;

/**
 * BigdataJobRegister
 *
 * @author tomgs
 * @since 2021/12/28
 */
public class BigdataJobRegister implements JobRegister {

    private final JobTypeManager jobTypeManager;

    private final HodorActuatorBigdataProperties properties;

    public BigdataJobRegister(HodorActuatorBigdataProperties properties) {
        this.properties = properties;
        String jobTypePluginDir = StringUtils.join(properties.getCommonProperties().getDataPath(), File.separator, DEFAULT_JOBTYPEPLUGINDIR);
        Props globalProps = new Props();
        globalProps.putAll(properties.getBigdata());
        this.jobTypeManager = new JobTypeManager(jobTypePluginDir, globalProps, getClass().getClassLoader());
    }

    @Override
    public Set<String> bindingGroup() {
        return Sets.newHashSet();
    }

    @Override
    public String bindingCluster() {
        return properties.getCommonProperties().getAppName();
    }

    @Override
    public List<JobDesc> registerJobs() {
        return new ArrayList<>();
    }

    @Override
    public JobRunnable getRunnableJob(ExecutableJob executableJob) {
        String jobCommandType = executableJob.getJobCommandType();
        Props jobPros = new Props();
        jobPros.put(CommonJobProperties.JOB_TYPE, jobCommandType);
        jobPros.put(CommonJobProperties.JOB_CONTEXT, executableJob.getRequestContext());
        Job job = jobTypeManager.buildJobExecutor(executableJob.getJobKey().toString(), jobPros, executableJob.getJobLogger());
        return new BigdataJobRunnable(job);
    }

}
