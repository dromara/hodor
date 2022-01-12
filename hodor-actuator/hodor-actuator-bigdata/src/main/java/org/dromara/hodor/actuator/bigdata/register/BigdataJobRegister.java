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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.dromara.hodor.actuator.common.JobRegister;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.core.JobInstance;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * BigdataJobRegister
 *
 * @author tomgs
 * @since 2021/12/28
 */
public class BigdataJobRegister implements JobRegister {

    @Override
    public Set<String> supportedGroupNames() {
        return null;
    }

    @Override
    public List<JobDesc> registerJobs() throws Exception {
        return new ArrayList<>();
    }

    @Override
    public void registerJob(JobInstance jobInstance) {

    }

    @Override
    public JobRunnable getRunnableJob(JobExecuteRequest request) {
        String jobCommandType = request.getJobCommandType();

        return null;
    }

    @Override
    public void clear() {

    }

}
