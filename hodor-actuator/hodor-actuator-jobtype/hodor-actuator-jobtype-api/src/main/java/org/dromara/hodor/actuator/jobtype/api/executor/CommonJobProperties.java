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

package org.dromara.hodor.actuator.jobtype.api.executor;

public class CommonJobProperties {
    /*
     * The following are Common properties that can be set in a job file
     */

    /**
     * The type of job that will be executed. Examples: command, java, etc.
     */
    public static final String JOB_TYPE = "job.type";

    /**
     * The request context.
     */
    public static final String REQUEST_CONTEXT = "request.context";

    /**
     * The job context
     */
    public static final String JOB_CONTEXT = "job.context";

    /**
     * Job key
     */
    public static final String JOB_KEY = "job.key";

    /**
     * Force a node to be a root node in a flow, even if there are other jobs dependent on it.
     */
    public static final String ROOT_NODE = "root.node";

    /**
     * Comma delimited list of job names which are dependencies
     */
    public static final String DEPENDENCIES = "dependencies";

    /**
     * The number of retries when this job has failed.
     */
    public static final String RETRIES = "retries";

    /**
     * The time in millisec to back off after every retry
     */
    public static final String RETRY_BACKOFF = "retry.backoff";

    /**
     * Comma delimited list of email addresses for both failure and success messages
     */
    public static final String NOTIFY_EMAILS = "notify.emails";

    /**
     * Comma delimited list of email addresses for success messages
     */
    public static final String SUCCESS_EMAILS = "success.emails";

    /**
     * Comma delimited list of email addresses for failure messages
     */
    public static final String FAILURE_EMAILS = "failure.emails";

    /*
     * The following are the common props that will be added to the job by actuator
     */

    /**
     * Url to access hodor actuator on a given host
     */
    public static final String ACTUATOR_URL = "hodor.actuator.url";

    /**
     * The attempt number of the executing job.
     */
    public static final String JOB_ATTEMPT = "hodor.actuator.job.attempt";

    /**
     * The attempt number of the executing job.
     */
    public static final String JOB_METADATA_FILE = "hodor.actuator.job.metadata.file";

    /**
     * The attempt number of the executing job.
     */
    public static final String JOB_ATTACHMENT_FILE =
        "hodor.actuator.job.attachment.file";

    /**
     * The executing flow id
     */
    public static final String FLOW_ID = "hodor.actuator.flow.flowid";

    /**
     * The nested flow id path
     */
    public static final String NESTED_FLOW_PATH = "hodor.actuator.flow.nested.path";

    /**
     * The executing job id
     */
    public static final String JOB_ID = "job.id";

    /**
     * The execution id. This should be unique per flow, but may not be due to restarts.
     */
    public static final String EXEC_ID = "job.execid";

    public static final String APP_ID = "job.appid";

    /**
     * The numerical project id identifier.
     */
    public static final String PROJECT_ID = "flow.projectid";

    /**
     * The project name.
     */
    public static final String PROJECT_NAME = "flow.projectname";

    /**
     * The project last modified by user.
     */
    public static final String PROJECT_LAST_CHANGED_BY = "flow.projectlastchangedby";

    /**
     * The project last modified on date.
     */
    public static final String PROJECT_LAST_CHANGED_DATE = "flow.projectlastchangeddate";

    /**
     * The version of the project the flow is running. This may change if a forced hotspot occurs.
     */
    public static final String PROJECT_VERSION = "flow.projectversion";

    /**
     * Find out who is the submit user, in addition to the user.to.proxy (they may be different)
     */
    public static final String SUBMIT_USER = "flow.submituser";

    /**
     * A uuid assigned to every execution
     */
    public static final String FLOW_UUID = "flow.uuid";

    public static final String JOB_LINK = "hodor.actuator.link.job.url";
    public static final String WORKFLOW_LINK = "hodor.actuator.link.workflow.url";
    public static final String EXECUTION_LINK = "hodor.actuator.link.execution.url";
    public static final String JOBEXEC_LINK = "hodor.actuator.link.jobexec.url";
    public static final String ATTEMPT_LINK = "hodor.actuator.link.attempt.url";
    public static final String OUT_NODES = "hodor.actuator.job.outnodes";
    public static final String IN_NODES = "hodor.actuator.job.innodes";

    public static final String JOB_CUR_STATUS = "hodor.actuator.cur.status";
    public static final String JOB_COMPLETE_TIME = "hodor.actuator.complete.time";
}
