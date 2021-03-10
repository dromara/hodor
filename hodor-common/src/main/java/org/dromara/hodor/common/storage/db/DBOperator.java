package org.dromara.hodor.common.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.dromara.hodor.common.extension.ExtensionLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * db operator
 *
 * @author tomgs
 * @since 2021/3/9
 */
@Slf4j
public class DBOperator {

  private final QueryRunner queryRunner;

  private final DataSource dataSource;

  public DBOperator() {
    HodorDataSource hodorDataSource = ExtensionLoader.getExtensionLoader(HodorDataSource.class).getProtoJoin("datasource");
    requireNonNull(hodorDataSource, "data source must not be null.");
    this.dataSource = hodorDataSource.getDataSource();
    this.queryRunner = new QueryRunner(dataSource);
  }

  public DBOperator(DataSource dataSource) {
    requireNonNull(dataSource, "data source must not be null.");
    this.dataSource = dataSource;
    this.queryRunner = new QueryRunner(dataSource);
  }

  public void createTableIfNeeded(String tableName, String sql) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      try (ResultSet resultSet = connection.getMetaData()
          .getTables(null, null, tableName, new String[]{"TABLE"})) {
        if (!resultSet.next()) {
          queryRunner.update(connection, sql);
        }
      }
    }
  }

  public <T> T query(String querySql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
    return queryRunner.query(querySql, rsh, params);
  }

  public <T> T query(String querySql, Class<T> clazz, Object... params) throws SQLException {
    T result = queryRunner.query(querySql, new BeanHandler<>(clazz), params);

    log.info("query result: {}", result);

    return result;
  }

  public <T> List<T> queryList(String querySql, Class<T> clazz, Object... params) throws SQLException {
    List<T> result = queryRunner.query(querySql, new BeanListHandler<>(clazz), params);

    log.info("query list result: {}", result);

    return result;
  }

  public int update(String updateSql, Object... params) throws SQLException {
    int update = queryRunner.update(updateSql, params);

    log.info("update result: {}", update);

    return update;
  }

}
