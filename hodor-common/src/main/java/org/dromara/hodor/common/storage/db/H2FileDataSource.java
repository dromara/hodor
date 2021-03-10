package org.dromara.hodor.common.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.dromara.hodor.common.extension.Join;

/**
 * h2 file datasource
 *
 * @author tomgs
 * @since 2021/3/9
 */
@Join
public class H2FileDataSource extends HodorDataSource {

    private static final String H2_DATASOURCE_CLASS_NAME = "org.h2.jdbcx.JdbcDataSource";

    private static final String H2_URL = String.format("jdbc:h2:file:%s/hodor-scheduler/db_hodor;IGNORECASE=TRUE", System.getProperty("user.dir"));

    public H2FileDataSource(final DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);
        HikariConfig config = new HikariConfig();
        if (dataSourceConfig == null) {
            config.setDataSourceClassName(H2_DATASOURCE_CLASS_NAME);
            config.addDataSourceProperty("URL", H2_URL);
            setDataSource(new HikariDataSource(config));
            return;
        }
        String dataSourceClassName = dataSourceConfig.getDataSourceClassName();
        config.setDataSourceClassName(dataSourceClassName == null ? H2_DATASOURCE_CLASS_NAME : dataSourceClassName);
        config.addDataSourceProperty("URL", dataSourceConfig.getUrl());
        config.addDataSourceProperty("user", dataSourceConfig.getUsername());
        config.addDataSourceProperty("password", dataSourceConfig.getPassword());
        setDataSource(new HikariDataSource(config));
    }

    @Override
    public String getDBType() {
        return "h2-file";
    }

}
