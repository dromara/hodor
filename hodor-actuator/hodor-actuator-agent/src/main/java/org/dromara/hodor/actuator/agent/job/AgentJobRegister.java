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
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.storage.api.FileStorage;
import org.dromara.hodor.storage.api.StorageConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * BigdataJobRegister
 *
 * @author tomgs
 * @since 1.0
 */
public class AgentJobRegister implements JobRegister {

    private final JobTypeManager jobTypeManager;

    private final HodorActuatorAgentProperties properties;

    private final Map<String, StorageConfig> storageConfigMap;

    private final ExtensionLoader<FileStorage> fileStorageLoader;

    private final String dataPath;

    private static final String JOB_CONFIG_FILE = "job.properties";
    private static final String JOB_STORAGE_TYPE = "job.storage.type";

    public AgentJobRegister(HodorActuatorAgentProperties properties) {
        this.properties = properties;
        this.dataPath = properties.getCommonProperties().getDataPath();
        this.storageConfigMap = properties.getStorageConfig();
        final String jobtypePath = properties.getCommonProperties().getJobtypePlugins();

        Assert.notNull("jobtype plugins path config must be not null", jobtypePath);
        Assert.notNull("dataPath config must be not null", dataPath);

        String jobTypePluginDir = StringUtils.join(jobtypePath, File.separator, JobTypeManager.DEFAULT_JOBTYPEPLUGINDIR);
        Props globalProps = new Props();
        globalProps.putAll(properties.getAgentConfig());
        this.jobTypeManager = new JobTypeManager(jobTypePluginDir, globalProps, getClass().getClassLoader());
        this.fileStorageLoader = ExtensionLoader.getExtensionLoader(FileStorage.class, StorageConfig.class);
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
        JobLogger logger = executableJobContext.getJobLogger();
        Props jobProps = prepareJobProps(executableJobContext);
        setupJobResources(executableJobContext, jobProps);

        Job job = jobTypeManager.buildJobExecutor(executableJobContext.getJobKey().toString(),
            jobProps,
            logger);
        return new AgentExecutableJob(job, logger);
    }

    private void setupJobResources(ExecutableJobContext context, Props jobProps) throws IOException {
        // ready job resource
        JobExecuteRequest executeRequest = context.getExecuteRequest();
        File executionsFile = context.getExecutionsPath(dataPath).toFile();
        if (!executionsFile.exists()) {
            FileUtils.forceMkdir(executionsFile);
        }

        // load resource file
        String storageType = jobProps.getString(JOB_STORAGE_TYPE, "none");
        StorageConfig storageConfig = storageConfigMap.get(storageType);
        context.getJobLogger().info("Job resource type, {}", storageType);
        if (storageConfig == null) {
            return;
        }

        // ${data_path}/resources/${job_key}/${version}
        File resourceFileDir = context.getResourcesPath(dataPath).toFile();
        if (!resourceFileDir.exists()) {
            //download job file from storage
            File sourceFile = new File(executeRequest.getJobCommand());
            if (!FileUtil.isFile(sourceFile)) {
                return;
            }
            File targetFile = new File(resourceFileDir, sourceFile.getName());
            FileStorage fileStorage = fileStorageLoader.getProtoJoin(storageType, storageConfig);
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
            jobProps.putIfAbsent(jobPropsFile);
        }
    }

    private Props prepareJobProps(ExecutableJobContext executableJobContext) {
        Props jobProps = new Props();
        jobProps.put(CommonJobProperties.JOB_CONTEXT, executableJobContext);
        jobProps.put(CommonJobProperties.JOB_KEY, executableJobContext.getJobKey());
        jobProps.put(CommonJobProperties.JOB_TYPE, executableJobContext.getJobCommandType());
        jobProps.put(CommonJobProperties.REQUEST_CONTEXT, executableJobContext.getRequestContext());
        jobProps.put(Constants.JobProperties.JOB_LOG_PATH, executableJobContext.getAbsoluteLogPath().toString());
        jobProps.put(AbstractProcessJob.WORKING_DIR, executableJobContext.getResourcesPath(dataPath).toString());
        jobProps.putAll(executableJobContext.getExecuteRequest().getJobParameters());
        jobProps.putAll(executableJobContext.getExecuteRequest().getExtensibleParameters());
        return jobProps;
    }

}
