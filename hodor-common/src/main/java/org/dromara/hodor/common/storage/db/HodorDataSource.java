package org.dromara.hodor.common.storage.db;

import org.dromara.hodor.common.extension.SPI;

import javax.sql.DataSource;

/**
 *  hodor data source
 *
 * @author tomgs
 * @version 2021/3/10 1.0 
 */
@SPI("datasource")
public interface HodorDataSource {

    String getDBType();

    DataSource getDataSource();

}
