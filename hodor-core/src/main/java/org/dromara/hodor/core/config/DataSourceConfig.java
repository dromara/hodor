package org.dromara.hodor.core.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * mapper scan config
 *
 * @author tomgs
 * @since 2020/9/3
 */
@Configuration
@MapperScan(basePackages = "org.dromara.hodor.core.mapper")
public class DataSourceConfig {

}
