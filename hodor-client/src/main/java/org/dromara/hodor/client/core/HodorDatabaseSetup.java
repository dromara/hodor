package org.dromara.hodor.client.core;

import java.sql.SQLException;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.common.storage.db.DBOperator;

/**
 * hodor database setup
 *
 * @author tomgs
 * @since 2021/3/11
 */
@Slf4j
public class HodorDatabaseSetup {

    private final DBOperator dbOperator;

    private static final String JOB_EXECUTION_TABLE_NAME = "hodor_job_execution";

    public HodorDatabaseSetup() {
        this.dbOperator = ServiceProvider.getInstance().getBean(DBOperator.class);
    }

    /**
     * 初始化表
     */
    public void initTables() throws SQLException {
        createJobExecutionTable();
    }

    /**
     * 初始化数据
     */
    public void initData() {

    }

    private void createJobExecutionTable() throws SQLException {
        String tableSql = buildCreateJobExecutionTableSql();
        String indexSql = buildJobExecutionTableIndex();

        log.info("create table sql: {}", tableSql);
        log.info("create index sql: {}", indexSql);

        if (!dbOperator.createTableIfNeeded(JOB_EXECUTION_TABLE_NAME, tableSql)) {
            // create index
            dbOperator.update(indexSql);
        }
    }

    private String buildCreateJobExecutionTableSql() {
        return MessageFormat.format("CREATE TABLE {0} " +
                "(\n" +
                "\trequest_id BIGINT NOT NULL,\n" +
                "\tgroup_name VARCHAR2 NOT NULL,\n" +
                "\tjob_name VARCHAR2 NOT NULL,\n" +
                "\tparameters VARCHAR2,\n" +
                "\tscheduler_tag VARCHAR2,\n" +
                "\tclient_hostname VARCHAR2 NOT NULL,\n" +
                "\tclient_ip VARCHAR2 NOT NULL,\n" +
                "\tstart_time TIMESTAMP NOT NULL,\n" +
                "\tcomplete_time TIMESTAMP,\n" +
                "\tstatus INT NOT NULL,\n" +
                "\tcomments VARCHAR2,\n" +
                "\tresult VARCHAR2,\n" +
                "\tCONSTRAINT {0}_pk PRIMARY KEY (request_id)\n" +
                ");",
            JOB_EXECUTION_TABLE_NAME);
    }

    private String buildJobExecutionTableIndex() {
        return MessageFormat.format("CREATE UNIQUE INDEX {0}_request_id_uindex ON {0} (request_id);",
            JOB_EXECUTION_TABLE_NAME);
    }

}
