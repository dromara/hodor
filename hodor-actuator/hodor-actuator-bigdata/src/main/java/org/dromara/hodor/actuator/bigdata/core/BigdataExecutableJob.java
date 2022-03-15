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

package org.dromara.hodor.actuator.bigdata.core;

import cn.hutool.core.util.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.common.ExecutableJob;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.utils.Props;
import org.dromara.hodor.common.storage.filesystem.FileStorage;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

import java.io.File;
import java.io.InputStream;

/**
 * BigdataExecutableJob
 *
 * @author tomgs
 * @since 2022/1/13
 */
public class BigdataExecutableJob implements ExecutableJob {

    private final Job job;

    private final Props jobProps;

    private final FileStorage fileStorage;

    private static final String JOB_CONFIG_FILE = "job.properties";

    public BigdataExecutableJob(final Job job, final Props jobProps, final FileStorage fileStorage) {
        this.job = job;
        this.jobProps = jobProps;
        this.fileStorage = fileStorage;
    }

    @Override
    public Object execute(ExecutableJobContext executableJobContext) throws Exception {
        // ready job resource
        JobExecuteRequest executeRequest = executableJobContext.getExecuteRequest();
        File executionsFile = executableJobContext.getExecutionsPath().toFile();
        if (!executionsFile.exists()) {
            FileUtils.forceMkdir(executionsFile);
        }
        // ${data_path}/resources/${job_key}/${version}
        File resourceFileDir = executableJobContext.getResourcesPath().toFile();
        if (!resourceFileDir.exists()) {
            //download job file from storage
            File sourceFile = new File(executeRequest.getJobPath());
            File zipFile = new File(resourceFileDir, sourceFile.getName());
            InputStream fileStream = fileStorage.fetchFile(sourceFile.toPath());
            FileUtils.copyInputStreamToFile(fileStream, zipFile);
            // unzip file
            ZipUtil.unzip(zipFile, resourceFileDir);
            // del zip file
            FileUtils.forceDelete(zipFile);
            // create link
            FileIOUtils.createDeepHardlink(resourceFileDir, executionsFile);
        }
        //load job.properties to props
        final File jobPropsFile = new File(resourceFileDir, JOB_CONFIG_FILE);
        if (jobPropsFile.exists()) {
            jobProps.put(jobPropsFile);
        }

        job.run();
        return null;
    }

    @Override
    public void stop(ExecutableJobContext executableJobContext) throws Exception {
        job.cancel();
    }

}
