package org.dromara.hodor.common.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.utils.StringUtils;

import static java.util.Objects.requireNonNull;

/**
 * h2 file datasource
 *
 * @author tomgs
 * @since 1.0
 */
@Join
public class H2FileDataSource extends AbstractHodorDataSource {

    private static final String H2_DATASOURCE_CLASS_NAME = "org.h2.jdbcx.JdbcDataSource";

    private static final String H2_URL = String.format("jdbc:h2:file:%s/hodorScheduler/db_hodor;IGNORECASE=TRUE", System.getProperty("user.dir"));

    public H2FileDataSource(final DataSourceConfig dataSourceConfig) {
        super(dataSourceConfig);
        HikariConfig config = new HikariConfig();
        if (dataSourceConfig == null) {
            config.setDataSourceClassName(H2_DATASOURCE_CLASS_NAME);
            config.addDataSourceProperty("URL", H2_URL);
            this.setDataSource(new HikariDataSource(config));
            return;
        }

        String dataSourceClassName = dataSourceConfig.getDataSourceClassName();
        String url = dataSourceConfig.getUrl();

        requireNonNull(url, "data source url must not be null.");

        config.setDataSourceClassName(dataSourceClassName == null ? H2_DATASOURCE_CLASS_NAME : dataSourceClassName);
        config.addDataSourceProperty("URL", url);
        // 设置扩展参数，这个方法设置的属性为HikariConfig中的setXXX中的xXX，如setMinimumIdle，那么配置属性的时候为minimumIdle
        PropertyElf.setTargetFromProperties(config, dataSourceConfig.getDataSourceProperties());

        String username = dataSourceConfig.getUsername();
        String password = dataSourceConfig.getPassword();

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            config.setUsername(username);
            config.setPassword(password);
        }

        this.setDataSource(new HikariDataSource(config));
    }

    @Override
    public String getDBType() {
        return "h2-file";
    }

}
