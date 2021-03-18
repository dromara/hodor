package org.dromara.hodor.client.executor;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.HodorJobExecution;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.storage.db.DBOperator;

/**
 * Job persistence event publisher
 *
 * @author tomgs
 * @since 2021/3/18
 */
@Slf4j
public class JobPersistenceEventPublisher extends AbstractAsyncEventPublisher<HodorJobExecution> {

    private final DBOperator dbOperator;

    public JobPersistenceEventPublisher(final DBOperator dbOperator) {
        this.dbOperator = dbOperator;
    }

    @Override
    public void registerListener() {
        registerInsertHodorJobExecution();
        registerUpdateHodorJobExecution();
    }

    private void registerInsertHodorJobExecution() {
        this.addListener(e -> {
            HodorJobExecution jobExecution = e.getValue();
            try {
                dbOperator.update(insertJobExecution, jobExecution);
            } catch (SQLException ex) {
                log.error("insert job execution message exception, {}", ex.getMessage(), ex);
            }
        }, "INSERT");
    }

    private void registerUpdateHodorJobExecution() {
        this.addListener(e -> {
            HodorJobExecution jobExecution = e.getValue();
            try {
                dbOperator.update(updateJobExecution, jobExecution);
            } catch (SQLException ex) {
                log.error("update job execution message exception, {}", ex.getMessage(), ex);
            }
        }, "UPDATE");
    }

    public void notifyJobExecutionEvent(HodorJobExecution jobExecution) {
        Event<HodorJobExecution> event = new Event<>(jobExecution, jobExecution.getStatus());
        this.publish(event);
    }

    final String insertJobExecution =
        "INSERT INTO hodor_job_execution (request_id, group_name, job_name, parameters, scheduler_tag, client_hostname, client_ip, " +
            "start_time, complete_time, status, comments, result) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    final String updateJobExecution =
        "UPDATE hodor_job_execution SET complete_time=?, status=?, comments=?, result=? " +
            "WHERE request_id=?";
}
