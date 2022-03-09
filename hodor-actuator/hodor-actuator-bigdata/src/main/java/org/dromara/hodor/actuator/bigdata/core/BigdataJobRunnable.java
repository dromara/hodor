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
import org.dromara.hodor.actuator.common.core.ExecutableJob;
import org.dromara.hodor.common.storage.filesystem.FileStorage;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * BigdataJobRunnable
 *
 * @author tomgs
 * @since 2022/1/13
 */
public class BigdataJobRunnable implements JobRunnable {

    private final Job job;

    private final FileStorage fileStorage;

    public BigdataJobRunnable(final Job job, FileStorage fileStorage) {
        this.job = job;
        this.fileStorage = fileStorage;
    }

    @Override
    public Object execute(ExecutableJob executableJob) throws Exception {
        JobExecuteRequest executeRequest = executableJob.getExecuteRequest();
        // ${data_path}/resources/${job_key}/${version}
        Path path = Paths.get(executableJob.getDataPath(), "resources", executableJob.getJobKey().toString(), String.valueOf(executeRequest.getVersion()));
        File resourceFile = path.toFile();
        if (!resourceFile.exists()) {
            InputStream fileStream = fileStorage.fetchFile(Paths.get(executeRequest.getJobPath()));
            FileUtils.copyInputStreamToFile(fileStream, resourceFile);
        }
        job.run();
        return null;
    }

    @Override
    public void stop(ExecutableJob executableJob) throws Exception {
        job.cancel();
    }

}
