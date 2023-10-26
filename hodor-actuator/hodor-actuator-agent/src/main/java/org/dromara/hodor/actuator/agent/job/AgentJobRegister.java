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

package org.dromara.hodor.actuator.agent.job;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.actuator.agent.config.HodorActuatorAgentProperties;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractProcessJob;
import org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties;
import org.dromara.hodor.actuator.jobtype.api.executor.Constants;
import org.dromara.hodor.actuator.jobtype.api.executor.Job;
import org.dromara.hodor.actuator.jobtype.api.jobtype.JobTypeManager;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.storage.api.FileStorage;


/**
 * BigdataJobRegister
 *
 * @author tomgs
 * @since 1.0
 */
public class AgentJobRegister implements JobRegister {

    private final JobTypeManager jobTypeManager;

    private final HodorActuatorAgentProperties properties;

    private final FileStorage fileStorage;

    private final String dataPath;

    private static final String JOB_CONFIG_FILE = "job.properties";

    public AgentJobRegister(HodorActuatorAgentProperties properties) {
        this.properties = properties;
        this.dataPath = properties.getCommonProperties().getDataPath();
        final String jobtypePath = properties.getCommonProperties().getJobtypePlugins();
        Utils.Assert.notNull("jobtype plugins path config must be not null", jobtypePath);
        Utils.Assert.notNull("dataPath config must be not null", dataPath);
        String jobTypePluginDir = StringUtils.join(jobtypePath, File.separator, JobTypeManager.DEFAULT_JOBTYPEPLUGINDIR);
        Props globalProps = new Props();
        globalProps.putAll(properties.getAgentConfig());
        // TODO: 在JobTypeManager中修改读取任务类型逻辑
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
    public List<String> registerJobType() {
        return jobTypeManager.getJobTypePluginSet().getJobTypeNames();
    }

    @Override
    public List<JobDesc> registerJobs() {
        return new ArrayList<>();
    }

    @Override
    public ExecutableJob provideExecutableJob(ExecutableJobContext executableJobContext) throws Exception {
        Props jobProps = new Props();
        JobLogger logger = executableJobContext.getJobLogger();

        setup(executableJobContext, jobProps);
        resetJobProps(executableJobContext, jobProps);

        Job job = jobTypeManager.buildJobExecutor(executableJobContext.getJobKey().toString(),
            jobProps,
            logger);
        return new AgentExecutableJob(job, logger);
    }

    private void setup(ExecutableJobContext executableJobContext, Props jobProps) throws IOException {
        // ready job resource
        JobExecuteRequest executeRequest = executableJobContext.getExecuteRequest();
        File executionsFile = executableJobContext.getExecutionsPath(dataPath).toFile();
        if (!executionsFile.exists()) {
            FileUtils.forceMkdir(executionsFile);
        }

        // ${data_path}/resources/${job_key}/${version}
        File resourceFileDir = executableJobContext.getResourcesPath(dataPath).toFile();
        if (!resourceFileDir.exists()) {
            //download job file from storage
            File sourceFile = new File(executeRequest.getJobCommand());
            if (!FileUtil.isFile(sourceFile)) {
                return;
            }

            File targetFile = new File(resourceFileDir, sourceFile.getName());
            InputStream fileStream = fileStorage.fetchFile(sourceFile.toPath());
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
            if ("zip".equals(FileTypeUtil.getType(targetFile))) {
                // unzip file
                ZipUtil.unzip(targetFile, resourceFileDir);
                // del zip file
                FileUtils.forceDelete(targetFile);
            }

            // create link
            FileIOUtils.createDeepHardlink(resourceFileDir, executionsFile);
        }
        //load job.properties to props
        final File jobPropsFile = new File(resourceFileDir, JOB_CONFIG_FILE);
        if (jobPropsFile.exists()) {
            jobProps.put(jobPropsFile);
        }
    }

    private void resetJobProps(ExecutableJobContext executableJobContext, Props jobProps) {
        jobProps.put(CommonJobProperties.JOB_CONTEXT, executableJobContext);
        jobProps.put(CommonJobProperties.JOB_KEY, executableJobContext.getJobKey());
        jobProps.put(CommonJobProperties.JOB_TYPE, executableJobContext.getJobCommandType());
        jobProps.put(CommonJobProperties.REQUEST_CONTEXT, executableJobContext.getRequestContext());
        jobProps.put(Constants.JobProperties.JOB_LOG_PATH, executableJobContext.getAbsoluteLogPath().toString());
        jobProps.put(AbstractProcessJob.WORKING_DIR, executableJobContext.getResourcesPath(dataPath).toString());
        // get all field map
        //Map<String, String> requestDescribe = BeanUtils.describe(executableJobContext.getExecuteRequest());
        //jobProps.putAll(requestDescribe);
        jobProps.putAll(executableJobContext.getExecuteRequest().getJobParameters());
        jobProps.putAll(executableJobContext.getExecuteRequest().getExtensibleParameters());
    }

}
