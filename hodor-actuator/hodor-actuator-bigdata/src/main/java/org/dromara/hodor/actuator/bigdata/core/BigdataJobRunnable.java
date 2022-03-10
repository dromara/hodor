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

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.utils.Props;
import org.dromara.hodor.common.storage.filesystem.FileStorage;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * BigdataJobRunnable
 *
 * @author tomgs
 * @since 2022/1/13
 */
public class BigdataJobRunnable implements JobRunnable {

    private final Job job;

    private final Props jobProps;

    private final FileStorage fileStorage;

    private static final String JOB_CONFIG_FILE = "job.properties";

    public BigdataJobRunnable(final Job job, final Props jobProps, final FileStorage fileStorage) {
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
        File resourceFile = executableJobContext.getResourcesPath().toFile();
        if (!resourceFile.exists()) {
            //download job file from storage
            InputStream fileStream = fileStorage.fetchFile(Paths.get(executeRequest.getJobPath()));
            FileUtils.copyInputStreamToFile(fileStream, resourceFile);
            // unzip file
        }
        //load job.properties to props
        final File jobPropsFile = new File(resourceFile, JOB_CONFIG_FILE);
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
