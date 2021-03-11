package org.dromara.hodor.client.core;

import java.sql.SQLException;
import java.text.MessageFormat;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.common.storage.db.DBOperator;

/**
 * hodor database setup
 *
 * @author tomgs
 * @since 2021/3/11
 */
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
        //createJobExecutionTable();
    }

    /**
     * 初始化数据
     */
    public void initData() {

    }

    private void createJobExecutionTable() throws SQLException {
        String sql = buildCreateJobExecutionTableSql();
        dbOperator.createTableIfNeeded(JOB_EXECUTION_TABLE_NAME, sql);
    }

    private String buildCreateJobExecutionTableSql() {
        return MessageFormat.format("CREATE TABLE {0} {}",
            JOB_EXECUTION_TABLE_NAME);
    }

}
