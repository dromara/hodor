package org.dromara.hodor.core.config;

import org.mybatis.spring.annotation.MapperScan;
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

}
