package org.dromara.hodor.server.restservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 2021/1/28
 */
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @GetMapping("/isAlive")
    public Mono<String> isAlive() {
        return Mono.just("alive");
    }

    @PostMapping("/createJob")
    public Mono<Void> createJob() {
        return Mono.empty();
    }

}
