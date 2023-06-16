package org.dromara.hodor.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mapper scan config
 *
 * @author tomgs
 * @since 1.0
 */
@Configuration
@MapperScan(basePackages = "org.dromara.hodor.core.mapper")
public class DataSourceConfig {

    @Value("${hodor.datasource.type:mysql}")
    private String type;

    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (DbType.MYSQL.getDb().equalsIgnoreCase(type)) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        } else if (DbType.POSTGRE_SQL.getDb().equalsIgnoreCase(type)) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        } else if (DbType.ORACLE_12C.getDb().equalsIgnoreCase(type)) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE_12C));
        }
        return interceptor;
    }

}
