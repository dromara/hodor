package org.dromara.hodor.client.executor;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.core.HodorJobExecution;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.storage.db.DBOperator;

/**
 * Job persistence
 *
 * @author tomgs
 * @since 2021/3/18
 */
@Slf4j
public class JobPersistence extends AbstractAsyncEventPublisher<HodorJobExecution> {

    private final DBOperator dbOperator;

    private final static Integer INSERT_EVENT = 0;

    private final static Integer UPDATE_EVENT = 1;

    public JobPersistence(final DBOperator dbOperator) {
        this.dbOperator = dbOperator;
    }

    @Override
    public void registerListener() {
        insertHodorJobExecutionListener();
        updateHodorJobExecutionListener();
    }

    private void insertHodorJobExecutionListener() {
        this.addListener(e -> {
            HodorJobExecution jobExecution = e.getValue();
            try {
                dbOperator.update(insertJobExecution, jobExecution.getRequestId(), jobExecution.getGroupName(), jobExecution.getJobName(),
                    jobExecution.getParameters(), jobExecution.getSchedulerTag(), jobExecution.getClientHostname(), jobExecution.getClientIp(),
                    jobExecution.getStartTime(), jobExecution.getStatus());
            } catch (SQLException ex) {
                log.error("insert job execution message exception, {}", ex.getMessage(), ex);
            }
        }, INSERT_EVENT);
    }

    private void updateHodorJobExecutionListener() {
        this.addListener(e -> {
            HodorJobExecution jobExecution = e.getValue();
            try {
                dbOperator.update(updateJobExecution, jobExecution.getCompleteTime(), jobExecution.getStatus(),
                    jobExecution.getComments(), jobExecution.getResult(), jobExecution.getRequestId());
            } catch (SQLException ex) {
                log.error("update job execution message exception, {}", ex.getMessage(), ex);
            }
        }, UPDATE_EVENT);
    }

    public void fireJobExecutionEvent(HodorJobExecution jobExecution) {
        Integer eventType = INSERT_EVENT;
        if (jobExecution.getStatus() != JobExecuteStatus.RUNNING) {
            eventType = UPDATE_EVENT;
        }
        Event<HodorJobExecution> event = new Event<>(jobExecution, eventType);
        this.publish(event);
    }

    public HodorJobExecution fetchJobExecution(Long requestId) throws SQLException {
        return dbOperator.query(selectJobExecution, HodorJobExecution.class, requestId);
    }

    final String insertJobExecution =
        "INSERT INTO hodor_job_execution (request_id, group_name, job_name, parameters, scheduler_tag, client_hostname, client_ip, start_time, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    final String updateJobExecution =
        "UPDATE hodor_job_execution SET complete_time = ?, status = ?, comments = ?, result = ? WHERE request_id = ?";

    final String selectJobExecution = "SELECT * FROM hodor_job_execution WHERE request_id = ?";
}
