package org.dromara.hodor.common.storage.db;

import java.util.Properties;

/**
 * @author tomgs
 * @since 2021/3/10
 */
public class DataSourceConfig {

    private String type;

    private Properties dataSourceProperties;

    private String url;

    private String dataSourceClassName;

    private String username;

    private String password;

    public DataSourceConfig() {
        this.dataSourceProperties = new Properties();;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setDataSourceProperties(Properties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public void addDataSourceProperty(String propertyName, Object value) {
        dataSourceProperties.put(propertyName, value);
    }

}
