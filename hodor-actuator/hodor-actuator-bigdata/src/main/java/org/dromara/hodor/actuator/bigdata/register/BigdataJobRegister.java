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
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.dromara.hodor.actuator.bigdata.config.HodorActuatorBigdataProperties;
import org.dromara.hodor.actuator.bigdata.core.BigdataJobRunnable;
import org.dromara.hodor.actuator.bigdata.core.JobTypeManager;
import org.dromara.hodor.actuator.bigdata.executor.AbstractProcessJob;
import org.dromara.hodor.actuator.bigdata.executor.CommonJobProperties;
import org.dromara.hodor.actuator.bigdata.executor.Constants;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.common.JobRegister;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.utils.Props;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.filesystem.FileStorage;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.job.JobDesc;


/**
 * BigdataJobRegister
 *
 * @author tomgs
 * @since 2021/12/28
 */
public class BigdataJobRegister implements JobRegister {

    private final JobTypeManager jobTypeManager;

    private final HodorActuatorBigdataProperties properties;

    private final FileStorage fileStorage;

    public BigdataJobRegister(HodorActuatorBigdataProperties properties) {
        this.properties = properties;
        String jobTypePluginDir = StringUtils.join(properties.getCommonProperties().getDataPath(), File.separator, JobTypeManager.DEFAULT_JOBTYPEPLUGINDIR);
        Props globalProps = new Props();
        globalProps.putAll(properties.getBigdata());
        this.jobTypeManager = new JobTypeManager(jobTypePluginDir, globalProps, getClass().getClassLoader());
        this.fileStorage = ExtensionLoader.getExtensionLoader(FileStorage.class).getDefaultJoin();
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
    public JobRunnable getRunnableJob(ExecutableJobContext executableJobContext) throws Exception {
        Props jobProps = buildJobProps(executableJobContext);
        Job job = jobTypeManager.buildJobExecutor(executableJobContext.getJobKey().toString(), jobProps, executableJobContext.getJobLogger().getLogger());
        return new BigdataJobRunnable(job, jobProps, fileStorage);
    }

    private Props buildJobProps(ExecutableJobContext executableJobContext) throws Exception {
        Props jobProps = new Props();
        jobProps.put(CommonJobProperties.JOB_TYPE, executableJobContext.getJobCommandType());
        jobProps.put(CommonJobProperties.JOB_CONTEXT, executableJobContext.getRequestContext());
        jobProps.put(Constants.JobProperties.JOB_LOG_PATH, executableJobContext.getAbsoluteLogPath().toString());
        jobProps.put(AbstractProcessJob.WORKING_DIR, executableJobContext.getExecutionsPath().toString());
        // get all field map
        Map<String, String> requestDescribe = BeanUtils.describe(executableJobContext.getExecuteRequest());
        jobProps.putAll(requestDescribe);
        return jobProps;
    }

}
