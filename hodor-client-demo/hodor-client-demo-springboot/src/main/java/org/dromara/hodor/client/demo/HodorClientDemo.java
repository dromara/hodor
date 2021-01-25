package org.dromara.hodor.client.demo;

import org.dromara.hodor.client.annotation.EnableHodorScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author tomgs
 * @since 2021/1/4
 */
@SpringBootApplication
@EnableHodorScheduler
public class HodorClientDemo {

    public static void main(String[] args) {
        SpringApplication.run(HodorClientDemo.class, args);
    }

}
