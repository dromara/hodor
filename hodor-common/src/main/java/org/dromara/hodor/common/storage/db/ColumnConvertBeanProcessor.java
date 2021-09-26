package org.dromara.hodor.common.storage.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.GenerousBeanProcessor;

/**
 * ColumnConvertBeanProcessor
 *
 * @author tomgs
 * @since 2021/8/4
 */
public class ColumnConvertBeanProcessor extends GenerousBeanProcessor {

    @Override
    protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
        if (byte[].class.isAssignableFrom(propType)
            || Byte[].class.isAssignableFrom(propType)) {
            return rs.getBytes(index);
        }
        return super.processColumn(rs, index, propType);
    }

}
