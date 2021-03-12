package org.dromara.hodor.common.storage.db;

import javax.sql.DataSource;

import static java.util.Objects.requireNonNull;

/**
 * hodor data source
 *
 * @author tomgs
 * @since 2021/3/10
 */
public abstract class AbstractHodorDataSource implements HodorDataSource {

    private DataSource dataSource;

    private DataSourceConfig dataSourceConfig;

    public AbstractHodorDataSource(final DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

}
