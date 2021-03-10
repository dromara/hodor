package org.dromara.hodor.common.storage.db;

import javax.sql.DataSource;
import org.dromara.hodor.common.extension.SPI;

/**
 * hodor data source
 *
 * @author tomgs
 * @since 2021/3/10
 */
@SPI("datasource")
public abstract class HodorDataSource {

    private DataSource dataSource;

    private DataSourceConfig dataSourceConfig;

    public HodorDataSource() {

    }

    public HodorDataSource(final DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public abstract String getDBType();
}
