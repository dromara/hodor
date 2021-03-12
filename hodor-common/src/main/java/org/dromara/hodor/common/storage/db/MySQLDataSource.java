package org.dromara.hodor.common.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;
import org.dromara.hodor.common.extension.Join;

import static java.util.Objects.requireNonNull;

/**
 * mysql datasource
 *
 * @author tomgs
 * @since 2021/3/12
 */
@Join
public class MySQLDataSource extends AbstractHodorDataSource {

    public MySQLDataSource(DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);

        requireNonNull(dataSourceConfig, "data source config must not be null.");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceConfig.getUrl());
        config.setUsername(dataSourceConfig.getUsername());
        config.setPassword(dataSourceConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // 设置扩展参数，这个方法设置的属性为HikariConfig中的setXXX中的xXX，如setMinimumIdle，那么配置属性的时候为minimumIdle
        PropertyElf.setTargetFromProperties(config, dataSourceConfig.getDataSourceProperties());

        this.setDataSource(new HikariDataSource(config));
    }

    @Override
    public String getDBType() {
        return "mysql";
    }

}
