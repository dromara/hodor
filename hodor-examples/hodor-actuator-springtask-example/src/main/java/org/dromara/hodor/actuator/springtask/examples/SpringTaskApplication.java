package org.dromara.hodor.actuator.springtask.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTaskApplication.class, args);
    }

}
