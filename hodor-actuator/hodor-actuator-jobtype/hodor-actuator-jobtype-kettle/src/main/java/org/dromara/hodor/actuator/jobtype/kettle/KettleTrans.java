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
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;

/**
 * KettleTrans
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleTrans extends AbstractKettleJob<Trans> {

    private final Props jobProps;

    private final Logger log;

    private Trans kettleTrans;

    public KettleTrans(String jobId, Props sysProps, Props jobProps, Logger log) {
        super(jobId, sysProps, jobProps, log);
        this.jobProps = jobProps;
        this.log = log;
    }

    @Override
    public Trans buildKettleExecutor(Repository repository, String path, String jobName, LogLevel logLevel) throws Exception {
        kettleTrans = KettleProcess.createKettleTrans(repository, path, jobName,
            jobProps.getMapByPrefix("kettle.params"),
            jobProps.getMapByPrefix("kettle.variables"));
        // 设置日志级别
        kettleTrans.setLogLevel(logLevel);
        return kettleTrans;
    }

    @Override
    public int execute(Trans trans, int timeout) throws KettleException {
        kettleTrans.execute(null);
        kettleTrans.waitUntilFinished();
        return kettleTrans.getErrors();
    }

    @Override
    public void cancel() {
        if (kettleTrans != null) {
            log.info("KettleTrans stop");
            kettleTrans.stopAll();
        }
    }
}
