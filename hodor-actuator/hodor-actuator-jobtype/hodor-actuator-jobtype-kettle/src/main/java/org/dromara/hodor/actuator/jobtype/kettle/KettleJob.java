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

package org.dromara.hodor.actuator.jobtype.kettle;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.utils.Props;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.Repository;

/**
 * KettleJob
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleJob extends AbstractKettleJob<Job> {

    private final Props jobProps;

    private final Logger log;

    private Job kettleJob;

    public KettleJob(String jobId, Props sysProps, Props jobProps, Logger log) {
        super(jobId, sysProps, jobProps, log);
        this.jobProps = jobProps;
        this.log = log;
    }

    @Override
    public Job buildKettleExecutor(Repository repository, String path, String jobName, LogLevel logLevel) throws Exception {
        // 3、执行job
        kettleJob = KettleProcess.createKettleJob(repository, path, jobName,
            jobProps.getMapByPrefix("kettle.params"),
            jobProps.getMapByPrefix("kettle.variables"));
        kettleJob.setLogLevel(logLevel);
        return kettleJob;
    }

    @Override
    public int execute(Job job, int timeout) {
        kettleJob.start();
        kettleJob.waitUntilFinished(timeout);
        return kettleJob.getErrors();
    }

    @Override
    public void cancel() {
        if (kettleJob != null) {
            log.info("KettleJob stop");
            kettleJob.stopAll();
        }
    }
}
