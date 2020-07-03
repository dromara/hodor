package com.dromara.hodor.server.service;

import com.dromara.hodor.server.component.LifecycleComponent;

/**
 * hodor service
 *
 * @author tomgs
 * @since 2020/6/29
 */
public class HodorService implements LifecycleComponent {

    private LeaderService leaderService;

    public HodorService(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    @Override
    public void start() {
        //init data

        //select leader
        leaderService.electLeader(() -> {
            // to be leader after logic write here
            System.out.println("---------");
        });
        //job assign
    }

    @Override
    public void stop() {

    }

}
