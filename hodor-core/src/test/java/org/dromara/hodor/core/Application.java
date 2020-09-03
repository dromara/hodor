package org.dromara.hodor.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.dromara.hodor")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);

    }

}
