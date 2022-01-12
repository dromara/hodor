/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.dromara.hodor.actuator.bigdata.executor;

import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.core.ExecuteContext;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.utils.Props;

/**
 * A no-op job.
 */
public class NoopJob implements Job {

  private final String jobId;

  public NoopJob(final String jobid) {
    jobId = jobid;
  }

  public NoopJob(final String jobid, final Props props, final Props jobProps, final Logger log) {
    this.jobId = jobid;
  }

  @Override
  public String getId() {
    return this.jobId;
  }

  @Override
  public void beforeExecute(ExecuteContext context) {

  }

  @Override
  public void execute(ExecuteContext context) throws JobExecutionException {

  }

  @Override
  public void executeSuccess(ExecuteContext context) {

  }

  @Override
  public void executeFail(ExecuteContext context) {

  }

  /*@Override
  public void run() throws Exception {
  }*/

  @Override
  public void cancel() throws Exception {
  }

  @Override
  public double getProgress() throws Exception {
    return 0;
  }

  @Override
  public Props getJobGeneratedProperties() {
    return new Props();
  }

  @Override
  public boolean isCanceled() {
    return false;
  }
}
