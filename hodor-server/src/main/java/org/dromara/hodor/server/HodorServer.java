package org.dromara.hodor.server;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  hodor server
 *
 * @author tomgs
 * @version 2020/6/24 1.0 
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "org.dromara.hodor")
@MapperScan(basePackages = "org.dromara.hodor.core.mapper")
public class HodorServer {

    public static void main(String[] args) {
        SpringApplication.run(HodorServer.class, args);
    }

}
