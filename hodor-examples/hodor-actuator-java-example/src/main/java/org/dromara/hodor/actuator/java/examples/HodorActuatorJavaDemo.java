package org.dromara.hodor.actuator.java.examples;

import org.dromara.hodor.actuator.java.annotation.EnableHodorScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author tomgs
 * @since 1.0
 */
@SpringBootApplication
@EnableHodorScheduler
public class HodorActuatorJavaDemo {

    public static void main(String[] args) {
        SpringApplication.run(HodorActuatorJavaDemo.class, args);
    }

}
