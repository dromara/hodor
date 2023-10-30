/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.hodor.actuator.api.core;

import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.storage.db.HodorDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * DbOperatorTest
 *
 * @author tomgs
 * @since 1.0
 */
public class DbOperatorTest {

    final String selectJobExecution = "SELECT * FROM hodor_job_execution WHERE request_id = ?";

    @Test
    public void testQuery() throws SQLException {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setType("h2");
        dataSourceConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        dataSourceConfig.setUrl("jdbc:h2:file:E:\\data\\hodor-actuator\\springtask-actuator\\db\\db_hodor;IGNORECASE=TRUE;AUTO_SERVER=TRUE");
        dataSourceConfig.setUsername("test");
        dataSourceConfig.setPassword("test");
        HodorDataSource datasource = ExtensionLoader.getExtensionLoader(HodorDataSource.class, DataSourceConfig.class)
            .getProtoJoin(dataSourceConfig.getType(), dataSourceConfig);
        DBOperator dbOperator = new DBOperator(datasource.getDataSource());
        Long requestId = 1718890855266344960L;
        final HodorJobExecution result = dbOperator.query(selectJobExecution, HodorJobExecution.class, requestId);
        System.out.println(result);
    }

}
