package com.dromara.hodor.server;

import javax.annotation.PostConstruct;
import org.dromara.hodor.remoting.api.NetServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  hodor server
 *
 * @author tomgs
 * @version 2020/6/24 1.0 
 */
@SpringBootApplication
public class HodorServer {

    public static void main(String[] args) {
        SpringApplication.run(HodorServer.class, args);
    }

    @PostConstruct
    public void run() {
        // start hodor server
        // start remoting server
        //NetServer netServer =
        // register service
    }

}
